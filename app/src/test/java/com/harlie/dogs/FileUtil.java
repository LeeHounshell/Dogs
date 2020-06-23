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
                return convertStreamToString(in);
            }
            else {
                System.out.println(TAG + "*** loadResource: UNABLE TO OPEN RESOURCE FILE: " + resource_name);
            }
        }
        catch (Exception e) {
            System.err.println(TAG + "*** ERROR: loadResource(" + resource_name + ") error=" + e);
        }
        return "";
    }

    private String convertStreamToString(InputStream is) {
        StringBuilder buf = new StringBuilder();
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String str;
        try {
            while ((str = reader.readLine()) != null) {
                buf.append(str).append("\n");
            }
        }
        catch (IOException e) {
            System.err.println(TAG + "*** convertStreamToString: unable to read JSON test data! error=" + e);
        }
        return buf.toString();
    }
}

