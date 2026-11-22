# Bank Application Specification

## Context

The project in this folder (`ai/`) is a Java SE Bank Application. It manages a Bank. The Bank has
clients; each client has accounts of two types: a saving account and a checking account (with
possible overdraft).

- If the `withdraw` method requests an amount of money that exceeds the amount that can be given
  to the client from a `SavingAccount`, a checked exception `NotEnoughFundsException` is thrown.
- `OverdraftLimitExceededException` is thrown by the overridden `withdraw()` method of the
  `CheckingAccount` class and extends `NotEnoughFundsException`.
- If a client with the given name already exists in the bank, the checked exception
  `ClientExistsException` is thrown.

Existing code layout (package `com.luxoft.bankapp`):

- `domain`: `Account`, `AbstractAccount`, `SavingAccount`, `CheckingAccount`, `Client`, `Gender`, `Bank`
- `exceptions`: `BankException`, `ClientExistsException`, `NotEnoughFundsException`, `OverdraftLimitExceededException`
- `service`: `BankService`
- `main`: `BankApplication`
- `utils`: `ClientRegistrationListener`
- `tests`: `Test1`, `Test2`, `Test3` (JUnit 4)

## General rules

- Work only inside this `ai/` folder. Do not modify anything outside it.
- Java SE 8 or higher. No external libraries except JUnit 4 (already on the module classpath).
- Keep the existing package structure.
- The existing tests `Test1`, `Test2`, `Test3` must keep passing. If a change in the domain model
  makes an existing test stop compiling, update the test minimally so that it checks the same thing.
- New tests go in the `com.luxoft.bankapp.tests` package and use JUnit 4.
- When you finish, summarize which files you created or changed.

## Task 1: Implementation using collections

1. Modify the `Bank` class: store the client list using `java.util.Set`; select the required
   interface implementation. Implement the access methods `getClients()` and `addClient(Client)`.
   Make sure the collection works properly, that is, the `equals()` and `hashCode()` methods are
   properly overridden. Properly encapsulate the clients collection: `getClients()` must return an
   unmodifiable client set.
2. Modify the `Client` class: store the account list using `java.util.Set`. Perform actions
   similar to those described above.
3. Implement the `BankReport` class with the following methods:

   - `int getNumberOfClients(Bank bank)`: Returns the number of bank clients. 
   - `int getNumberOfAccounts(Bank bank)`: Returns the total number of accounts for all bank clients. 
   - `SortedSet<Client> getClientsSorted(Bank bank)`: Returns the set of clients in alphabetical order. 
   - `double getTotalSumInAccounts(Bank bank)`: Returns the total sum (balance) of the accounts of all bank clients. 
   - `SortedSet<Account> getAccountsSortedBySum(Bank bank)`: Returns the set of all accounts, ordered by current account balance. 
   - `double getBankCreditSum(Bank bank)`: Returns the total amount of credit granted to the bank clients, that is, the sum of all values above the account balance for `CheckingAccount`. 
   - `Map<Client, Collection<Account>> getCustomerAccounts(Bank bank)`: Returns a map Client ⇔ list of his/her accounts. 
   - `Map<String, List<Client>> getClientsByCity(Bank bank)`: Add a `city` field to the `Client` class. Returns a map with cities as keys and the list of clients in each city as values, ordered by city name alphabetically. 

4. Define all collections using generics.
5. Implement displaying the bank statistics by calling the corresponding methods of `BankReport`
   from `BankApplication`. Add a command-line argument so that `java BankApplication -statistics`
   launches the application in a special mode that reads the `display statistic` command from the
   console and prints the statistics (including the clients-by-city table).

## Task 2: Implementation using streams

`BankReport` from Exercise 1 already exists. Create a `BankReportStreams` class with the same
methods and functionality, this time implemented using streams.

1. Implement `BankReportStreams` with the following methods, using streams:

   - `int getNumberOfClients(Bank bank)`: Returns the number of bank clients. 
   - `int getNumberOfAccounts(Bank bank)`: Returns the total number of accounts for all bank clients. 
   - `SortedSet<Client> getClientsSorted(Bank bank)`: Returns the set of clients in alphabetical order. 
   - `double getTotalSumInAccounts(Bank bank)`: Returns the total sum (balance) of the accounts of all bank clients. 
   - `SortedSet<Account> getAccountsSortedBySum(Bank bank)`: Returns the set of all accounts, ordered by current account balance. 
   - `double getBankCreditSum(Bank bank)`: Returns the total amount of credit granted to the bank clients, that is, the sum of all values above the account balance for `CheckingAccount`. 
   - `Map<Client, Collection<Account>> getCustomerAccounts(Bank bank)`: Returns a map Client -> list of his/her accounts. 
   - `Map<String, List<Client>> getClientsByCity(Bank bank)`: Returns a map with cities as keys and the list of clients in each city as values, ordered by city name alphabetically. 

2. Write JUnit 4 tests that check the functionality of the `BankReportStreams` class.
