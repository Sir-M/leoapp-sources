package de.slg.startseite;


import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import de.slg.leoapp.Start;
import de.slg.leoapp.Utils;

class SyncTaskName extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... params) {
        if (!Utils.checkNetwork())
            return null;

        BufferedReader in = null;
        String result = "";

        try {
            URL interfaceDB = new URL("http://www.moritz.liegmanns.de/getName.php?key=5453&userid=" + Utils.getUserID());

            in = new BufferedReader(new InputStreamReader(interfaceDB.openStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (!inputLine.contains("<"))
                    result += inputLine;
            }
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null)
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
        }

        if (result.startsWith("-"))
            return null;

        Start.pref.edit()
                .putString("pref_key_username_general", result)
                .apply();

        return null;
    }
}