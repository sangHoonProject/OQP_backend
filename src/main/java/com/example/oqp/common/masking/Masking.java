package com.example.oqp.common.masking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
@Slf4j
public class Masking {

    public String maskingUserId(String userId){
        String regex = "^[a-zA-Z][a-zA-Z0-9_-]{4,19}$";

        Matcher matcher = Pattern.compile(regex).matcher(userId);
        if(matcher.find()){
            int length = userId.length();

            if(length <= 1){
                return userId;
            }

            char[] chars = new char[length - 1];
            Arrays.fill(chars, '*');
            return userId.replaceAll(userId, userId.substring(0, 1) + String.valueOf(chars));
        }

        return userId;
    }

    public String maskingEmail(String email) {

        String regex = "([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+)\\.([a-zA-Z]{2,})$";

        Matcher matcher = Pattern.compile(regex).matcher(email);
        if(matcher.find()) {
            String target = matcher.group(1);
            int length = target.length();

            if(length > 3){
                char[] chars = new char[length - 3];
                Arrays.fill(chars, '*');

                return email.replaceAll(target, target.substring(0, 3) + String.valueOf(chars));
            }
        }

        return email;
    }

    public String maskingPassword(String password) {
        String masking = password.replaceAll(password, "*");
        return masking;
    }
}
