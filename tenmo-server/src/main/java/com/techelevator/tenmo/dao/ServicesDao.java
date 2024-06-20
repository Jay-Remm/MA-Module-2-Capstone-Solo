package com.techelevator.tenmo.dao;

import com.techelevator.tenmo.model.MakeTransferDto;
import com.techelevator.tenmo.model.Transfer;

import java.math.BigDecimal;
import java.util.List;

public interface ServicesDao {

    BigDecimal getBalance(int id);

    Transfer getTransferById(int id);

    Transfer makeTransfer(MakeTransferDto transfer);

    void pushTransfer(int id);

    List<Transfer> getTransfers(int userId);
}