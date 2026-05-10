package Utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public final class PasswordUtils {
    private static final BCryptPasswordEncoder BCRYPT = new BCryptPasswordEncoder();

    private PasswordUtils() {
    }

    public static boolean matches(String rawPassword, String storedPassword) {
        if (rawPassword == null || storedPassword == null || storedPassword.isBlank()) {
            return false;
        }
        if (storedPassword.startsWith("$2y$")) {
            storedPassword = "$2a$" + storedPassword.substring(4);
        }
        if (storedPassword.startsWith("$2a$") || storedPassword.startsWith("$2b$")) {
            return BCRYPT.matches(rawPassword, storedPassword);
        }
        return storedPassword.equals(rawPassword);
    }
}
