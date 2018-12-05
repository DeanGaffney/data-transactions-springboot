package com.dgaffney.transaction;

public class TransactionQuery {

    // constant for setting a default max limit on entries returned
    public static final int DEFAULT_MAX_LIMIT = 100;

    // constant for matching everything with a regex
    public static final String DEFAULT_MATCH_ALL = ".*";

    private String date = DEFAULT_MATCH_ALL;     // filter by transactions with a given date
    private String type=DEFAULT_MATCH_ALL;      // filter by transactions with a specific type
    private int limit = DEFAULT_MAX_LIMIT;      // the number of transactions to return in the response

    public TransactionQuery() {
       
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
     * @return int return the limit
     */
    public int getLimit() {
        return limit;
    }

    /**
     * @param limit the limit to set
     */
    public void setLimit(int limit) {
        this.limit = limit;
    }

}