package com.luxoft.bankapp.service;

import com.luxoft.bankapp.domain.*;

import java.util.*;

public class BankReport {
    public int getNumberOfClients(Bank bank) {
        return bank.getClients().size();
    }

    public int getNumberOfAccounts(Bank bank) {
        int numberOfAccounts = 0;

        for (Client client : bank.getClients()) {
            numberOfAccounts += client.getAccounts().size();
        }
        return numberOfAccounts;
    }

    public SortedSet<Client> getClientsSorted(Bank bank) {
        SortedSet<Client> clientsSorted = new TreeSet<>(Comparator.comparing(Client::getName));
        clientsSorted.addAll(bank.getClients());

        return clientsSorted;
    }

    public double getTotalSumInAccounts(Bank bank) {
        double totalSumInAccounts = 0.0;

        for (Client client : bank.getClients()) {
            for (Account account : client.getAccounts()) {
                totalSumInAccounts += account.getBalance();
            }
        }
        return totalSumInAccounts;
    }

    public SortedSet<Account> getAccountsSortedBySum(Bank bank) {
        SortedSet<Account> accountsSortedBySum = new TreeSet<>(Comparator
                .comparingDouble(Account::getBalance)
                .thenComparingInt(Account::getId)
        );

        for (Client client : bank.getClients()) {
            accountsSortedBySum.addAll(client.getAccounts());
        }

        return accountsSortedBySum;
    }

    public double getBankCreditSum(Bank bank) {
        double bankCreditSum = 0.0;

        for (Client client : bank.getClients()) {
            for (Account account : client.getAccounts()) {
                if (account instanceof CheckingAccount && account.getBalance() < 0.0) {
                    bankCreditSum -= account.getBalance();
                }
            }
        }

        return bankCreditSum;
    }

    public Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
        Map<Client, Collection<Account>> customerAccount = new HashMap<>();

        for (Client client : bank.getClients()) {
            customerAccount.put(client, new ArrayList<>(client.getAccounts()));
        }

        return customerAccount;
    }

    public Map<String, List<Client>> getClientsByCity(Bank bank) {
        Map<String, List<Client>> clientsByCity = new TreeMap<>();

        for (Client client : bank.getClients()) {
            String city = client.getCity();
            List<Client> clientsInCity = clientsByCity.get(city);

            if (clientsInCity == null) {
                clientsInCity = new ArrayList<>();
                clientsByCity.put(city, clientsInCity);
            }

            clientsInCity.add(client);
        }

        return clientsByCity;
    }
}
