package com.nekonetwork.nekonetwork.modules.nekoduels.commands;

import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.command.NekoDuelsCommand;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaveQueueCommand extends NekoDuelsCommand {

    public LeaveQueueCommand(NekoDuels module, String name) {
        super(module, name);
    }

    @Override
    public void process(Player p, String label, String[] args) {
        boolean isQueued = false;
        for(Map.Entry<String, ArrayList<Player>> e : nekoDuels.helper.duelQueue.entrySet()) {
            String key = e.getKey();
            List<Player> value = new ArrayList<>(e.getValue());
            value.removeIf(p2 -> !(p.getUniqueId().toString().equals(p2.getUniqueId().toString())));

            if(!value.isEmpty()) {
                isQueued = true;
                nekoDuels.helper.duelQueue.get(key).remove(p);
                nekoDuels.helper.updateScoreboards();
                break;
            }
        }

        if(isQueued) {
            p.sendMessage("§aYou've left the queue-");
        } else {
            p.sendMessage("§cYou've aren't in a queue-");
        }
    }
}
