package com.ebf.eternalVariables;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;

public class webSearchResults extends AsyncTask<String,Integer,String>{


    @Override
    protected String doInBackground(String... query) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + query[0]).openStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
            reader.close();

            return sb.toString();
        }
        catch(Exception e){e.printStackTrace();
            return "failed to do anything";
        }
    }
}
