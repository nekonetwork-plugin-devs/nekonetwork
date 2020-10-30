package com.nekonetwork.nekonetwork.helpers;

import com.google.gson.internal.LinkedTreeMap;
import com.nekonetwork.nekonetwork.NekoNetwork;
import com.nekonetwork.nekonetwork.webmessages.GenericDataJSON;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import java.util.List;

public abstract class NekoModule implements Listener {

    public String name;
    public NekoNetwork plugin;
    public EventsListener events;
    public boolean enabled = false;

    public NekoModule(NekoNetwork plugin, String name) {
        this.plugin = plugin;
        this.name = name;
    }

    public void loadConfig(FileConfiguration config) {
        List<String> a = (List<String>) config.getList("enabledModules");
        if(a.contains(name)) { enable(); }
    }

    public void editConfig(FileConfiguration config) {
        List<String> a = (List<String>) config.getList("enabledModules");
        if(enabled) { if(!a.contains(name)) { a.add(name); } } else { a.remove(name); }
        config.set("enabledModules", a);
    }

    public void enable() {
        enabled = true;
    }

    public void disable() {
        enabled = false;
        HandlerList.unregisterAll(events);
    }

    public void switchModule(boolean state) {
        if(state) { enable(); } else { disable(); }
    }

    public void handlePOST(GenericDataJSON body, LinkedTreeMap obj) {

    }

    public void handleGET(GenericDataJSON mi) {
        mi.data.put("name", name);
        mi.data.put("enabled", enabled);
    }
}
