package Utils;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PasswordUtilsTest {

    @Test
    void matchesSupportsPlainTextFallback() {
        assertTrue(PasswordUtils.matches("secret", "secret"));
        assertFalse(PasswordUtils.matches("secret", "other"));
    }

    @Test
    void matchesSupportsSymfonyBcryptPrefix() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String hash = encoder.encode("secret");
        String symfonyHash = "$2y$" + hash.substring(4);

        assertTrue(PasswordUtils.matches("secret", symfonyHash));
        assertFalse(PasswordUtils.matches("wrong", symfonyHash));
    }
}
