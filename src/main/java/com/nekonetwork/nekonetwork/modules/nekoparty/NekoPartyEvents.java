package com.nekonetwork.nekonetwork.modules.nekoparty;

import com.nekonetwork.nekonetwork.helpers.EventsListener;

public class NekoPartyEvents extends EventsListener {

    public NekoPartyEvents(NekoParty module) {
        super(module);
        this.module = module;
    }
}