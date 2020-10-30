package com.nekonetwork.nekonetwork.modules.nekoduels.commands;

import com.nekonetwork.nekonetwork.command.RequiredArgument;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.command.NekoDuelsCommand;
import org.bukkit.entity.Player;

public class SetDuelLobbyWorldCommand extends NekoDuelsCommand {

    public SetDuelLobbyWorldCommand(NekoDuels module, String name) {
        super(module, name);
        needsOp = true;
        requiredArguments.add(new RequiredArgument("string", "§cYou need to type in a world name-"));
    }

    @Override
    public void process(Player p, String label, String[] args) {
        String worldName = args[0];

        nekoDuels.helper.duelLobbyWorld = worldName;
        p.sendMessage("§aSet duel world lobby to §c" + worldName + "§a-");
    }
}
