package com.luxoft.bankapp.domain;

import java.util.*;

public class Client {
	
	private String name;
	private Gender gender;
	private Set<Account> accounts = new LinkedHashSet<>();
	private String city;

	public Client(String name, Gender gender) {
		this(name, gender, "Unknown");
		this.name = name;
		this.gender = gender;
	}

    public Client(String name, Gender gender, String city) {
		this.name = name;
		this.gender = gender;
		this.city = city;
	}
	
	public void addAccount(final Account account) {
		accounts.add(account);
	}
	
	public String getName() {
		return name;
	}
	
	public Gender getGender() {
		return gender;
	}

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Set<Account> getAccounts() {
		return Collections.unmodifiableSet(accounts);
	}
	
	public String getClientGreeting() {
		if (gender != null) {
			return gender.getGreeting() + " " + name;
		} else {
			return name;
		}
	}
	
	@Override
	public String toString() {
		return getClientGreeting();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || getClass() != o.getClass()) return false;
		Client client = (Client) o;
		return Objects.equals(name, client.name);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(name);
	}
}
