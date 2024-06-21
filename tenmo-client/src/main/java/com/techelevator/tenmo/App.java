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
		// TODO Auto-generated method stub
		
	}

	private void viewPendingRequests() {
		// TODO Auto-generated method stub
		
	}

	private void sendBucks() {
        AccountServices accountServices = new AccountServices(API_BASE_URL, currentUser);
        System.out.println("-------------------------------------------");
        System.out.println("Users");
        System.out.printf("%-12s %-30s", "ID", "Name");
        System.out.println("-------------------------------------------");
        User[] users = accountServices.listUsers();
        for (User user : users) {
            if (user.getId() == currentUser.getUser().getId()) {
            } else {
                System.out.printf("%-12s %-30s", user.getId(), user.getUsername());
            }
        }
        System.out.println();
        int userTo = consoleService.promptForInt("Enter ID of user you are sending to (0 to cancel): ");
        // verify that I am not sending money to myself
        if (userTo == currentUser.getUser().getId()) {
            System.out.println("User cannot send TE Bucks to themselves.");
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
        accountServices.pushTransfer(transferReturned.getId());
	}

	private void requestBucks() {
		// TODO Auto-generated method stub
		
	}

}
