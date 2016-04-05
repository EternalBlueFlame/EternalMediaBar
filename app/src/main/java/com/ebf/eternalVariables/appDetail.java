package com.ebf.eternalVariables;

import android.graphics.Bitmap;
import java.io.Serializable;

public class appDetail implements Serializable {
    private static final long serialVersionUID = 0L;

    public CharSequence label;
    public String name;
    public Bitmap icon;
    public boolean isPersistent;
}


