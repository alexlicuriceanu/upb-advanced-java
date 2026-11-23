package com.luxoft.bankapp.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;

/**
 * Bank statistics implemented with plain collections (no streams).
 */
public class BankReport {

	/** Key used in {@link #getClientsByCity(Bank)} for clients without a city. */
	public static final String UNKNOWN_CITY = "Unknown";

	private static final Comparator<Client> CLIENTS_BY_NAME = new Comparator<Client>() {
		@Override
		public int compare(Client c1, Client c2) {
			return c1.getName().compareTo(c2.getName());
		}
	};

	// Ties on balance are broken by id so that accounts with equal balances are not dropped by the set
	private static final Comparator<Account> ACCOUNTS_BY_BALANCE = new Comparator<Account>() {
		@Override
		public int compare(Account a1, Account a2) {
			int result = Double.compare(a1.getBalance(), a2.getBalance());
			return result != 0 ? result : Integer.compare(a1.getId(), a2.getId());
		}
	};

	public int getNumberOfClients(Bank bank) {
		return bank.getClients().size();
	}

	public int getNumberOfAccounts(Bank bank) {
		int count = 0;
		for (Client client : bank.getClients()) {
			count += client.getAccounts().size();
		}
		return count;
	}

	public SortedSet<Client> getClientsSorted(Bank bank) {
		SortedSet<Client> sorted = new TreeSet<Client>(CLIENTS_BY_NAME);
		sorted.addAll(bank.getClients());
		return sorted;
	}

	public double getTotalSumInAccounts(Bank bank) {
		double total = 0;
		for (Client client : bank.getClients()) {
			for (Account account : client.getAccounts()) {
				total += account.getBalance();
			}
		}
		return total;
	}

	public SortedSet<Account> getAccountsSortedBySum(Bank bank) {
		SortedSet<Account> sorted = new TreeSet<Account>(ACCOUNTS_BY_BALANCE);
		for (Client client : bank.getClients()) {
			sorted.addAll(client.getAccounts());
		}
		return sorted;
	}

	/**
	 * The credit granted is the overdraft actually used, i.e. the negative part
	 * of the balance of every {@link CheckingAccount}.
	 */
	public double getBankCreditSum(Bank bank) {
		double credit = 0;
		for (Client client : bank.getClients()) {
			for (Account account : client.getAccounts()) {
				if (account instanceof CheckingAccount && account.getBalance() < 0) {
					credit += -account.getBalance();
				}
			}
		}
		return credit;
	}

	public Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
		Map<Client, Collection<Account>> result = new LinkedHashMap<Client, Collection<Account>>();
		for (Client client : bank.getClients()) {
			result.put(client, client.getAccounts());
		}
		return result;
	}

	/**
	 * @return cities (alphabetically ordered) mapped to their clients; clients without
	 *         a city are grouped under {@link #UNKNOWN_CITY}
	 */
	public Map<String, List<Client>> getClientsByCity(Bank bank) {
		Map<String, List<Client>> result = new TreeMap<String, List<Client>>();
		for (Client client : bank.getClients()) {
			String city = client.getCity() != null ? client.getCity() : UNKNOWN_CITY;
			List<Client> cityClients = result.get(city);
			if (cityClients == null) {
				cityClients = new ArrayList<Client>();
				result.put(city, cityClients);
			}
			cityClients.add(client);
		}
		return result;
	}

}
