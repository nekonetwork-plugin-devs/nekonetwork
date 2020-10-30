package com.nekonetwork.nekonetwork.webhandlers;

import com.google.gson.Gson;
import com.nekonetwork.nekonetwork.command.Command;
import com.nekonetwork.nekonetwork.helpers.NekoModule;
import com.nekonetwork.nekonetwork.web.WebServer;
import com.nekonetwork.nekonetwork.webmessages.GenericDataJSON;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import static org.bukkit.Bukkit.getLogger;

public class LoadHandler implements HttpHandler {

    public WebServer ws;
    public LoadHandler(WebServer ws) {
        this.ws = ws;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        GenericDataJSON o = new GenericDataJSON();
        o.data.put("ipAddress", Bukkit.getServer().getIp());
        o.data.put("ipAddress", String.class.cast(o.data.get("ipAddress")).length() < 1 ? "localhost:" + Bukkit.getServer().getPort() : o.data.get("ipAddress"));
        o.data.put("serverName", Bukkit.getServer().getServerName());
        o.data.put("modules", new HashMap<>());
        for(NekoModule module : ws.plugin.modules.values()) {
            GenericDataJSON mi = new GenericDataJSON();
            module.handleGET(mi);

            HashMap.class.cast(o.data.get("modules")).put(mi.data.get("name"), mi);
        }

        HashMap<String, GenericDataJSON> a = new HashMap<>();
        for(Command c : ws.plugin.commandManager.commands.values()) {
            GenericDataJSON ci = new GenericDataJSON();
            ci.data.put("name", c.name);
            ci.data.put("parentModule", c.module.name);
            ci.data.put("needsOp", c.needsOp);

            a.put(c.name, ci);
        }
        o.data.put("commands", a);

        HashMap<String, GenericDataJSON> b = new HashMap<>();
        for(NekoModule module : ws.plugin.modules.values()) {
            String className = "com.nekonetwork.nekonetwork.modules." + module.name.toLowerCase() + "." + StringUtils.capitalize(module.name) + "Events";
            try {
                Class<?> cl = Class.forName(className);

                GenericDataJSON li = new GenericDataJSON();
                List<String> b2 = new ArrayList<>();
                for(Method m : cl.getDeclaredMethods()) {
                    b2.add(m.getName());
                }
                li.data.put("name", cl.getName());
                li.data.put("parentModule", module.name);
                li.data.put("methods", b2.toArray());

                b.put(module.name, li);
            } catch (ClassNotFoundException e) {
                getLogger().log(Level.WARNING, String.format("Couldn't find the specified class: %s", className));
            }
        }
        o.data.put("listeners", b);

        Map<String, GenericDataJSON> c = new HashMap<>();
        for(Player p : Bukkit.getOnlinePlayers()) {
            GenericDataJSON pi = new GenericDataJSON();
            pi.data.put("name", p.getName());
            pi.data.put("isOp", p.isOp());

            c.put(p.getName(), pi);
        }
        o.data.put("players", c);

        Map<String, GenericDataJSON> d = new HashMap<>();
        for(World w : Bukkit.getWorlds()) {
            GenericDataJSON wi = new GenericDataJSON();
            wi.data.put("name", w.getName());

            d.put(w.getName(), wi);
        }
        o.data.put("worlds", d);

        o.data.put("maxPlayers", Bukkit.getServer().getMaxPlayers());
        o.data.put("version", Bukkit.getServer().getVersion());
        o.data.put("motd", Bukkit.getServer().getMotd());

        Gson gson = new Gson();
        String response = gson.toJson(o);

        t.getResponseHeaders().set("Content-Type", "application/json");
        t.sendResponseHeaders(200, response.length());

        OutputStream os = t.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }
}