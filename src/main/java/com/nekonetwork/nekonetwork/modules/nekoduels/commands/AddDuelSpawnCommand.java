package com.nekonetwork.nekonetwork.modules.nekoduels.commands;

import com.nekonetwork.nekonetwork.command.RequiredArgument;
import com.nekonetwork.nekonetwork.helpers.Utils;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.command.NekoDuelsCommand;
import com.nekonetwork.nekonetwork.modules.nekoduels.helpers.DuelType;
import org.bukkit.entity.Player;

public class AddDuelSpawnCommand extends NekoDuelsCommand {

    public AddDuelSpawnCommand(NekoDuels module, String name) {
        super(module, name);
        needsOp = true;
        requiredArguments.add(new RequiredArgument("string", "§cYou need to type in a duel type-"));
    }

    @Override
    public void process(Player p, String label, String[] args) {
        String duelType = args[0];

        if(!nekoDuels.helper.duelTypes.keySet().contains(duelType)) {
            p.sendMessage("§cInvalid duelType-");
            return;
        }

        DuelType duel = nekoDuels.helper.duelTypes.get(duelType);
        duel.spawns.add(Utils.getStringFromLocation(p.getLocation()));

        p.sendMessage("§aCreated a new spawn for §c" + duelType + "- (currently " + duel.spawns.size() + ")");
    }
}
