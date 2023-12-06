import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {

    @Test
    void setOwnerSuccess() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.setOwner("Timofey");
        assertEquals(account.getOwner(), "Timofey");
    }

    @Test
    void setOwnerFail1() {
        Account account = Account.of("123", "Ivan Rybnikov");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            account.setOwner("");
        });
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void setOwnerFail2() {
        Account account = Account.of("123", "Ivan Rybnikov");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            account.setOwner(null);
        });
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void getBalances() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney("USD", 1000);
        assertEquals(account.getBalances().get("USD"), 1000);
    }

    @Test
    void ofSuccess() {
        Account account = Account.of("123", "Ivan Rybnikov");
        assertEquals(account.getOwner(), "Ivan Rybnikov");
    }

    @Test
    void ofFail1() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Account.of("123", "");
        });
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void ofFail2() {
        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            Account.of("123", null);
        });
        Assertions.assertEquals("Owner must be not empty and null", thrown.getMessage());
    }

    @Test
    void addMoneySuccess() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney("USD", 1000);
        assertEquals(account.getBalances().get("USD"), 1000);
    }

    @Test
    void addMoneySuccessSameCurrency() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney("USD", 1000);
        account.changeMoney("USD", 1200);
        assertEquals(account.getBalances().get("USD"), 1200);
    }

    @Test
    void addMoneySuccessDifferentCurrency() {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney("USD", 1000);
        account.changeMoney("RUB", 1200);
        assertEquals(account.getBalances().get("USD"), 1000);
        assertEquals(account.getBalances().get("RUB"), 1200);
    }

    @Test
    void addMoneyFail1() {
        Account account = Account.of("123", "Ivan Rybnikov");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            account.changeMoney("USD", -5);
        });
        Assertions.assertEquals("Amount can't be below zero", thrown.getMessage());

    }

    @Test
    void addMoneyFail2() {
        Account account = Account.of("123", "Ivan Rybnikov");

        IllegalArgumentException thrown = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            account.changeMoney("USB", 1000);
        });
        assertEquals("Not correct currency", thrown.getMessage());

    }


    @Test
    void undoAddMoney() throws NoSuchFieldException, IllegalAccessException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney("USD", 1000);
        account.undo();
        assertEquals(account.getBalances(), null);
    }

    @Test
    void doubleUndoAddMoney() throws NoSuchFieldException, IllegalAccessException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.changeMoney("USD", 1000);
        account.changeMoney("RUB", 1000);
        account.changeMoney("USD", 1000);
        account.undo();
        account.undo();
        assertEquals(account.getBalances().get("USD"), 1000);
    }

    @Test
    void undoSetOwner() throws NoSuchFieldException, IllegalAccessException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.setOwner("Timofey");
        account.undo();
        assertEquals(account.getOwner(), "Ivan Rybnikov");
    }

    @Test
    void undoMixedDoubleNoCurrencyInitialState() throws NoSuchFieldException, IllegalAccessException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.setOwner("Timofey");
        account.changeMoney("USD", 1000);
        account.undo();
        account.undo();
        assertEquals(account.getOwner(), "Ivan Rybnikov");
        assertEquals(account.getBalances(), null);
    }

    @Test
    void undoFail() {
        Account account = Account.of("123", "Ivan Rybnikov");

        UnsupportedOperationException thrown = Assertions.assertThrows(UnsupportedOperationException.class, () -> {
            account.changeMoney("USD", 1000);
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
    void addSnapshot() throws InterruptedException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.addSnapshot();
        String key = (String) account.accountSnapshots.keySet().toArray()[0];
        Account account1 = account.accountSnapshots.get(key);
        assertEquals(account1.getOwner(), "Ivan Rybnikov");
    }

    @Test
    void immutableSnapshot() throws InterruptedException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.addSnapshot();
        String key = (String) account.accountSnapshots.keySet().toArray()[0];
        String ownerInitial = account.accountSnapshots.get(key).getOwner();
        account.setOwner("Timofey");
        String ownerAfterUpdate = account.accountSnapshots.get(key).getOwner();
        assertEquals(ownerInitial, ownerAfterUpdate);
    }

    @Test
    void returnSnapshot() throws InterruptedException {
        Account account = Account.of("123", "Ivan Rybnikov");
        account.addSnapshot();
        String key = (String) account.accountSnapshots.keySet().toArray()[0];
        account.setOwner("Timofey");
        Account accountSnapshot = account.returnSnapshot(key);
        assertEquals(accountSnapshot.getOwner(), "Ivan Rybnikov");
    }

}



