package com.luxoft.bankapp.main;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;
import com.luxoft.bankapp.domain.Gender;
import com.luxoft.bankapp.domain.SavingAccount;
import com.luxoft.bankapp.exceptions.ClientExistsException;
import com.luxoft.bankapp.exceptions.NotEnoughFundsException;
import com.luxoft.bankapp.exceptions.OverdraftLimitExceededException;
import com.luxoft.bankapp.service.BankReport;
import com.luxoft.bankapp.service.BankService;

public class BankApplication {
	
	private static final String STATISTICS_ARG = "-statistics";
	private static final String DISPLAY_STATISTIC_CMD = "display statistic";
	private static final String EXIT_CMD = "exit";

	private static Bank bank;

	public static void main(String[] args) {
		bank = new Bank();
		modifyBank();
		printBalance();
		BankService.printMaximumAmountToWithdraw(bank);

		if (Arrays.asList(args).contains(STATISTICS_ARG)) {
			runStatisticsMode();
		}
	}

	private static void runStatisticsMode() {
		System.out.format("Statistics mode. Type '%s' to show the bank statistics or '%s' to quit.%n",
				DISPLAY_STATISTIC_CMD, EXIT_CMD);
		Scanner scanner = new Scanner(System.in);
		while (scanner.hasNextLine()) {
			String command = scanner.nextLine().trim();
			if (DISPLAY_STATISTIC_CMD.equalsIgnoreCase(command)) {
				displayStatistics();
			} else if (EXIT_CMD.equalsIgnoreCase(command)) {
				break;
			} else if (!command.isEmpty()) {
				System.out.format("Unknown command: %s%n", command);
			}
		}
	}

	private static void displayStatistics() {
		BankReport report = new BankReport();

		System.out.format("%n=== Bank statistics ===%n");
		System.out.format("Number of clients: %d%n", report.getNumberOfClients(bank));
		System.out.format("Number of accounts: %d%n", report.getNumberOfAccounts(bank));
		System.out.format("Total sum in accounts: %.2f%n", report.getTotalSumInAccounts(bank));
		System.out.format("Bank credit sum: %.2f%n", report.getBankCreditSum(bank));

		System.out.format("%nClients sorted by name:%n");
		for (Client client : report.getClientsSorted(bank)) {
			System.out.format("  %s%n", client);
		}

		System.out.format("%nAccounts sorted by balance:%n");
		for (Account account : report.getAccountsSortedBySum(bank)) {
			System.out.format("  Account %d : %.2f%n", account.getId(), account.getBalance());
		}

		System.out.format("%nAccounts per client:%n");
		for (Map.Entry<Client, Collection<Account>> entry : report.getCustomerAccounts(bank).entrySet()) {
			System.out.format("  %s:", entry.getKey());
			for (Account account : entry.getValue()) {
				System.out.format(" #%d", account.getId());
			}
			System.out.println();
		}

		System.out.format("%nClients by city:%n");
		System.out.format("  %-15s | %s%n", "City", "Clients");
		System.out.format("  %s%n", "----------------+------------------------------");
		for (Map.Entry<String, List<Client>> entry : report.getClientsByCity(bank).entrySet()) {
			StringBuilder names = new StringBuilder();
			for (Client client : entry.getValue()) {
				if (names.length() > 0) {
					names.append(", ");
				}
				names.append(client.getName());
			}
			System.out.format("  %-15s | %s%n", entry.getKey(), names);
		}
		System.out.println();
	}

	private static void modifyBank() {
		Client client1 = new Client("John", Gender.MALE, "Bucharest");
		Account account1 = new SavingAccount(1, 100);
		Account account2 = new CheckingAccount(2, 100, 20);
		client1.addAccount(account1);
		client1.addAccount(account2);
		
		try {
		   BankService.addClient(bank, client1);
		} catch(ClientExistsException e) {
			System.out.format("Cannot add an already existing client: %s%n", client1.getName());
	    } 

		account1.deposit(100);
		try {
		  account1.withdraw(10);
		} catch (OverdraftLimitExceededException e) {
	    	System.out.format("Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
	    } catch (NotEnoughFundsException e) {
	    	System.out.format("Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getAmount());
	    }
		
		try {
		  account2.withdraw(90);
		} catch (OverdraftLimitExceededException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
	    } catch (NotEnoughFundsException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getAmount());
	    }
		
		try {
		  account2.withdraw(100);
		} catch (OverdraftLimitExceededException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, overdraft: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getOverdraft(), e.getAmount());
	    } catch (NotEnoughFundsException e) {
	      System.out.format("Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getAmount());
	    }
		
		try {
		  BankService.addClient(bank, client1);
		} catch(ClientExistsException e) {
		  System.out.format("Cannot add an already existing client: %s%n", client1);
	    }

		// a few more clients so that the statistics are meaningful
		Client client2 = new Client("Maria", Gender.FEMALE, "Cluj-Napoca");
		client2.addAccount(new SavingAccount(3, 1500));
		Account account4 = new CheckingAccount(4, 300, 100);
		client2.addAccount(account4);
		try {
			account4.withdraw(360); // goes into overdraft -> bank credit
		} catch (NotEnoughFundsException e) {
			System.out.format("Not enough funds for account %d, balance: %.2f, tried to extract amount: %.2f%n", e.getId(), e.getBalance(), e.getAmount());
		}

		Client client3 = new Client("Andrei", Gender.MALE, "Bucharest");
		client3.addAccount(new SavingAccount(5, 50));

		try {
			BankService.addClient(bank, client2);
			BankService.addClient(bank, client3);
		} catch (ClientExistsException e) {
			System.out.format("Cannot add an already existing client: %s%n", e.getMessage());
		}
	}
	
	private static void printBalance() {
		System.out.format("%nPrint balance for all clients%n");
		for(Client client : bank.getClients()) {
			System.out.println("Client: " + client);
			for (Account account : client.getAccounts()) {
				System.out.format("Account %d : %.2f%n", account.getId(), account.getBalance());
			}
		}
	}

}
