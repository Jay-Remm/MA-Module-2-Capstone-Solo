package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfer {

    private int id;
    private String type;
    private String status;
    private String accountFrom;
    private int accountFromId;
    private String accountTo;
    private int accountToId;
    private BigDecimal amount;

    // change the data types to what i want in my return values, need to make sure my SQL query returns readable data types not the IDs

    public Transfer() { }

    public Transfer(int id, String type, String status, String accountFrom, int accountFromId, String accountTo, int accountToId, BigDecimal amount) {
        this.id = id;
        this.type = type;
        this.status = status;
        this.accountFrom = accountFrom;
        this.accountFromId = accountFromId;
        this.accountTo = accountTo;
        this.accountToId = accountToId;
        this.amount = amount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAccountFrom() {
        return accountFrom;
    }

    public void setAccountFrom(String accountFrom) {
        this.accountFrom = accountFrom;
    }

    public int getAccountFromId() {
        return accountFromId;
    }

    public void setAccountFromId(int accountFromId) {
        this.accountFromId = accountFromId;
    }

    public String getAccountTo() {
        return accountTo;
    }

    public void setAccountTo(String accountTo) {
        this.accountTo = accountTo;
    }

    public int getAccountToId() {
        return accountToId;
    }

    public void setAccountToId(int accountToId) {
        this.accountToId = accountToId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", type='" + type + '\'' +
                ", status='" + status + '\'' +
                ", accountFrom='" + accountFrom + '\'' +
                ", accountFromId=" + accountFromId +
                ", accountTo='" + accountTo + '\'' +
                ", accountToId=" + accountToId +
                ", amount=" + amount +
                '}';
    }
}
