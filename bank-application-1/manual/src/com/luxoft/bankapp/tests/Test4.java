package com.luxoft.bankapp.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;
import com.luxoft.bankapp.domain.Gender;
import com.luxoft.bankapp.domain.SavingAccount;
import com.luxoft.bankapp.exceptions.ClientExistsException;
import com.luxoft.bankapp.service.BankReport;
import com.luxoft.bankapp.service.BankReportStreams;

public class Test4 {

    private Bank bank;
    private Client john;
    private Client anna;
    private Client mike;
    private BankReportStreams report;

    @Before
    public void setUp() throws ClientExistsException {
        bank = new Bank();

        john = new Client("John", Gender.MALE, "Bucharest");
        john.addAccount(new SavingAccount(1, 1000));
        john.addAccount(new CheckingAccount(2, -100, 500));

        anna = new Client("Anna", Gender.FEMALE, "Cluj");
        anna.addAccount(new SavingAccount(3, 2000));
        anna.addAccount(new CheckingAccount(4, 1000, 200)); // same balance as account 1

        mike = new Client("Mike", Gender.MALE, "Bucharest");
        mike.addAccount(new SavingAccount(5, 500));
        mike.addAccount(new CheckingAccount(6, -50, 100));

        bank.addClient(john);
        bank.addClient(anna);
        bank.addClient(mike);

        report = new BankReportStreams();
    }

    @Test
    public void testGetNumberOfClients() {
        assertEquals(3, report.getNumberOfClients(bank));
    }

    @Test
    public void testGetNumberOfAccounts() {
        assertEquals(6, report.getNumberOfAccounts(bank));
    }

    @Test
    public void testGetClientsSorted() {
        List<String> names = report.getClientsSorted(bank).stream()
                .map(Client::getName)
                .collect(Collectors.toList());
        assertEquals(Arrays.asList("Anna", "John", "Mike"), names);
    }

    @Test
    public void testGetTotalSumInAccounts() {
        // 1000 - 100 + 2000 + 1000 + 500 - 50
        assertEquals(4350, report.getTotalSumInAccounts(bank), 0.001);
    }

    @Test
    public void testGetAccountsSortedBySum() {
        List<Integer> ids = report.getAccountsSortedBySum(bank).stream()
                .map(Account::getId)
                .collect(Collectors.toList());
        // accounts 1 and 4 both have 1000 -> both kept, ordered by id
        assertEquals(Arrays.asList(2, 6, 5, 1, 4, 3), ids);
    }

    @Test
    public void testGetBankCreditSum() {
        // checking accounts at -100 and -50
        assertEquals(150, report.getBankCreditSum(bank), 0.001);
    }

    @Test
    public void testGetBankCreditSumIgnoresPositiveBalances() throws ClientExistsException {
        Bank richBank = new Bank();
        Client client = new Client("Rich", Gender.MALE, "Iasi");
        client.addAccount(new CheckingAccount(10, 5000, 100));
        richBank.addClient(client);
        assertEquals(0, report.getBankCreditSum(richBank), 0.001);
    }

    @Test
    public void testGetCustomerAccounts() {
        Map<Client, Collection<Account>> customerAccounts = report.getCustomerAccounts(bank);
        assertEquals(3, customerAccounts.size());
        assertEquals(2, customerAccounts.get(john).size());
        assertTrue(customerAccounts.get(anna).containsAll(anna.getAccounts()));
    }

    @Test
    public void testGetClientsByCity() {
        Map<String, List<Client>> byCity = report.getClientsByCity(bank);
        assertEquals(Arrays.asList("Bucharest", "Cluj"), new ArrayList<>(byCity.keySet()));
        assertEquals(Arrays.asList(john, mike), byCity.get("Bucharest"));
        assertEquals(Arrays.asList(anna), byCity.get("Cluj"));
    }

    @Test
    public void testClientWithoutCity() throws ClientExistsException {
        bank.addClient(new Client("Nobody", Gender.MALE));
        Map<String, List<Client>> byCity = report.getClientsByCity(bank);
        assertEquals(1, byCity.get("Unknown").size());
    }

    @Test
    public void testSameResultsAsBankReport() {
        BankReport classic = new BankReport();
        assertEquals(classic.getNumberOfClients(bank), report.getNumberOfClients(bank));
        assertEquals(classic.getNumberOfAccounts(bank), report.getNumberOfAccounts(bank));
        assertEquals(classic.getClientsSorted(bank), report.getClientsSorted(bank));
        assertEquals(classic.getTotalSumInAccounts(bank), report.getTotalSumInAccounts(bank), 0.001);
        assertEquals(classic.getAccountsSortedBySum(bank), report.getAccountsSortedBySum(bank));
        assertEquals(classic.getBankCreditSum(bank), report.getBankCreditSum(bank), 0.001);
        assertEquals(classic.getCustomerAccounts(bank), report.getCustomerAccounts(bank));
        assertEquals(classic.getClientsByCity(bank), report.getClientsByCity(bank));
    }
}