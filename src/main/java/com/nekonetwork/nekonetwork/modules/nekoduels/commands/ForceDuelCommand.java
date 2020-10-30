package com.nekonetwork.nekonetwork.modules.nekoduels.commands;

import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.command.NekoDuelsCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import java.util.ArrayList;

public class ForceDuelCommand extends NekoDuelsCommand {

    public ForceDuelCommand(NekoDuels module, String name) {
        super(module, name);
        needsOp = true;
    }

    @Override
    public void process(Player p, String label, String[] args) {
        ArrayList<Player> players = new ArrayList<>();

        for(String s : args) {
            if(Bukkit.getServer().getPlayer(s) == null) {
                p.sendMessage("§cCouldn't find player with name " + s + "-");
                return;
            }
        }

        nekoDuels.helper.createDuel(players, "classic");
    }
}
