package com.techelevator.tenmo.controller;

import com.techelevator.tenmo.dao.UserDao;
import com.techelevator.tenmo.model.MakeTransferDto;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.User;
import org.springframework.web.bind.annotation.*;

import com.techelevator.tenmo.dao.ServicesDao;

import javax.validation.Valid;
import java.math.BigDecimal;
import java.util.List;

// Controller to access and manipulate account services

@RestController
public class ServicesController {

    private ServicesDao servicesDao;
    private UserDao userDao;

    public ServicesController(ServicesDao servicesDao) {
        this.servicesDao = servicesDao;
    }

    // Get the balance from jdbcServicesDao which gets balance from database
    @RequestMapping(path = "/balance/{id}", method = RequestMethod.GET)
    public BigDecimal getBalance(@PathVariable int id) {
        return servicesDao.getBalance(id);
    }

    // create transfer.
    @RequestMapping(path = "/transfer", method = RequestMethod.POST)
    public Transfer makeTransfer(@Valid @RequestBody MakeTransferDto makeTransferDto) {
        return servicesDao.makeTransfer(makeTransferDto);
    }

    // Get transfer details
    @RequestMapping(path = "/transfer/{id}", method = RequestMethod.GET)
    public Transfer getTransfer(@PathVariable int id) {
        return servicesDao.getTransferById(id);
    }

    // get all transfers tied to the client
    @RequestMapping(path = "/transfer/list/{id}", method = RequestMethod.GET)
    public List<Transfer> transfersList(@PathVariable int id) {
        return servicesDao.getTransfers(id);
    }

    // User model has bean that prevents sensitive information (passwords, perms) from being sent to client through JSON
    @RequestMapping(path = "/users", method = RequestMethod.GET)
    public List<User> usersList() {
        return userDao.getUsers();
    }

    @RequestMapping(path = "/finalize-transfer/{id}", method = RequestMethod.PUT)
    public void pushTransfer(@PathVariable int id) {
        servicesDao.pushTransfer(id);
    }

}
