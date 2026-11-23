package com.luxoft.bankapp.service;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.luxoft.bankapp.domain.Account;
import com.luxoft.bankapp.domain.Bank;
import com.luxoft.bankapp.domain.CheckingAccount;
import com.luxoft.bankapp.domain.Client;

/**
 * Bank statistics implemented with streams. Same behaviour as {@link BankReport}.
 */
public class BankReportStreams {

	/** Key used in {@link #getClientsByCity(Bank)} for clients without a city. */
	public static final String UNKNOWN_CITY = BankReport.UNKNOWN_CITY;

	private static final Comparator<Client> CLIENTS_BY_NAME = Comparator.comparing(Client::getName);

	// Ties on balance are broken by id so that accounts with equal balances are not dropped by the set
	private static final Comparator<Account> ACCOUNTS_BY_BALANCE =
			Comparator.comparingDouble(Account::getBalance).thenComparingInt(Account::getId);

	public int getNumberOfClients(Bank bank) {
		return (int) bank.getClients().stream().count();
	}

	public int getNumberOfAccounts(Bank bank) {
		return (int) accounts(bank).count();
	}

	public SortedSet<Client> getClientsSorted(Bank bank) {
		return bank.getClients().stream()
				.collect(Collectors.toCollection(() -> new TreeSet<>(CLIENTS_BY_NAME)));
	}

	public double getTotalSumInAccounts(Bank bank) {
		return accounts(bank)
				.mapToDouble(Account::getBalance)
				.sum();
	}

	public SortedSet<Account> getAccountsSortedBySum(Bank bank) {
		return accounts(bank)
				.collect(Collectors.toCollection(() -> new TreeSet<>(ACCOUNTS_BY_BALANCE)));
	}

	/**
	 * The credit granted is the overdraft actually used, i.e. the negative part
	 * of the balance of every {@link CheckingAccount}.
	 */
	public double getBankCreditSum(Bank bank) {
		return accounts(bank)
				.filter(account -> account instanceof CheckingAccount)
				.mapToDouble(Account::getBalance)
				.filter(balance -> balance < 0)
				.map(balance -> -balance)
				.sum();
	}

	public Map<Client, Collection<Account>> getCustomerAccounts(Bank bank) {
		return bank.getClients().stream()
				.collect(Collectors.toMap(
						Function.identity(),
						Client::getAccounts,
						(a, b) -> a,
						LinkedHashMap::new));
	}

	/**
	 * @return cities (alphabetically ordered) mapped to their clients; clients without
	 *         a city are grouped under {@link #UNKNOWN_CITY}
	 */
	public Map<String, List<Client>> getClientsByCity(Bank bank) {
		return bank.getClients().stream()
				.collect(Collectors.groupingBy(
						client -> client.getCity() != null ? client.getCity() : UNKNOWN_CITY,
						TreeMap::new,
						Collectors.toList()));
	}

	private static Stream<Account> accounts(Bank bank) {
		return bank.getClients().stream()
				.flatMap(client -> client.getAccounts().stream());
	}

}
