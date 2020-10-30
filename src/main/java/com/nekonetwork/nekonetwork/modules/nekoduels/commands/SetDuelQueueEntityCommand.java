package com.nekonetwork.nekonetwork.modules.nekoduels.commands;

import com.nekonetwork.nekonetwork.command.RequiredArgument;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.command.NekoDuelsCommand;
import org.bukkit.entity.Player;

public class SetDuelQueueEntityCommand extends NekoDuelsCommand {

    public SetDuelQueueEntityCommand(NekoDuels module, String name) {
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

        nekoDuels.helper.waitingDuelQueueEntities.put(p.getUniqueId().toString(), duelType);
        p.sendMessage("§aRight click on an entity you want to assign the queue to-");
    }
}
