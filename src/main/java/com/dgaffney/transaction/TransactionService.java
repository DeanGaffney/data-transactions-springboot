package com.dgaffney.transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

@Service
public class TransactionService {
    
    ReadWriteLock rwLock = new ReentrantReadWriteLock();

    /**
     * Persists transactions to a file, this function is thread safe due to
     * interaction with the single transaction file. This function uses a write lock
     * to only allow one writer to write to the file at a time
     *
     * @param transactions the transactions to persist
     * @return the transaction result object
     */
    TransactionResult persistTransactions(Transactions transactions) {
        TransactionResult transactionResult = new TransactionResult(0, 0, "");

        try {
            File transactionFile = getTransactionsFile();

            String transactionsFileName = transactionFile.getName();
            File transactionCopyFile = getTransactionsCopyFile();


            // lock the file to a single writer
            rwLock.writeLock().lock();
            try {
                // write to the file and apply updates if needed
                transactionResult = updateAndWrite(transactionFile, transactionCopyFile, transactions);
            } finally {
                // release the write lock
                rwLock.writeLock().unlock();
            }
            // delete the original transactions file and rename the copy to be the original
            // file name
            if (!deleteTransactionsFile(transactionFile)) {
                transactionResult.setMessage("Error occurred deleting");
            }

            if (!renameFile(transactionCopyFile, transactionsFileName)) {
                transactionResult.setMessage("Error occurred renaming");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return transactionResult;
    }

    /**
     * Writes transactions to the file, updates are carried out on transactions before
     * writing them out to the csv file
     *
     * @param transactionFile the transaction file to read from
     * @param transactionsCopyFile the transaction copy file to write to
     * @param transactions the transactions to update and write to the copy file
     * @return the transaction result
     */
    TransactionResult updateAndWrite(File transactionFile, File transactionsCopyFile, Transactions transactions){
        TransactionResult transactionResult = new TransactionResult(0, 0, "No operations occurred");
        // try with resources on stream and writer
        try(Stream<String> fileStream = Files.lines(transactionFile.toPath()); BufferedWriter writer = new BufferedWriter(new FileWriter(transactionsCopyFile))){
            // read from original transactions the file line by line to keep memory consumption down
            fileStream.forEach (line -> {
                if(!line.isEmpty()){
                    // create a transaction from the line
                    Transaction transactionFromFile = createTransactionFromLine(line);
                    // update any matching transactions
                    updateMatchingTransactions(transactions, transactionFromFile);
                    
                    // write the transaction out to the copy file
                    appendToTransactionsCopyFile(writer, transactionFromFile);
                }
            });
            // filter out existing transactions to get the new transactions and write them to the file
            List<Transaction> transactionsToCreate = filterExistingTransactions(transactions);
            transactionsToCreate.forEach(tran -> appendToTransactionsCopyFile(writer, tran) );
            transactionResult = new TransactionResult(transactionsToCreate.size(), transactions.getEntries().size() - transactionsToCreate.size(), "Transactions Stored");
        }catch(Exception e){
            e.printStackTrace();
        }
        return transactionResult;
    }

    /**
     * Update matching transactions that match the given transaction
     * by summing their amounts together and marking the client transaction
     * as having existed previously in the transaction file
     *
     * @param transactionsFromClient the transactions from the client
     * @param transactionToMatch the transaction to compare against the client transactions
     */
    void updateMatchingTransactions(Transactions transactionsFromClient, Transaction transactionToMatch){
        // with each line compare it against each transaction to see if there are duplicates
        transactionsFromClient.getEntries().forEach(transaction ->{
            if(transaction.equals(transactionToMatch)){
                // if the transactions are the same sum the transactions
                transactionToMatch.sumTransactions(transaction);
                transaction.setExisted(true);
            }
        });
    }

    /**
     * Gets a csv file from the users temp directory
     * with the supplied name
     *
     * @param name the name of the file to look for
     * @return the file
     */
    File getTempDirFile(String name){
        return new File(System.getProperty("java.io.tmpdir"), name + ".csv");
    }

    /**
     * Creates a temp csv file which will be deleted
     * when the JVM is terminated. Should be used
     * for creating the transaction files
     *
     * @param name the name to use for the file
     * @return the temp file
     */
    File createTempCsvFile(String name) throws Exception {
        File tempCsv = getTempDirFile(name);
        tempCsv.createNewFile();
        tempCsv.setReadable(true);
        tempCsv.setWritable(true);
        return tempCsv;
    }

    /**
     * Gets a temp csv file by checking to see if the file
     * already exists in the temp directory. If it does the file
     * is returned, otherwise the file is created as a temp file
     * marked for deletion and returned
     *
     * @param name the name of the file to check for
     * @return the temp csv file
     */
    File getTempCsvFile(String name) throws Exception {
        File file = getTempDirFile(name);
        if(!file.exists()){
            file = createTempCsvFile(name);
        }
        return file;
    }

    /**
     * Gets the transaction file which is created
     * or retrieved depending on if it already exists
     *
     * @see #createTempCsvFile(java.lang.String)
     * @return the transaction file
     */
    File getTransactionsFile() throws Exception {
        return getTempCsvFile("transactions");
    }

    /**
     * Gets the transaction copy file which is created
     * or retrieved depending on if it already exists
     *
     * @see #createTempCsvFile(java.lang.String)
     * @return the transaction copy file
     */
    File getTransactionsCopyFile() throws Exception {
        return getTempCsvFile("transactions-copy");
    }

    /**
     * Deletes a transaction file
     *
     * @param transactionFile the file to delete
     * @return true if the file got deleted, false otherwise
     */
    boolean deleteTransactionsFile(File transactionFile){
        return transactionFile.delete();
    }

    /**
     * Renames the given file with the supplied name
     *
     * @param transactionFile the file to rename
     * @param newName the new name for the file
     * @return true if the file was renamed, false otherwise
     */
    boolean renameFile(File transactionFile, String newName){
        boolean renamed;
        try {
            Path copyPath = Files.move(transactionFile.toPath(), transactionFile.toPath().resolveSibling(newName));
            renamed = copyPath.toFile().exists();
        } catch (Exception e) {
            e.printStackTrace();
            renamed = false;
        }
        return renamed;
    }

    /**
     * Creates a transaction object from a line in the transaction file
     *
     * @param line the line to use for creating the transaction object
     * @return the transaction object created from the line
     */
    Transaction createTransactionFromLine(String line){
        String[] attributes = line.split(",");
        return new Transaction(attributes[0], attributes[1], attributes[2]);
    }

    /**
     * Gets all the transactions which did not previously exist
     *
     * @param transactions the transaction object to filter from
     * @return a list of transactions which can be considered new transactions
     */
    List<Transaction> filterExistingTransactions(Transactions transactions){
        return transactions.getEntries().stream()
                                        .filter(t -> !t.isExisted())
                                        .collect(Collectors.toList());
    }

    /**
     * Append a transaction to the transaction copy file
     *
     * @param transactionsCopyFile the transaction copy file
     * @param transaction the transaction to append to the file
     */
    void appendToTransactionsCopyFile(BufferedWriter writer, Transaction transaction) {
        try {
            writer.write(transaction.toCsv());
            writer.newLine();
            writer.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Gets transactions from the file using the transaction query object
     *
     * @param transactionQuery the query params to use as a filter
     * @return the transactions matching the params
     */
    Transactions getTransactions(TransactionQuery transactionQuery) throws Exception {
        Transactions transactions = new Transactions();
        File transactionFile = getTransactionsFile();

        // get all transactions limited to the supplied or default limit and matching the supplied filters
        transactions.setEntries(filterTransactions(transactionFile, transactionQuery));
        return transactions;
    }

    /**
     * Filters transactions within the supplied transaction file using the
     * supplied transaction filter object and returns the transactions as a list
     * this function uses read locks to allow concurrent readers to read the file
     *
     * @param transactionFile the transaction file to read from
     * @param transactionFilter the transaction filter to filter transactions
     * @return a list of filtered transactions form the transaction file
     */
    List<Transaction> filterTransactions(File transactionFile, TransactionQuery query) throws Exception{
        List<Transaction> entries = Collections.emptyList();
        // create the transaction filter
        TransactionFilter transactionFilter = new TransactionFilter(query.getDate(), query.getType());

        Stream<Transaction> streamLines = Files.lines(transactionFile.toPath())   // use stream here to lazily fetch data
                                          .limit(query.getLimit())   // limit the number of lines processed
                                          .map(line -> createTransactionFromLine(line)) //map to transaction object
                                          .filter(tran -> tran.getDate().matches(transactionFilter.getDateFilter())) // see if date matches filter regex
                                          .filter(tran -> tran.getType().matches(transactionFilter.getTypeFilter())); // see if type matches filter regex

        // lock the file to allow concurrent readers
        rwLock.readLock().lock();
        try{
            // read access to the file occurs here on the stream as collect is a terminal operation
            entries = streamLines.collect(Collectors.toList());
            // close off the stream to free up the file
            streamLines.close();
        }finally{
            // release the read lock
            rwLock.readLock().unlock();
        }
        return entries;
    }
}