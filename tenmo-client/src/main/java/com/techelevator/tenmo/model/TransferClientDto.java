package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class TransferClientDto {

    private int userIdTo;
    private int userIdFrom;
    private BigDecimal amount;
    private int transferType;
    private int transferStatus;

    public TransferClientDto() { }

    public TransferClientDto(int userIdTo, int userIdFrom, BigDecimal amount, int transferType, int transferStatus) {
        this.userIdTo = userIdTo;
        this.userIdFrom = userIdFrom;
        this.amount = amount;
        this.transferType = transferType;
        this.transferStatus = transferStatus;
    }

    public int getUserIdTo() {
        return userIdTo;
    }

    public void setUserIdTo(int userIdTo) {
        this.userIdTo = userIdTo;
    }

    public int getUserIdFrom() {
        return userIdFrom;
    }

    public void setUserIdFrom(int userIdFrom) {
        this.userIdFrom = userIdFrom;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public int getTransferType() {
        return transferType;
    }

    public void setTransferType(int transferType) {
        this.transferType = transferType;
    }

    public int getTransferStatus() {
        return transferStatus;
    }

    public void setTransferStatus(int transferStatus) {
        this.transferStatus = transferStatus;
    }
}
