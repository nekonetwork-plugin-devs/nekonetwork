package com.nekonetwork.nekonetwork.helpers;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static org.bukkit.Bukkit.getServer;

public class Utils {

    private Utils() { throw new IllegalStateException(); }

    public static String getPlayerNames(List<Player> players) {
        String playerNames = "";
        for(Player p : players) {
            playerNames += p.getName() + ",";
        }
        playerNames = playerNames.endsWith(",") ? playerNames.substring(0, playerNames.length() - 1) : playerNames;

        return playerNames;
    }

    public static String getStringFromLocation(Location loc) {
        DecimalFormat df = new DecimalFormat("0.00");
        return df.format(loc.getX()) + ">" + df.format(loc.getY()) + ">" + df.format(loc.getZ());
    }

    public static Location getLocationFromString(String worldName, String loc) {
        String[] loc2 = loc.split(">");
        return new Location(getServer().getWorld(worldName), Double.parseDouble(loc2[0]), Double.parseDouble(loc2[1]), Double.parseDouble(loc2[2]));
    }

    public static List<String> getStringFromInventory(Inventory inv) {
        List<String> items = new ArrayList<>();
        Integer i = 0;
        for(ItemStack stack : inv.getContents()) {
            if(stack != null && stack.getType() != Material.AIR) {
                items.add(stack.getType().toString() + ">" + stack.getAmount() + ">" + i);
            }

            i++;
        }

        return items;
    }

    public static void sendCountdown(List<Player> players, String title, Integer time) {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                for(Player p : players) {
                    p.sendTitle(title, "-", 0, 20, 0);
                }
            }
        }, time * 1000L);
    }
}
