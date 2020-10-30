package com.nekonetwork.nekonetwork.modules.nekoduels.command;

import com.nekonetwork.nekonetwork.command.Command;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;

public class NekoDuelsCommand extends Command {

    public NekoDuels nekoDuels;
    public NekoDuelsCommand(NekoDuels module, String name) {
        super(module, name);
        nekoDuels = module;
    }
}
