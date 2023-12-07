import java.text.SimpleDateFormat;
import java.util.*;


public class Account {

    private final String number;

    private String owner;
    private Map<Currency, Integer> balances = new HashMap<>();
    List<Action> previousState = new ArrayList<>();

    Map<String, Account> accountSnapshots = new HashMap<>();


    private Account(String owner, String number) {
        this.number = number;
        this.owner = owner;
    }

    public Account(Account copyConstructor) {
        this.number = copyConstructor.number;
        this.owner = copyConstructor.owner;
        this.balances = new HashMap<>(copyConstructor.getBalances());
        this.previousState = new ArrayList<>();
        this.accountSnapshots = new HashMap<>(copyConstructor.accountSnapshots);
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        String currentOwner = this.owner;

        if (Objects.equals(owner, "") || owner == null) {
            throw new IllegalArgumentException("Owner must be not empty and null");
        } else {
            previousState.add(() -> this.owner = currentOwner);
            this.owner = owner;
        }
    }

    public HashMap<Currency, Integer> getBalances() {
        if (balances != null) {
            return new HashMap<>(balances);
        } else return null;
    }

    public static Account of(String number, String owner) {
        if (Objects.equals(owner, "") || owner == null) {
            throw new IllegalArgumentException("Owner must be not empty and null");
        } else return new Account(owner, number);
    }

    public void changeMoney(Currency currency, int amount) {
        if (amount >= 0) {
            if (balances.containsKey(currency)) {
                int val = balances.get(currency);
                previousState.add(() -> this.balances.put(currency, val));
                balances.put(currency, amount);
            } else {
                previousState.add(() -> this.balances.remove(currency));
                balances.put(currency, amount);
            }
        } else throw new IllegalArgumentException("Amount can't be below zero");
    }

    @Override
    public String toString() {
        return "Account{" + "number='" + number + '\'' + ", owner='" + owner + '\'' + ", balances=" + balances + ", previousState=" + previousState + ", accountSnapshots=" + accountSnapshots + '}';
    }

    public void undo() {
        if (previousState.size() > 0) {
            previousState.get(previousState.size() - 1).run();
            previousState.remove(previousState.size() - 1);
        } else throw new UnsupportedOperationException("No updates were made");
    }


    public void addSnapshot() {
        Account accountSnapshot = new Account(this);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        accountSnapshots.put(timeStamp, accountSnapshot);
    }

    public Account returnSnapshot(String time) {
        Account accountSnapshot = this.accountSnapshots.get(time);
        return new Account(accountSnapshot);
    }

}
