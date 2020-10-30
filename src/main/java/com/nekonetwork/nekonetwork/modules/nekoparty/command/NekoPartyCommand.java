package com.nekonetwork.nekonetwork.modules.nekoparty.command;

import com.nekonetwork.nekonetwork.command.Command;
import com.nekonetwork.nekonetwork.modules.nekoparty.NekoParty;

public class NekoPartyCommand extends Command {

    public NekoParty nekoParty;
    public NekoPartyCommand(NekoParty module, String name) {
        super(module, name);
        nekoParty = module;
    }
}
