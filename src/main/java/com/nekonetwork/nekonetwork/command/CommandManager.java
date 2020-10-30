package com.nekonetwork.nekonetwork.command;

import com.nekonetwork.nekonetwork.NekoNetwork;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoduels.commands.*;
import com.nekonetwork.nekonetwork.modules.nekoparty.NekoParty;
import com.nekonetwork.nekonetwork.modules.nekoparty.commands.PartyAcceptCommand;
import com.nekonetwork.nekonetwork.modules.nekoparty.commands.PartyCommand;
import com.nekonetwork.nekonetwork.modules.nekoparty.commands.PartyDenyCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import java.util.HashMap;
import java.util.Map;

public class CommandManager {

    public NekoNetwork plugin;
    public Map<String, Command> commands = new HashMap<>();

    public CommandManager(NekoNetwork plugin) {
        this.plugin = plugin;

        registerCommands();
    }

    public void registerCommands() {
        NekoDuels nekoDuels = (NekoDuels) plugin.modules.get("nekoDuels");
        commands.put("addDuelArenaWorld", new AddDuelArenaWorldCommand(nekoDuels, "addDuelArenaWorld"));
        commands.put("addDuelSpawn", new AddDuelSpawnCommand(nekoDuels, "addDuelSpawn"));
        commands.put("createDuelType", new CreateDuelTypeCommand(nekoDuels, "createDuelType"));
        commands.put("forceDuel", new ForceDuelCommand(nekoDuels, "forceDuel"));
        commands.put("leaveQueue", new LeaveQueueCommand(nekoDuels, "leaveQueue"));
        commands.put("setDuelLobbyWorld", new SetDuelLobbyWorldCommand(nekoDuels, "setDuelLobbyWorld"));
        commands.put("setDuelQueueEntity", new SetDuelQueueEntityCommand(nekoDuels, "setDuelQueueEntity"));
        commands.put("setDuelItems", new SetDuelItemsCommand(nekoDuels, "setDuelItems"));

        NekoParty nekoParty = (NekoParty) plugin.modules.get("nekoParty");
        commands.put("p", new PartyCommand(nekoParty, "p"));
        commands.put("paccept", new PartyAcceptCommand(nekoParty, "paccept"));
        commands.put("pdeny", new PartyDenyCommand(nekoParty, "pdeny"));
    }

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        Player p = (Player) sender;

        if(commands.containsKey(command.getName())) {
            Command command2 = commands.get(command.getName());
            if(command2.needsOp && !p.isOp()) {
                p.sendMessage("§cYou don't have enough permissions-");
                return true;
            }
            if(!command2.module.enabled) {
                p.sendMessage("Unknown command. Type \"/help\" for help.");
                return true;
            }
            if(args.length < command2.requiredArguments.size()) {
                p.sendMessage(command2.requiredArguments.get(args.length).message);
                return true;
            }

            command2.process(p, label, args);
            return true;
        } else {
            return false;
        }
    }
}
