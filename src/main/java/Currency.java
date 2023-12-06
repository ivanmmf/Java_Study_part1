import java.util.Arrays;

public enum Currency {
    USD,RUB,EUR;

    public static boolean isInEnum(String value) {
        return Arrays.stream(Currency.values()).anyMatch(e -> e.name().equals(value));
    }
}
