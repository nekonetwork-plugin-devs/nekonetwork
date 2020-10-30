package com.nekonetwork.nekonetwork.web;

import com.google.gson.Gson;
import com.nekonetwork.nekonetwork.webmessages.GenericDataJSON;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class WebHelper {

    private WebHelper() {
        throw new IllegalStateException();
    }

    public static String getRequestBody(HttpExchange t) {
        try {
            InputStreamReader isr = new InputStreamReader(t.getRequestBody(), StandardCharsets.UTF_8);
            BufferedReader br = new BufferedReader(isr);

            int b;
            StringBuilder buf = new StringBuilder(512);
            while ((b = br.read()) != -1) {
                buf.append((char) b);
            }

            br.close();
            isr.close();

            return buf.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "{}";
        }
    }

    public static GenericDataJSON getRequestPostMessage(HttpExchange t) {
        String str = getRequestBody(t);
        Gson gson = new Gson();
        return gson.fromJson(str, GenericDataJSON.class);
    }
}
