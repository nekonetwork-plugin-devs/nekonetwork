package com.nekonetwork.nekonetwork.helpers;

import org.bukkit.event.Listener;

public class EventsListener implements Listener {

    public NekoModule module;
    public EventsListener(NekoModule module) {
        this.module = module;
    }
}
