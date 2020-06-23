package com.harlie.dogs;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class FileUtil {
    private final static String TAG = "LEE: <" + FileUtil.class.getSimpleName() + ">";

    public String loadResource(String resource_name) {
        try {
            Context context = ApplicationProvider.getApplicationContext();
            InputStream in = context.getClassLoader().getResourceAsStream(resource_name);
            if (in != null) {
                String jsonInfo = convertStreamToString(in);
                System.out.println(TAG + "loadResource: jsonInfo=" + jsonInfo);
                return jsonInfo;
            }
        }
        catch (Exception e) {
            System.err.println(TAG + "*** ERROR: loadResource(" + resource_name + ") e=" + e);
        }
        return "";
    }

    public String convertStreamToString(InputStream is) {
        System.out.println(TAG + "convertStreamToString");
        StringBuilder buf = new StringBuilder();
        if (is != null) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String str;
            try {
                while ((str = reader.readLine()) != null) {
                    buf.append(str).append("\n");
                }
            }
            catch (IOException e) {
                System.err.println(TAG + "*** unable to read JSON test data! *** - e=" + e);
            }
        }
        else {
            System.err.println(TAG + "*** the InputStream is null! ***");
        }
        return buf.toString();
    }
}

