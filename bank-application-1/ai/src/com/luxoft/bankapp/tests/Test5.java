package com.luxoft.bankapp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;
import com.luxoft.bankapp.domain.Gender;
import com.luxoft.bankapp.domain.SavingAccount;
import com.luxoft.bankapp.exceptions.ClientExistsException;
import com.luxoft.bankapp.exceptions.OverdraftLimitExceededException;
import com.luxoft.bankapp.service.BankReport;
import com.luxoft.bankapp.service.BankReportStreams;
import com.luxoft.bankapp.service.BankService;

/**
 * Tests for {@link BankReportStreams}.
 */
public class Test5 {

	private Bank bank;
	private BankReportStreams report;
	private Client zoe;
	private Client adam;
	private Client mike;
	private Client nobody;

	@Before
	public void setUp() throws ClientExistsException, OverdraftLimitExceededException {
		bank = new Bank();
		report = new BankReportStreams();

		zoe = new Client("Zoe", Gender.FEMALE, "Iasi");
		zoe.addAccount(new SavingAccount(1, 500));
		CheckingAccount zoeChecking = new CheckingAccount(2, 100, 300);
		zoeChecking.withdraw(250); // balance -150
		zoe.addAccount(zoeChecking);

		adam = new Client("Adam", Gender.MALE, "Bucharest");
		adam.addAccount(new SavingAccount(3, 1000));

		mike = new Client("Mike", Gender.MALE, "Bucharest");
		CheckingAccount mikeChecking = new CheckingAccount(4, 0, 100);
		mikeChecking.withdraw(50); // balance -50
		mike.addAccount(mikeChecking);
		mike.addAccount(new SavingAccount(5, 500)); // same balance as account 1

		nobody = new Client("Nobody", null);

		BankService.addClient(bank, zoe);
		BankService.addClient(bank, adam);
		BankService.addClient(bank, mike);
		BankService.addClient(bank, nobody);
	}

	@Test
	public void testNumberOfClients() {
		assertEquals(4, report.getNumberOfClients(bank));
	}

	@Test
	public void testNumberOfAccounts() {
		assertEquals(5, report.getNumberOfAccounts(bank));
	}

	@Test
	public void testClientsSorted() {
		SortedSet<Client> sorted = report.getClientsSorted(bank);
		assertEquals(Arrays.asList(adam, mike, nobody, zoe), new ArrayList<Client>(sorted));
	}

	@Test
	public void testTotalSumInAccounts() {
		// 500 - 150 + 1000 - 50 + 500
		assertEquals(1800, report.getTotalSumInAccounts(bank), 0.001);
	}

	@Test
	public void testAccountsSortedBySum() {
		List<Integer> ids = new ArrayList<Integer>();
		for (Account account : report.getAccountsSortedBySum(bank)) {
			ids.add(account.getId());
		}
		// accounts 1 and 5 share a balance and must both be kept
		assertEquals(Arrays.asList(2, 4, 1, 5, 3), ids);
	}

	@Test
	public void testBankCreditSum() {
		assertEquals(200, report.getBankCreditSum(bank), 0.001);
	}

	@Test
	public void testBankCreditSumIgnoresPositiveCheckingBalances() throws ClientExistsException {
		Bank other = new Bank();
		Client rich = new Client("Rich", Gender.MALE);
		rich.addAccount(new CheckingAccount(1, 1000, 500));
		BankService.addClient(other, rich);
		assertEquals(0, report.getBankCreditSum(other), 0);
	}

	@Test
	public void testCustomerAccounts() {
		Map<Client, Collection<Account>> map = report.getCustomerAccounts(bank);
		assertEquals(Arrays.asList(zoe, adam, mike, nobody), new ArrayList<Client>(map.keySet()));
		assertEquals(2, map.get(zoe).size());
		assertEquals(1, map.get(adam).size());
		assertEquals(2, map.get(mike).size());
		assertTrue(map.get(nobody).isEmpty());
		assertTrue(map.get(adam).contains(new SavingAccount(3, 0)));
	}

	@Test
	public void testClientsByCity() {
		Map<String, List<Client>> byCity = report.getClientsByCity(bank);
		assertEquals(Arrays.asList("Bucharest", "Iasi", BankReportStreams.UNKNOWN_CITY),
				new ArrayList<String>(byCity.keySet()));
		assertEquals(Arrays.asList(adam, mike), byCity.get("Bucharest"));
		assertEquals(Arrays.asList(zoe), byCity.get("Iasi"));
		assertEquals(Arrays.asList(nobody), byCity.get(BankReportStreams.UNKNOWN_CITY));
	}

	@Test
	public void testEmptyBank() {
		Bank empty = new Bank();
		assertEquals(0, report.getNumberOfClients(empty));
		assertEquals(0, report.getNumberOfAccounts(empty));
		assertEquals(0, report.getTotalSumInAccounts(empty), 0);
		assertEquals(0, report.getBankCreditSum(empty), 0);
		assertTrue(report.getClientsSorted(empty).isEmpty());
		assertTrue(report.getAccountsSortedBySum(empty).isEmpty());
		assertTrue(report.getCustomerAccounts(empty).isEmpty());
		assertTrue(report.getClientsByCity(empty).isEmpty());
	}

	@Test
	public void testSameResultsAsBankReport() {
		BankReport classic = new BankReport();
		assertEquals(classic.getNumberOfClients(bank), report.getNumberOfClients(bank));
		assertEquals(classic.getNumberOfAccounts(bank), report.getNumberOfAccounts(bank));
		assertEquals(classic.getTotalSumInAccounts(bank), report.getTotalSumInAccounts(bank), 0.001);
		assertEquals(classic.getBankCreditSum(bank), report.getBankCreditSum(bank), 0.001);
		assertEquals(new ArrayList<Client>(classic.getClientsSorted(bank)),
				new ArrayList<Client>(report.getClientsSorted(bank)));
		assertEquals(new ArrayList<Account>(classic.getAccountsSortedBySum(bank)),
				new ArrayList<Account>(report.getAccountsSortedBySum(bank)));
		assertEquals(classic.getCustomerAccounts(bank), report.getCustomerAccounts(bank));
		assertEquals(classic.getClientsByCity(bank), report.getClientsByCity(bank));
	}

}
