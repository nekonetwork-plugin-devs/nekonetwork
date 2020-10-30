package com.nekonetwork.nekonetwork.modules.nekoduels.commands;

import com.nekonetwork.nekonetwork.command.RequiredArgument;
import com.nekonetwork.nekonetwork.helpers.Utils;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.command.NekoDuelsCommand;
import com.nekonetwork.nekonetwork.modules.nekoduels.helpers.DuelType;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class CreateDuelTypeCommand extends NekoDuelsCommand {

    public CreateDuelTypeCommand(NekoDuels module, String name) {
        super(module, name);
        needsOp = true;
        requiredArguments.add(new RequiredArgument("string", "§cYou need to type in a name-"));
    }

    @Override
    public void process(Player p, String label, String[] args) {
        String dtName = args[0];

        if(nekoDuels.helper.duelTypes.keySet().contains(dtName)) {
            p.sendMessage("§cduelType with this name already exists-");
            return;
        }

        DuelType duelType = new DuelType();
        duelType.name = dtName;
        duelType.requiredPlayers = 2;
        duelType.arenaWorlds.add(p.getWorld().getName());
        duelType.spawns.add(Utils.getStringFromLocation(p.getLocation()));
        duelType.items = Utils.getStringFromInventory(p.getInventory());

        nekoDuels.helper.duelTypes.put(duelType.name, duelType);
        nekoDuels.helper.duelQueue.put(duelType.name, new ArrayList<>());
        p.sendMessage("§aCreated duelType - " + duelType + "-");
    }
}
