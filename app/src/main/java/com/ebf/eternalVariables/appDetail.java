package com.ebf.eternalVariables;

import android.graphics.Bitmap;
import java.io.Serializable;

public class AppDetail implements Serializable {
    private static final long serialVersionUID = 0L;

    public CharSequence label;
    public String URI;
    public Bitmap icon;
    public boolean isPersistent;

    //basic constructors for the class, allows us to set the default values without needing to fully instance the class
    public AppDetail(CharSequence label, String URI, boolean isPersistent, Bitmap icon){
        this.label = label;
        this.URI = URI;
        this.isPersistent = isPersistent;
        this.icon = icon;
    }
    public AppDetail(CharSequence label, String URI, boolean isPersistent){
        this.label = label;
        this.URI = URI;
        this.isPersistent = isPersistent;
    }
    public AppDetail(){}

}


