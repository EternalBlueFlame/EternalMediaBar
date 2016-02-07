package com.ebf.eternalmediabar;

import android.graphics.drawable.Drawable;
import java.io.Serializable;

public class AppDetail implements Serializable {
    private static final long serialVersionUID = 0L;

    CharSequence label;
    String name;
    Drawable icon;
    boolean isPersistent;
}


