package com.xingy.lib.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

public class AlphaTextView extends TextView {
	
	
	public AlphaTextView(Context context) {
		super(context);
		init();
	}
	
	public AlphaTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public AlphaTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}
	
	
	private static Typeface s_AlphabetTypeface;
	public static Typeface getAlphabetTypeface(Context context) {
		if(s_AlphabetTypeface == null) {
			s_AlphabetTypeface = Typeface.createFromAsset(context.getApplicationContext().getAssets(), "fonts/alphabet.ttf");
		}
		return s_AlphabetTypeface;
	}
	private void init() {
		Typeface pTypeface = getAlphabetTypeface(getContext());
//		Typeface pTypeface = Typeface.createFromAsset(getContext().getApplicationContext().getAssets(), "fonts/alphabet.ttf");
		this.setTypeface(pTypeface);
	}

//	private boolean mOptional; // Indicates whether the default ttf is optional or not.
}
