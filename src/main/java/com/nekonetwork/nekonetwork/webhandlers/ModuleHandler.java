package com.nekonetwork.nekonetwork.webhandlers;

import com.google.gson.internal.LinkedTreeMap;
import com.nekonetwork.nekonetwork.helpers.NekoModule;
import com.nekonetwork.nekonetwork.web.WebHelper;
import com.nekonetwork.nekonetwork.web.WebServer;
import com.nekonetwork.nekonetwork.webmessages.GenericDataJSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class ModuleHandler implements HttpHandler {

    public WebServer ws;
    public ModuleHandler(WebServer ws) {
        this.ws = ws;
    }

    @Override
    public void handle(HttpExchange t) {
        try {
            GenericDataJSON body = WebHelper.getRequestPostMessage(t);
            LinkedTreeMap obj = body.data.containsKey("obj") ? (LinkedTreeMap) body.data.get("obj") : null;
            LinkedTreeMap objData = obj != null ? (LinkedTreeMap) obj.get("data") : null;

            ModuleActions.handlePOST(body, objData);
            for (NekoModule module : ws.plugin.modules.values()) {
                module.handlePOST(body, objData);
            }

            t.sendResponseHeaders(200, 0);
            t.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}