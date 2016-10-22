package com.ebf.eternalVariables;

import java.io.Serializable;

public class AppDetail implements Serializable {
    private static final long serialVersionUID = 0L;

    public CharSequence label = "";
    public CharSequence subLabel = "";
    public String internalCommand = "";
    public String URI = "";

    //basic constructors for the class, allows us to set the default values without needing to fully instance the class
    public AppDetail(CharSequence label, String URI){
        this.label = label;
        this.URI = URI;
    }
    public AppDetail(CharSequence label,CharSequence subLabel, String URI,  String internalCommand){
        this.label = label;
        this.URI = URI;
        this.internalCommand = internalCommand;
        this.subLabel = subLabel;
    }
    public AppDetail(){}


    public AppDetail setCommand(String command){
        return new AppDetail(this.label, this.subLabel, this.URI, command);
    }
    public AppDetail setURI(String URI){
        return new AppDetail(this.label, this.subLabel, URI, this.internalCommand);
    }
}


