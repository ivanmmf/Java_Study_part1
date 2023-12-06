import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;


public class Account {

    private final String number;

    private String owner;
    private final HashMap<String, Integer> balances;
    List<HashMap<String, Object>> updateHistory;

    HashMap<String, Account> accountSnapshots;


    private Account(String owner, String number) {
        this.number = number;
        this.balances = new HashMap<>();
        this.updateHistory = new ArrayList<>();
        this.accountSnapshots = new HashMap<>();
        HashMap<String, Object> initialAccount = new HashMap<>();
        initialAccount.put("owner", owner);
        initialAccount.put("balances", null);
        updateHistory.add(initialAccount);
        this.owner = owner;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        String fieldName = "owner";
        Object value = this.owner;
        HashMap<String, Object> changeOwner = new HashMap<>();
        changeOwner.put(fieldName, value);


        if (Objects.equals(owner, "") || owner == null) {
            throw new IllegalArgumentException("Owner must be not empty and null");
        } else
            updateHistory.add(changeOwner);
        this.owner = owner;
    }

    public HashMap<String, Integer> getBalances() {
        if (balances != null) {
            return new HashMap<>(balances);
        } else return null;
    }

    public static Account of(String number, String owner) {
        if (Objects.equals(owner, "") || owner == null) {
            throw new IllegalArgumentException("Owner must be not empty and null");
        } else return new Account(owner, number);
    }

    public void changeMoney(String currency, int amount) {
        String fieldName = "balances";
        HashMap<String, Integer> value = (HashMap<String, Integer>) this.balances.clone();
        HashMap<String, Object> changeBalances = new HashMap<>();
        changeBalances.put(fieldName, value);


        if (Currency.isInEnum(currency)) {
            if (amount >= 0) {
                if (balances.size() > 0) {
                    updateHistory.add(changeBalances);
                }
                balances.put(currency, amount);
            } else throw new IllegalArgumentException("Amount can't be below zero");
        } else throw new IllegalArgumentException("Not correct currency");
    }


    @Override
    public String toString() {
        return "Account{" +
                "number='" + number + '\'' +
                ", owner='" + owner + '\'' +
                ", balances=" + balances +
                ", updateHistory=" + updateHistory +
                ", accountSnapshots=" + accountSnapshots +
                '}';
    }

    public void undo() throws NoSuchFieldException, IllegalAccessException {
        if (updateHistory.size() > 0) {
            HashMap<String, Object> previousVersion = updateHistory.get(updateHistory.size() - 1);
            updateHistory.remove(updateHistory.size() - 1);
            List<String> names = Stream.of(this.getClass().getDeclaredFields()).map(Field::getName).toList();


            for (String s : previousVersion.keySet()) {
                for (String f : names) {
                    if (Objects.equals(s, f)) {
                        Field field = this.getClass().getDeclaredField(f);
                        field.setAccessible(true);
                        field.set(this, previousVersion.get(s));
                    }
                }

            }

        } else throw new UnsupportedOperationException("No updates were made");
    }


    public Account(Account s) {
        this.number = s.number;
        this.owner = s.owner;
        this.balances = new HashMap<>(s.getBalances());
        this.updateHistory = new ArrayList<>(s.updateHistory);
        this.accountSnapshots = new HashMap<>(s.accountSnapshots);


    }

    public void addSnapshot() {
        Account accountSnapshot = new Account(this);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
        accountSnapshots.put(timeStamp, accountSnapshot);
    }

    public Account returnSnapshot(String time) {
        Account accountSnapshot = this.accountSnapshots.get(time);
        Account account = new Account(accountSnapshot);
        return account;
    }

}
