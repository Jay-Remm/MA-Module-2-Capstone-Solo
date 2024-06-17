package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

public interface ServicesDao {

    BigDecimal getBalance(int id);
}
