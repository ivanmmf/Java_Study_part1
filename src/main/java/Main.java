public class Main {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException, InterruptedException {


        Account account = Account.of("12345", "Ivan Rybnikov");
        account.changeMoney("EUR", 1000);
        account.addSnapshot();
        String key = (String) account.accountSnapshots.keySet().toArray()[0];
        account.setOwner("Timofey");
        account.returnSnapshot(key);
        System.out.println(account.returnSnapshot(key));




    }
}