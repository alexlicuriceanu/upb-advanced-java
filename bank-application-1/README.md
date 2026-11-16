# Bank Application – Collections, Streams & AI-assisted development

Lab: *Development on the Java SE platform* – Exercises 1, 2 and 3.

The same starting code (`bank-application-start`) is solved twice so the two
solutions can be compared (Exercise 3):

| Folder | Module (IntelliJ) | Who writes it |
|---|---|---|
| [`manual/`](manual) | `bank-application-manual` | Me, by hand |
| [`ai`](ai) | `bank-application-ai-cursor` | AI agent (Cursor) |

Both folders start out **identical** – an untouched copy of the starter project –
so any difference between them comes from the solution.

## Tasks (apply to both folders)

### Exercise 1 – Collections
1. `Bank`: store clients in a `java.util.Set`; `getClients()` returns an unmodifiable set,
   `addClient(Client)`; override `equals()` / `hashCode()` correctly.
2. `Client`: store accounts in a `Set` the same way; add a `city` field.
3. New `BankReport` class:
   - `int getNumberOfClients(Bank)`
   - `int getNumberOfAccounts(Bank)`
   - `SortedSet<Client> getClientsSorted(Bank)` – alphabetical
   - `double getTotalSumInAccounts(Bank)`
   - `SortedSet<Account> getAccountsSortedBySum(Bank)` – by balance
   - `double getBankCreditSum(Bank)` – total credit used on `CheckingAccount`s (negative balances)
   - `Map<Client, Collection<Account>> getCustomerAccounts(Bank)`
   - `Map<String, List<Client>> getClientsByCity(Bank)` – cities sorted alphabetically
4. All collections use generics.
5. `java BankApplication -statistics` – read a `display statistic` command from the console
   and print the `BankReport` results.

### Exercise 2 – Streams
1. `BankReportStreams` – same methods as `BankReport`, implemented with streams.
2. JUnit tests for `BankReportStreams`.

### Exercise 3 – AI
Solve 1 and 2 again in `ai` with Cursor, then fill in [`COMPARISON.md`](COMPARISON.md).

## Layout

```
bank-application-1/
├── manual/      src/com/luxoft/bankapp/{domain,exceptions,main,service,tests,utils}
├── ai-cursor/   (same structure)
└── COMPARISON.md
```

## Running
Open the repo root (`upb-advanced-java`) in IntelliJ – both modules are registered and
use the project-level JUnit 4.13.2 library.

- App: run `com.luxoft.bankapp.main.BankApplication` (add `-statistics` as a program argument for stats mode).
- Tests: run the `com.luxoft.bankapp.tests` package.
