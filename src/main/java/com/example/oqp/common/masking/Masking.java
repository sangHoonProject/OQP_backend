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
        String substring = userId.substring(0, 3);
        String masking = userId.substring(3).replaceAll(".", "*");
        return substring + masking;
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

}
