package org.tcollignon.user.utils;

import java.util.Base64;
import java.util.Optional;

public class StringUtils {

    public static final String BASIC = "Basic%20";

    public static String removeLastCharOptional(String s) {
        return Optional.ofNullable(s)
            .filter(str -> str.length() != 0)
            .map(str -> str.substring(0, str.length() - 1))
            .orElse(s);
    }

    public static String format(float amount) {
        if (amount == (int) amount) {
            return String.format("%d", (int) amount);
        } else {
            return String.format("%s", amount);
        }
    }

    public static String getLoginFromAuth(String auth) {
        String decodedAuth = getDecodedAuth(auth);
        return decodedAuth.split(":")[0];
    }

    public static String getDecodedAuth(String auth) {
        String authWithoutBasicMention = auth.replace(BASIC, "");
        return new String(Base64.getDecoder().decode(authWithoutBasicMention));
    }

    public static boolean isNullOrEmpty(String s) {
        return s == null || s.isEmpty();
    }
}
