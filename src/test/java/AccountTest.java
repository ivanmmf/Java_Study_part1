import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void setOwnerSuccess() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.setOwner("Timofey");
        assertEquals(account.getOwner(), "Timofey");
    }

    @Test
    void setOwnerFailEmpty() {
        Account account = Account.of("123", "Ivan Rybnikov");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> account.setOwner(""));
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void setOwnerFailNull() {
        Account account = Account.of("123", "Ivan Rybnikov");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> account.setOwner(null));
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void getBalancesIncapsulation() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        HashMap<Currency, Integer> incapsulateCheck = account.getBalances();
        incapsulateCheck.put(Currency.valueOf("USD"), 2000);
        assertEquals(account.getBalances().get(Currency.USD), 1000);
    }

    @Test
    void ofSuccess() {
        Account account = Account.of("123", "Ivan Rybnikov");
        assertEquals(account.getOwner(), "Ivan Rybnikov");
    }

    @Test
    void ofFailEmpty() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> Account.of("123", ""));
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void ofFailNull() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> Account.of("123", null));
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void addMoneySuccess() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        assertEquals(account.getBalances().get(Currency.USD), 1000);
    }

    @Test
    void addMoneySuccessSameCurrency() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.changeMoney(Currency.valueOf("USD"), 1200);
        assertEquals(account.getBalances().get(Currency.USD), 1200);
    }

    @Test
    void addMoneySuccessDifferentCurrency() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.changeMoney(Currency.valueOf("RUB"), 1200);
        assertEquals(account.getBalances().get(Currency.USD), 1000);
        assertEquals(account.getBalances().get(Currency.RUB), 1200);
    }

    @Test
    void addMoneyFail() {
        Account account = Account.of("123", "Ivan Rybnikov");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> account.changeMoney(Currency.valueOf("USD"), -5));
        Assertions.assertEquals("Amount can't be below zero", thrown.getMessage());
    }

    @Test
    void undoAddMoney() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.undo();
        assertEquals(account.getBalances().size(), 0);
    }

    @Test
    void doubleUndoAddMoney() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.changeMoney(Currency.valueOf("RUB"), 1000);
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.undo();
        account.undo();
        assertEquals(account.getBalances().get(Currency.USD), 1000);
    }

    @Test
    void undoSetOwner() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.setOwner("Timofey");
        account.undo();
        assertEquals(account.getOwner(), "Ivan Rybnikov");
    }

    @Test
    void undoMixedDoubleNoCurrencyInitialState() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.setOwner("Timofey");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.undo();
        account.undo();
        assertEquals(account.getOwner(), "Ivan Rybnikov");
        assertEquals(account.getBalances().size(), 0);
    }

    @Test
    void undoFail() {
        Account account = Account.of("123", "Ivan Rybnikov");

        UnsupportedOperationException thrown = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            account.changeMoney(Currency.valueOf("USD"), 1000);
            account.undo();
            account.undo();
        });
        assertEquals("No updates were made", thrown.getMessage());

    }

    @Test
    void addSeveralSnapshots() throws InterruptedException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.addSnapshot();
        account.setOwner("Timofey");
        Thread.sleep(2000);
        account.addSnapshot();
        int size = account.accountSnapshots.size();
        assertEquals(size, 2);
    }

    @Test
    void addSnapshot() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.addSnapshot();
        String key = (String) account.accountSnapshots.keySet().toArray()[0];
        Account account1 = account.accountSnapshots.get(key);
        assertEquals(account1.getOwner(), "Ivan Rybnikov");
    }

    @Test
    void immutableSnapshot() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.addSnapshot();
        String key = (String) account.accountSnapshots.keySet().toArray()[0];
        String ownerInitial = account.accountSnapshots.get(key).getOwner();
        account.setOwner("Timofey");
        String ownerAfterUpdate = account.accountSnapshots.get(key).getOwner();
        assertEquals(ownerInitial, ownerAfterUpdate);
    }

    @Test
    void returnSnapshot() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.addSnapshot();
        String key = (String) account.accountSnapshots.keySet().toArray()[0];
        account.setOwner("Timofey");
        Account accountSnapshot = account.returnSnapshot(key);
        assertEquals(accountSnapshot.getOwner(), "Ivan Rybnikov");
    }

}



