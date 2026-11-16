# Task for the AI agent (Cursor)

Work only inside this `ai-cursor/` folder. Java 8+ (no external libraries except JUnit 4.13.2).
Keep the existing package structure `com.luxoft.bankapp.*` and keep the existing tests passing.

## Exercise 1 – Collections
1. Modify `Bank`: store clients in a `java.util.Set` (choose the implementation). Implement
   `getClients()` (must return an unmodifiable set) and `addClient(Client)`. Make sure `equals()`
   and `hashCode()` are properly overridden. Encapsulate the collection properly.
2. Modify `Client`: store accounts in a `java.util.Set`, same rules. Add a `city` field.
3. Create `BankReport` with:
   - `int getNumberOfClients(Bank bank)` – number of clients
   - `int getNumberOfAccounts(Bank bank)` – total accounts of all clients
   - `SortedSet<Client> getClientsSorted(Bank bank)` – clients in alphabetical order
   - `double getTotalSumInAccounts(Bank bank)` – total balance of all accounts
   - `SortedSet<Account> getAccountsSortedBySum(Bank bank)` – all accounts ordered by balance
   - `double getBankCreditSum(Bank bank)` – total credit granted: the sum of the amounts
     above the account balance for `CheckingAccount`s (overdraft in use)
   - `Map<Client, Collection<Account>> getCustomerAccounts(Bank bank)` – client -> accounts
   - `Map<String, List<Client>> getClientsByCity(Bank bank)` – city -> clients, cities sorted
     alphabetically; print the table
4. Use generics for every collection.
5. In `BankApplication`, add a `-statistics` command-line mode that reads the
   `display statistic` command from the console and prints the `BankReport` results.

## Exercise 2 – Streams
1. Create `BankReportStreams` with the same methods and behaviour as `BankReport`, implemented with streams.
2. Write JUnit 4 tests for `BankReportStreams` in `com.luxoft.bankapp.tests`.
