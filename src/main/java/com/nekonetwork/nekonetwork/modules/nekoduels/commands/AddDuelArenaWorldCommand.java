package com.nekonetwork.nekonetwork.modules.nekoduels.commands;

import com.nekonetwork.nekonetwork.command.RequiredArgument;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.command.NekoDuelsCommand;
import com.nekonetwork.nekonetwork.modules.nekoduels.helpers.DuelType;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class AddDuelArenaWorldCommand extends NekoDuelsCommand {

    public AddDuelArenaWorldCommand(NekoDuels module, String name) {
        super(module, name);
        needsOp = true;
        requiredArguments.add(new RequiredArgument("string", "§cYou need to type in a duel type-"));
        requiredArguments.add(new RequiredArgument("string", "§cYou need to type in a world name-"));
    }

    @Override
    public void process(Player p, String label, String[] args) {
        String duelType = args[0];
        String worldName = args[1];

        if(!nekoDuels.helper.duelTypes.keySet().contains(duelType)) {
            p.sendMessage("§cInvalid duelType-");
            return;
        }

        List<World> a = new ArrayList<>(Bukkit.getServer().getWorlds());
        a.removeIf(w -> !(w.getName().equals(worldName)));
        if(a.isEmpty()) {
            p.sendMessage("§cInvalid worldName-");
            return;
        }

        DuelType duel = nekoDuels.helper.duelTypes.get(duelType);
        duel.arenaWorlds.add(worldName);

        p.sendMessage("§aAdded a new arena world for §c" + duelType + "- (currently " + duel.arenaWorlds.size() + ")");
    }
}
