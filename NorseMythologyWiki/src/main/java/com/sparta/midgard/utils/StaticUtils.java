package com.sparta.midgard.utils;

import java.util.regex.Pattern;

public class StaticUtils {
    public static String getPartialMatcherRegex(String searchTerm){
        return "(?i).*" + Pattern.quote(searchTerm) + ".*";
    }
}
