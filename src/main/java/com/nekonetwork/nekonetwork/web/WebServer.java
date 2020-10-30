package com.nekonetwork.nekonetwork.web;

import java.io.IOException;
import java.net.InetSocketAddress;
import com.nekonetwork.nekonetwork.NekoNetwork;
import com.nekonetwork.nekonetwork.webhandlers.*;
import com.sun.net.httpserver.HttpServer;

public class WebServer {

    public NekoNetwork plugin;
    public HttpServer server;

    public WebServer(NekoNetwork plugin) {
        this.plugin = plugin;
        ModuleActions.ws = this;
    }

    public void setup() {
        try {
            server = HttpServer.create(new InetSocketAddress(8001), 0);
            server.createContext("/app", new AppHandler(this));
            server.createContext("/load", new LoadHandler(this));
            server.createContext("/static", new StaticHandler(this));
            server.createContext("/module", new ModuleHandler(this));
            server.setExecutor(null); // creates a default executor
            server.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}