package com.techelevator.tenmo.services;

import com.techelevator.tenmo.model.AuthenticatedUser;
import com.techelevator.tenmo.model.Transfer;
import com.techelevator.tenmo.model.TransferClientDto;
import com.techelevator.tenmo.model.User;
import com.techelevator.util.BasicLogger;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClientResponseException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class AccountServices {

    private final String baseUrl;
    private AuthenticatedUser currentUser;
    private final RestTemplate restTemplate = new RestTemplate();

    public AccountServices(String url, AuthenticatedUser currentUser) {
        this.baseUrl = url;
        this.currentUser = currentUser;
    }

    public BigDecimal getBalance(int userId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        BigDecimal balance = null;
        try {
            ResponseEntity<BigDecimal> response = restTemplate.exchange(
                    baseUrl + "/balance/" + userId, HttpMethod.GET, entity, BigDecimal.class);
            balance = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return balance;
    }

    public Transfer makeTransfer(TransferClientDto transfer) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<TransferClientDto> entity = new HttpEntity<>(transfer, headers);
        Transfer newTransfer = null;
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(
                    baseUrl + "/transfer", HttpMethod.POST, entity, Transfer.class);
            newTransfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return newTransfer;
    }

    public Transfer getTransferById(int transferId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        Transfer transfer = null;
        try {
            ResponseEntity<Transfer> response = restTemplate.exchange(
                    baseUrl + "/transfer/" + transferId, HttpMethod.GET, entity, Transfer.class);
            transfer = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfer;
    }

    public Transfer[] listTransfers(int userId) {
        Transfer[] transfers = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(
                    baseUrl + "/transfer/list/" + userId, HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }

    // Start and close the Transaction, reflecting the transfer in both accounts
    public void pushTransfer(int id) {
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            restTemplate.exchange(baseUrl + "/finalize-transfer/" + id, HttpMethod.PUT, entity, Void.class);
            System.out.print("Transfer sent successfully");
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
    }

    // Method to list users
    public User[] listUsers() {
        User[] users = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<User[]> response = restTemplate.exchange(
                    baseUrl + "/users", HttpMethod.GET, entity, User[].class);
            users = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return users;
    }

    // Method to view pending requests
    public Transfer[] listPendingTransfers(int userId) {
        Transfer[] transfers = null;
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(currentUser.getToken());
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        try {
            ResponseEntity<Transfer[]> response = restTemplate.exchange(
                    baseUrl + "/transfer/pending/" + userId, HttpMethod.GET, entity, Transfer[].class);
            transfers = response.getBody();
        } catch (RestClientResponseException | ResourceAccessException e) {
            BasicLogger.log(e.getMessage());
        }
        return transfers;
    }
}
