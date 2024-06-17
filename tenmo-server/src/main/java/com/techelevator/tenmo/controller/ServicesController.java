package com.techelevator.tenmo.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.techelevator.tenmo.dao.ServicesDao;

import java.math.BigDecimal;

// Controller to access and manipulate account services

@RestController
public class ServicesController {

    private ServicesDao servicesDao;

    public ServicesController(ServicesDao servicesDao) {
        this.servicesDao = servicesDao;
    }

    // Get the balance from jdbcServicesDao which gets balance from database
    @RequestMapping(path = "/balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        return servicesDao.getBalance(id);
    }

}
