package com.dgaffney.transaction;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransactionController {

    @Autowired
    TransactionService transactionService;

    @PostMapping(value = "/transaction")
    public TransactionResult persistTransactions(@Valid @RequestBody Transactions transactions, BindingResult bindingResult) {
        return transactionService.persistTransactions(transactions);
    }

    @GetMapping(value = "/transaction")
    public Transactions getMethodName(TransactionQuery transactionQuery) throws Exception {
        return transactionService.getTransactions(transactionQuery);
    }

}