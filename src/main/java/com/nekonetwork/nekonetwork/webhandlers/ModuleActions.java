package com.nekonetwork.nekonetwork.webhandlers;

import com.google.gson.internal.LinkedTreeMap;
import com.nekonetwork.nekonetwork.web.WebServer;
import com.nekonetwork.nekonetwork.webmessages.GenericDataJSON;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ModuleActions {

    private ModuleActions() { throw new IllegalStateException(); }

    public static WebServer ws;
    public static void handlePOST(GenericDataJSON body, LinkedTreeMap objData) {
        switch ((String) body.data.get("type")) {
            case "switchModule":
                ws.plugin.modules.get(body.data.get("name")).switchModule((boolean) body.data.get("enabled"));
                break;

            case "submit-broadcastMessage":
                Bukkit.getServer().broadcastMessage((String) objData.get("message"));
                break;

            case "submit-killPlayer":
                Bukkit.getServer().getPlayer((String) objData.get("username")).setHealth(0);
                break;

            case "submit-fakeKillPlayer":
                try {
                    Player p = Bukkit.getServer().getPlayer((String) objData.get("username"));
                    Player sourcePlayer = Bukkit.getServer().getPlayer((String) objData.get("className"));

                    if(sourcePlayer != null) {
                        p.damage(999 , sourcePlayer);
                    } else {
                        Class<? extends Entity> cl = (Class<? extends Entity>) Class.forName("org.bukkit.entity." + objData.get("className"));
                        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(ws.plugin, new Runnable() {
                            @Override
                            public void run() {
                                Entity e = p.getWorld().spawn(p.getLocation(), cl);
                                p.damage(999 , e);
                                e.remove();
                            }
                        }, 0L);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;

            case "submit-executeCommand":
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), (String) objData.get("command"));
                break;
        }
    }
}
