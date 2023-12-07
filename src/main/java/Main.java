public class Main {
    public static void main(String[] args) {


        Account account = Account.of("12345", "Ivan Rybnikov");
        account.setOwner("Timofey");
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.changeMoney(Currency.valueOf("RUB"), 1000);
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.changeMoney(Currency.valueOf("USD"), 1000);
        account.undo();
        account.undo();
        System.out.println(account);
    }
}