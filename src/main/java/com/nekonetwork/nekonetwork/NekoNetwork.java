package com.nekonetwork.nekonetwork;

import com.nekonetwork.nekonetwork.command.CommandManager;
import com.nekonetwork.nekonetwork.config.ConfigManager;
import com.nekonetwork.nekonetwork.helpers.NekoModule;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import com.nekonetwork.nekonetwork.modules.nekoparty.NekoParty;
import com.nekonetwork.nekonetwork.web.WebServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.HashMap;
import java.util.Map;

public class NekoNetwork extends JavaPlugin implements Listener {

    public WebServer ws;
    public ConfigManager config;
    public CommandManager commandManager;
    public Map<String, NekoModule> modules = new HashMap<>();

    @Override
    public void onEnable() {
        if (!Bukkit.getPluginManager().isPluginEnabled("HolographicDisplays")) {
            getLogger().severe("*** HolographicDisplays is not installed or not enabled. ***");
            getLogger().severe("*** This plugin will be disabled. ***");
            this.setEnabled(false);
            return;
        }

        //Setup Web Server
        ws = new WebServer(this);
        ws.setup();

        //Setup Modules
        NekoDuels nekoDuels = new NekoDuels(this, "nekoDuels");
        modules.put(nekoDuels.name, nekoDuels);

        NekoParty nekoParty = new NekoParty(this, "nekoParty");
        modules.put(nekoParty.name, nekoParty);

        //Setup Config Manager
        config = new ConfigManager(this);
        config.loadConfig();

        //Setup Command Manager
        commandManager = new CommandManager(this);
    }

    @Override
    public void onDisable() {
        System.out.println("Saving config...");
        ws.server.stop(0);
        config.editConfig();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandManager.onCommand(sender, command, label, args);
    }
}
