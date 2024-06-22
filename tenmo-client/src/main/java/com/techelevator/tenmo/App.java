package com.techelevator.tenmo;

import com.techelevator.tenmo.model.*;
import com.techelevator.tenmo.services.AccountServices;
import com.techelevator.tenmo.services.AuthenticationService;
import com.techelevator.tenmo.services.ConsoleService;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;


public class App {

    private static final String API_BASE_URL = "http://localhost:8080/";
    private RestTemplate restTemplate = new RestTemplate();

    private final ConsoleService consoleService = new ConsoleService();
    private final AuthenticationService authenticationService = new AuthenticationService(API_BASE_URL);

    private AuthenticatedUser currentUser;

    public static void main(String[] args) {
        App app = new App();
        app.run();
    }

    private void run() {
        consoleService.printGreeting();
        loginMenu();
        if (currentUser != null) {
            mainMenu();
        }
    }
    private void loginMenu() {
        int menuSelection = -1;
        while (menuSelection != 0 && currentUser == null) {
            consoleService.printLoginMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                handleRegister();
            } else if (menuSelection == 2) {
                handleLogin();
            } else if (menuSelection != 0) {
                System.out.println("Invalid Selection");
                consoleService.pause();
            }
        }
    }

    private void handleRegister() {
        System.out.println("Please register a new user account");
        UserCredentials credentials = consoleService.promptForCredentials();
        if (authenticationService.register(credentials)) {
            System.out.println("Registration successful. You can now login.");
        } else {
            consoleService.printErrorMessage();
        }
    }

    private void handleLogin() {
        UserCredentials credentials = consoleService.promptForCredentials();
        currentUser = authenticationService.login(credentials);
        if (currentUser == null) {
            consoleService.printErrorMessage();
        }
    }

    private void mainMenu() {
        int menuSelection = -1;
        while (menuSelection != 0) {
            consoleService.printMainMenu();
            menuSelection = consoleService.promptForMenuSelection("Please choose an option: ");
            if (menuSelection == 1) {
                viewCurrentBalance();
            } else if (menuSelection == 2) {
                viewTransferHistory();
            } else if (menuSelection == 3) {
                viewPendingRequests();
            } else if (menuSelection == 4) {
                sendBucks();
            } else if (menuSelection == 5) {
                requestBucks();
            } else if (menuSelection == 0) {
                continue;
            } else {
                System.out.println("Invalid Selection");
            }
            consoleService.pause();
        }
    }

	private void viewCurrentBalance() {
        AccountServices accountServices = new AccountServices(API_BASE_URL, currentUser);
        BigDecimal balance = accountServices.getBalance(currentUser.getUser().getId());
        System.out.println("Your current account balance is: $" + balance);
        System.out.println();
        mainMenu();
	}

	private void viewTransferHistory() {
		AccountServices accountServices = new AccountServices(API_BASE_URL, currentUser);
        System.out.println("-------------------------------------------");
        System.out.println("Transfers");
        System.out.printf("%-12s %-25s %-8s %n", "ID", "From/To", "Amount");
        System.out.println("-------------------------------------------");
        Transfer[] transfers = accountServices.listTransfers(currentUser.getUser().getId());
        for (Transfer transfer : transfers) {
            if (currentUser.getUser().getUsername().equals(transfer.getAccountFrom())) {
                System.out.printf("%-12s %-6s %-19s %-2s %7s %n", transfer.getId(), "To: ", transfer.getAccountTo(), "$", transfer.getAmount());
            } else if (currentUser.getUser().getUsername().equals(transfer.getAccountTo())) {
                System.out.printf("%-12s %-6s %-19s %-2s %7s %n", transfer.getId(), "From: ", transfer.getAccountFrom(), "$", transfer.getAmount());
            }
        }
        System.out.println("-------------------------------------------");
        int transferId = consoleService.promptForInt("Please enter transfer ID to view details (0 to cancel): ");
        Transfer specificTransfer = accountServices.getTransferById(transferId);
        System.out.println();
        System.out.println("-------------------------------------------");
        System.out.println("Transfer Details");
        System.out.println("-------------------------------------------");
        System.out.println("Id: " + specificTransfer.getId());
        System.out.println("From: " + specificTransfer.getAccountFrom());
        System.out.println("To: " + specificTransfer.getAccountTo());
        System.out.println("Type: " + specificTransfer.getType());
        System.out.println("Status: " + specificTransfer.getStatus());
        System.out.println("Amount: $" + specificTransfer.getAmount());
	}

	private void viewPendingRequests() {
        AccountServices accountServices = new AccountServices(API_BASE_URL, currentUser);
        System.out.println("-------------------------------------------");
        System.out.println("Pending Transfers");
        System.out.printf("%-12s %-25s %-8s %n", "ID", "To", "Amount");
        System.out.println("-------------------------------------------");
        Transfer[] transfers = accountServices.listPendingTransfers(currentUser.getUser().getId());
        for (Transfer transfer : transfers) {
            System.out.printf("%-12s %-25s %-2s %7s %n", transfer.getId(), transfer.getAccountTo(), "$", transfer.getAmount());
        }
        System.out.println("-------------------------------------------");
        int transferId = consoleService.promptForInt("Please enter transfer ID to approve/reject (0 to cancel): ");
        Transfer specificTransfer = accountServices.getTransferById(transferId);
        System.out.println();
        System.out.println("1: Approve");
        System.out.println("2: Reject");
        System.out.println("0: Don't approve or reject");
        System.out.println("-------------------------------------------");
        int choice = consoleService.promptForInt("Please choose an option: ");
        if (choice == 1) {
            // check if I have the money in my account and then push the transfer
            if (specificTransfer.getAmount().compareTo(accountServices.getBalance(currentUser.getUser().getId())) > 0) {
                System.out.println("Balance is not sufficient for transfer.");
                consoleService.pause();
                viewPendingRequests();
            } else {
                accountServices.pushTransfer(specificTransfer.getId());
                accountServices.updateTransferStatus(specificTransfer.getId(), 2);
            }
        } else if (choice == 2) {
            accountServices.updateTransferStatus(specificTransfer.getId(), 3);
        } else if (choice == 0) {
            mainMenu();
        }
	}

	private void sendBucks() {
        AccountServices accountServices = new AccountServices(API_BASE_URL, currentUser);
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.printf("%-12s %-30s %n", "ID", "Name");
        System.out.println("-------------------------------------------");
        User[] users = accountServices.listUsers();
        for (User user : users) {
            if (user.getId() == currentUser.getUser().getId()) {
            } else {
                System.out.printf("%-12s %-30s %n", user.getId(), user.getUsername());
            }
        }
        System.out.println();
        int userTo = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        // verify that I am not sending money to myself
        if (userTo == currentUser.getUser().getId()) {
            System.out.println("Users cannot send TE Bucks to themselves.");
            consoleService.pause();
            sendBucks();
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        // verify that I have the amount in my account before making the transfer
        // verify that the amount is not 0 or negative
        if (amount.compareTo(accountServices.getBalance(currentUser.getUser().getId())) > 0) {
            System.out.println("Balance is not sufficient for transfer.");
            consoleService.pause();
            sendBucks();
        } else if (amount.compareTo(BigDecimal.ZERO) <= 0 ) {
            System.out.println("Amount must not be 0 or negative.");
            consoleService.pause();
            sendBucks();
        }
        // make new DTO and then pass it into making a newTransfer, and then because send transfers are automatically approved, push the transfer.
        TransferClientDto newTransfer = new TransferClientDto(userTo, currentUser.getUser().getId(), amount, 2, 2);
        Transfer transferReturned = accountServices.makeTransfer(newTransfer);
        System.out.println("Confirm sending $" + amount + " to user: " + userTo);
        consoleService.pause();
        accountServices.pushTransfer(transferReturned.getId());
	}

	private void requestBucks() {
        AccountServices accountServices = new AccountServices(API_BASE_URL, currentUser);
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.printf("%-12s %-30s %n", "ID", "Name");
        System.out.println("-------------------------------------------");
        User[] users = accountServices.listUsers();
        for (User user : users) {
            if (user.getId() == currentUser.getUser().getId()) {
            } else {
                System.out.printf("%-12s %-30s %n", user.getId(), user.getUsername());
            }
        }
        System.out.println();
        int userFrom = consoleService.promptForInt("Enter ID of user you are requesting from (0 to cancel): ");
        // verify that I am not sending money to myself
        if (userFrom == currentUser.getUser().getId()) {
            System.out.println("Users cannot send TE Bucks to themselves.");
            consoleService.pause();
            sendBucks();
        }
        BigDecimal amount = consoleService.promptForBigDecimal("Enter amount: ");
        // verify that the amount is not 0 or negative
        if (amount.compareTo(BigDecimal.ZERO) <= 0 ) {
            System.out.println("Amount must not be 0 or negative.");
            consoleService.pause();
            sendBucks();
        }
        TransferClientDto newTransfer = new TransferClientDto(currentUser.getUser().getId(), userFrom, amount, 1, 1);
        Transfer transferReturned = accountServices.makeTransfer(newTransfer);
        System.out.println("Confirm requesting $" + amount + " from user: " + userFrom);
        consoleService.pause();
    }

}
