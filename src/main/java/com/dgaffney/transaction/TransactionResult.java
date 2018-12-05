package com.dgaffney.transaction;

public class TransactionResult {

    private int created;
    private int updated;
    private String message;

    public TransactionResult(int created, int updated, String message){
        this.created = created;
        this.updated = updated;
        this.message = message;
    }

    /**
     * @return int return the created
     */
    public int getCreated() {
        return created;
    }

    /**
     * @param created the created to set
     */
    public void setCreated(int created) {
        this.created = created;
    }

    /**
     * @return int return the updated
     */
    public int getUpdated() {
        return updated;
    }

    /**
     * @param updated the updated to set
     */
    public void setUpdated(int updated) {
        this.updated = updated;
    }

    /**
     * @return String return the message
     */
    public String getMessage() {
        return message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

}