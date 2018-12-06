package com.dgaffney.transaction;

import java.util.Collections;
import java.util.List;

import javax.validation.Valid;

public class Transactions {

    @Valid
    private List<Transaction> entries;    

    public Transactions() { 
        this.entries = Collections.emptyList();
    }

    /**
     * @return List<Transaction> return the entries
     */
    public List<Transaction> getEntries() {
        return entries;
    }

    /**
     * @param entries the entries to set
     */
    public void setEntries(List<Transaction> entries) {
        this.entries = entries;
    }

}