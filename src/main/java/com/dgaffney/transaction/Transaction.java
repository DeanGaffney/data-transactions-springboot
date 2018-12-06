package com.dgaffney.transaction;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.lang.Nullable;

public class Transaction {

    @NotEmpty
    @NotNull
    @NotBlank
    @Pattern(regexp = "^\\d{2}-\\d{2}-\\d{4}$")
    private String date;

    @NotEmpty
    @NotNull
    @NotBlank
    private String type;

    @NotEmpty
    @NotNull
    @NotBlank
    @Pattern(regexp = "^-?\\d+\\.\\d{2}$")
    private String amount;

    @Nullable
    private boolean existed;

    public Transaction() {
        
    }

    public Transaction(String date, String type, String amount) {
        this.date = date;
        this.type = type;
        this.amount = amount;
    }


    public void sumTransactions(Transaction transaction){
        float sum = Float.parseFloat(this.amount) + Float.parseFloat(transaction.getAmount());
        this.amount = String.format("%.2f", sum);
    }

    /**
     * Converts a transaction to csv format
     *
     * @return a csv representation of the transaction object
     */
    public String toCsv(){
        return String.format("%s,%s,%s", this.getDate(), this.getType(), this.getAmount());
    }


    /**
     * Consider objects equal if they have the same date and type attributes
     */
    @Override
    public boolean equals(Object obj) {
        return this.getDate().equals(((Transaction)obj).getDate()) &&
               this.getType().equals(((Transaction)obj).getType());
    }

    /**
     * @return String return the date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * @return String return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return String return the amount
     */
    public String getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(String amount) {
        this.amount = amount;
    }

    /**
     * @return boolean return the existed
     */
    public boolean isExisted() {
        return existed;
    }

    /**
     * @param existed the existed to set
     */
    public void setExisted(boolean existed) {
        this.existed = existed;
    }

}