package com.ebf.eternalVariables;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;

public class webSearchResults extends AsyncTask<String,Integer,String>{


    @Override
    protected String doInBackground(String... query) {
        try {
            URL url = new URL("http://ajax.googleapis.com/ajax/services/search/web?v=1.0&q=" + URLEncoder.encode(query[0], "UTF-8"));
            BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));

            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
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
