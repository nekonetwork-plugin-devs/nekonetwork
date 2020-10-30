package com.nekonetwork.nekonetwork.webhandlers;

import com.nekonetwork.nekonetwork.web.WebServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

public class AppHandler implements HttpHandler {

    public WebServer ws;
    public AppHandler(WebServer ws) {
        this.ws = ws;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        String response = new String(Files.readAllBytes(Paths.get(ws.plugin.getDataFolder() + "/web/index.html")));
        t.sendResponseHeaders(200, response.length());
        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}