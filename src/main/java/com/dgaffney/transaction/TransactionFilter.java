package com.dgaffney.transaction;

public class TransactionFilter {
    private String dateFilter;
    private String typeFilter;

    public TransactionFilter(String dateFilter, String typeFilter) {
        this.dateFilter = dateFilter;
        this.typeFilter = typeFilter;
    }

    /**
     * @return String return the dateFilter
     */
    public String getDateFilter() {
        return dateFilter;
    }

    /**
     * @param dateFilter the dateFilter to set
     */
    public void setDateFilter(String dateFilter) {
        this.dateFilter = dateFilter;
    }

    /**
     * @return String return the typeFilter
     */
    public String getTypeFilter() {
        return typeFilter;
    }

    /**
     * @param typeFilter the typeFilter to set
     */
    public void setTypeFilter(String typeFilter) {
        this.typeFilter = typeFilter;
    }

}