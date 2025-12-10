package amc.helperUtils;

import java.util.regex.Pattern;

/** This class provides simple input checks for common fields. */
public final class InputValidator {
    private InputValidator() {} // Prevents instantiation.

    /** Returns true if text is not null and not empty after trim. */
    public static boolean notEmpty(String text) {
        return text != null && text.trim().length() > 0;
    }

    /** Returns true if text looks like a basic email address. */
    public static boolean isEmail(String text) {
        if (!notEmpty(text)) return false;
        String simpleEmailRegex = "^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$";
        return Pattern.matches(simpleEmailRegex, text);
    }

    /** Returns true if text has 7–15 digits after removing non-digits. */
    public static boolean isPhoneSimple(String text) {
        if (!notEmpty(text)) return false;
        String digitsOnly = text.replaceAll("\\D", "");
        return digitsOnly.length() >= 7 && digitsOnly.length() <= 15;
    }

    /** Returns true if text length is at least min after trim. */
    public static boolean minLength(String text, int min) {
        if (text == null) return false;
        return text.trim().length() >= min;
    }
}