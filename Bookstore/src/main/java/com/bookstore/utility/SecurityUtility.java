package com.bookstore.utility;

import java.security.SecureRandom;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class SecurityUtility {

    // BCryptPasswordEncoder bean for encoding passwords with strength 12 (work factor)
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // internally generates secure salt
    }

    // Generate a random secure password string (not a bean)
    public static String randomPassword() {
        final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder(18);

        for (int i = 0; i < 18; i++) {
            int index = random.nextInt(CHARACTERS.length());
            password.append(CHARACTERS.charAt(index));
        }

        return password.toString();
    }
}
