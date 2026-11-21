package com.luxoft.bankapp.service;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;

import java.util.*;
import java.util.stream.Collectors;

public class BankReportStreams {
    public int getNumberOfClients(Bank bank) {
        return (int) bank.getClients().stream()
                .count();
    }

    public int getNumberOfAccounts(Bank bank) {
        return (int) bank.getClients().stream()
                .flatMap(client -> client.getAccounts().stream())
                .count();
    }

    public SortedSet<Client> getClientsSorted(Bank bank) {
        return bank.getClients().stream()
                .collect(Collectors.toCollection(
                        () -> new TreeSet<Client>(Comparator.comparing(Client::getName))));
    }

    public double getTotalSumInAccounts(Bank bank) {
        return bank.getClients().stream()
                .flatMap(client -> client.getAccounts().stream())
                .mapToDouble(Account::getBalance)
                .sum();
    }

    public SortedSet<Account> getAccountsSortedBySum(Bank bank) {
        Comparator<Account> comparator = Comparator
                .comparingDouble(Account::getBalance)
                .thenComparingInt(Account::getId);

        return bank.getClients().stream()
                .flatMap(client -> client.getAccounts().stream())
                .collect(Collectors.toCollection(() -> new TreeSet<>(comparator)));
    }

    public double getBankCreditSum(Bank bank) {
        return bank.getClients().stream()
                .flatMap(client -> client.getAccounts().stream())
                .filter(account -> account instanceof CheckingAccount && account.getBalance() < 0.0)
                .mapToDouble(account -> Math.abs(account.getBalance()))
                .sum();
    }

    public Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
        return bank.getClients().stream()
                .collect(Collectors.toMap(client -> client, client -> client.getAccounts()));
    }

    public Map<String, List<Client>> getClientsByCity(Bank bank) {
        return bank.getClients().stream()
                .collect(Collectors.groupingBy(client -> client.getCity()));
    }
}
