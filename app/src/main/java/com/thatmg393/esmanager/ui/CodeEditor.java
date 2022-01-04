package com.thatmg393.esmanager.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.Spanned;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import androidx.annotation.NonNull;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.text.TextWatcher;

class ColorScheme {
    final Pattern pattern;
    final int color;

    ColorScheme(Pattern pattern, int color) {
        this.pattern = pattern;
        this.color = color;
    }
}

public class CodeEditor extends androidx.appcompat.widget.AppCompatEditText {

    private Rect rect;
    private Paint paint;
    private Context context;

    private String keywords_color = "#ffff4444";


    public CodeEditor(@NonNull Context context) {
        super(context);
        this.context = context;
        initSyntaxHighlight();
    }

    public CodeEditor(@NonNull Context context, AttributeSet attrs) {
        super(context,attrs);
        this.context = context;
        initSyntaxHighlight();
    }

    public CodeEditor(@NonNull Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
        this.context = context;
        initSyntaxHighlight();
    }

    private final void initSyntaxHighlight()
    {
		/*
		 Source: https://stackoverflow.com/questions/42786493/syntax-highlighting-on-android-edittext-using-span
		*/

        System.out.println("Syntax Highlighting Engine Initializing");

        final ColorScheme keywords = new ColorScheme(
                Pattern.compile("\\b(and|end|in|repeat|break|false|local|return|do|for|nil|then|else|function|not|true|elseif|if|or|until|while)\\b"),
                Color.parseColor("#CC7832"));


        final ColorScheme operators = new ColorScheme(
                Pattern.compile("\\b(<|>|<=|>=|~=|==)\\b"),
                Color.parseColor("#A9B7C6")
        );

        final ColorScheme strings = new ColorScheme(
            Pattern.compile("'[a-zA-Z]'"),Color.parseColor("#60864C")
        );

        final ColorScheme[] schemes = {keywords, operators};

        this.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                removeSpans(s, ForegroundColorSpan.class);

                for (ColorScheme scheme : schemes)
                {
                    for (Matcher m = scheme.pattern.matcher(s); m.find();)
                    {
                        s.setSpan(new ForegroundColorSpan(scheme.color),
                                m.start(),
                                m.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }
        });

        //Init Complete!
        System.out.println("Syntax Highlighting Engine Initialized");
        init();
    }

    void removeSpans(Editable e, Class<? extends CharacterStyle> type) {
        CharacterStyle[] spans = e.getSpans(0, e.length(), type);
        for (CharacterStyle span : spans) {
            e.removeSpan(span);
        }
    }

    private final void init() {

        System.out.println("Main Initializing");

        //useless?
        rect = new Rect();

        //The line number background
        paint = new Paint(paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        paint.setTextSize(20);
        paint.setTypeface(Typeface.MONOSPACE);

        //Removes underline
        this.setBackgroundColor(Color.TRANSPARENT);

        System.out.println("Main Initialized");
    }

    @Override
    protected void onDraw(Canvas canvas) {

        int baseline;
        int lineCount = getLineCount();
        int lineNumber = 1;

        for (int i = 0; i < lineCount; ++i)
        {
            baseline = getLineBounds(i, null);

            if (i == 0)
            {
                canvas.drawText("" + lineNumber, rect.left, baseline, paint);
                ++lineNumber;
            }

            else if (getText().charAt(getLayout().getLineStart(i) - 1) == '\n')
            {
                canvas.drawText("" + lineNumber, rect.left, baseline, paint);
                ++lineNumber;
            }
        }

        if(lineCount < 100)
        {
            setPadding(45, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }

        else if(lineCount > 99 && lineCount < 1000)
        {
            setPadding(63, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }

        else if(lineCount > 999 && lineCount < 10000)
        {
            setPadding(73, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }

        else if(lineCount > 9999 && lineCount < 100000)
        {
            setPadding(83, getPaddingTop(), getPaddingRight(), getPaddingBottom());
        }

        super.onDraw(canvas);
    }
}