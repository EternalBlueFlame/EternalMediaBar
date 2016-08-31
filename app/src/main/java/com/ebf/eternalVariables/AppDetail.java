package com.ebf.eternalVariables;

import java.io.Serializable;

public class AppDetail implements Serializable {
    private static final long serialVersionUID = 0L;

    public CharSequence label;
    public CharSequence subLabel;
    public String internalCommand = "";
    public String URI;
    public boolean isPersistent;

    //basic constructors for the class, allows us to set the default values without needing to fully instance the class
    public AppDetail(CharSequence label, String URI, boolean isPersistent){
        this.label = label;
        this.URI = URI;
        this.isPersistent = isPersistent;
    }
    public AppDetail(CharSequence label,CharSequence subLabel, String URI,  String internalCommand){
        this.label = label;
        this.URI = URI;
        this.internalCommand = internalCommand;
        this.subLabel = subLabel;
    }
    public AppDetail(){}

}


