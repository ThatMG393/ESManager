package com.thatmg393.esmanager.classes;

import android.graphics.Color;

import java.util.regex.Pattern;

public class LuaSyntaxManager {

    public static final Pattern lua_keywords = Pattern.compile("\\b(and|end|in|repeat|break|false|local|return|do|for|nil|then|else|function|not|true|elseif|if|or|until|while)\\b");
    public static final Pattern lua_operators = Pattern.compile("\\b(<|>|<=|>=|~=|==)\\b");
    public static final Pattern lua_chars = Pattern.compile("'[a-zA-Z]'");

    public static final int lua_keywords_colors = Color.parseColor("#FF5252");
    public static final int lua_operators_colors = Color.parseColor("#EC407A");
    public static final int lua_chars_colors = Color.parseColor("#8bc34a");

}
