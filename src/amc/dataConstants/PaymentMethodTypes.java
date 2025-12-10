package amc.dataConstants;

/** This class holds payment method strings. It should not be instantiated. */
public final class PaymentMethodTypes {
    private PaymentMethodTypes() {} // Prevents creating this utility class.

    public static final String cash    = "CASH";
    public static final String card    = "CARD";
    public static final String ewallet = "EWALLET";
}