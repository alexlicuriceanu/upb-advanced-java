package com.luxoft.bankapp.main;

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

import java.util.*;

public class BankApplication {
	
	private static Bank bank;
	
	public static void main(String[] args) {
		bank = new Bank();
		modifyBank();
		printBalance();
		BankService.printMaximumAmountToWithdraw(bank);

		if (args.length > 0 && args[0].equals("-statistics")) {
			runStatistics();
		}
	}

	private static void runStatistics() {
		Scanner scanner = new Scanner(System.in);

		while (scanner.hasNextLine()) {
			String command = scanner.nextLine().trim().toLowerCase();

			if (command.equals("display statistic")) {
				printStatistics();
			}
		}
	}

	private static void printStatistics() {
		BankReport report = new BankReport();

		System.out.println("Number of clients: " + report.getNumberOfClients(bank));
		System.out.println("Number of accounts: " + report.getNumberOfAccounts(bank));
		System.out.println("Clients sorted: " + report.getClientsSorted(bank));
		System.out.format("Total sum in accounts: %.2f%n", report.getTotalSumInAccounts(bank));

		System.out.println("Accounts sorted by balance:");
		for (Account account : report.getAccountsSortedBySum(bank)) {
			System.out.format("  Account %d : %.2f%n", account.getId(), account.getBalance());
		}

		System.out.format("Bank credit sum: %.2f%n", report.getBankCreditSum(bank));

		System.out.println("Accounts per client:");
		for (Map.Entry<Client, Collection<Account>> entry : report.getCustomerAccounts(bank).entrySet()) {
			System.out.println("  " + entry.getKey() + ": " + entry.getValue().size() + " account(s)");
		}

		System.out.println("Clients by city:");
		for (Map.Entry<String, List<Client>> entry : report.getClientsByCity(bank).entrySet()) {
			System.out.println("  " + entry.getKey() + ": " + entry.getValue());
		}
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


		Client client2 = new Client("Anna", Gender.FEMALE, "Cluj");
		client2.addAccount(new SavingAccount(3, 500));
		client2.addAccount(new CheckingAccount(4, -50, 200));
		try {
			BankService.addClient(bank, client2);
		} catch (ClientExistsException e) {
			System.out.format("Cannot add an already existing client: %s%n", client2.getName());
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
