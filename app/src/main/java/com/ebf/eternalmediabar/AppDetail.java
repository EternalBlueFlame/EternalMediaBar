package com.ebf.eternalmediabar;

/**
 * Created by Jonathan on 7/9/2015.
 */
import android.graphics.drawable.Drawable;
import java.io.Serializable;

public class AppDetail implements Serializable {
    private static final long serialVersionUID = 0L;

    CharSequence label;
    String name;
    Drawable icon;
    int ismenu; //use 0 for false, 1 for menu, 2 is null, and >2 for actual settings entries
}

