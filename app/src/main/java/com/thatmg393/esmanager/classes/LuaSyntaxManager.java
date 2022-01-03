package com.thatmg393.esmanager.classes;

import java.util.regex.Pattern;

public class LuaSyntaxManager {

    private static final Pattern lua_keywords = Pattern.compile("\\b(and|end|in|repeat|break|false|local|return|do|for|nil|then|else|function|not|true|elseif|if|or|until|while)\\b");
    private static final Pattern lua_operators = Pattern.compile("\\b(<|>|<=|>=|~=|==)\\b");
    private static final Pattern lua_chars = Pattern.compile("'[a-zA-Z]'");
    private static final Pattern lua_strings = Pattern.compile("");

}
