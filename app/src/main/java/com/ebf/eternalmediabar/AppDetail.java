package com.ebf.eternalmediabar;

import android.graphics.Bitmap;
import java.io.Serializable;

public class appDetail implements Serializable {
    private static final long serialVersionUID = 0L;

    CharSequence label;
    String name;
    Bitmap icon;
    boolean isPersistent;
}


