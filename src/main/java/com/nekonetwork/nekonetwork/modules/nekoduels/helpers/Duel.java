package com.nekonetwork.nekonetwork.modules.nekoduels.helpers;

import com.nekonetwork.nekonetwork.helpers.Utils;
import com.nekonetwork.nekonetwork.modules.nekoduels.NekoDuels;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.*;
import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.Bukkit.getScoreboardManager;
import static org.bukkit.Bukkit.getServer;

public class Duel {

    public NekoDuels module;
    public String id;
    public Integer teamID;

    public String type;
    public List<String> spawns = new ArrayList<>();
    public Map<String, ItemStack[]> previousItems = new HashMap<>();
    public List<String> items = new ArrayList<>();
    public List<String> arenaWorlds = new ArrayList<>();
    public List<Player> players = new ArrayList<>();
    public List<Player> deadPlayers = new ArrayList<>();

    public boolean started = false;
    public boolean ended = false;
    public Integer timeElapsed = 0;

    public Scoreboard scoreboard;

    public Duel(NekoDuels module) {
        this.module = module;
    }

    public void processPre() {
        //Check if duel is valid
        if(arenaWorlds.isEmpty()) {
            getServer().broadcastMessage("§cCouldn't teleport players, because there isn't any ArenaWorld-");
            return;
        }
        String worldName = arenaWorlds.get(new Random().nextInt(arenaWorlds.size()));
        if(getServer().getWorld(worldName) == null) {
            getServer().broadcastMessage("§cCouldn't teleport players, because ArenaWorld (name: " + worldName + ") doesn't exist-");
            return;
        }

        //Teleport players
        int i = 0;
        for(Player p : players) {
            if(spawns.size() <= i) {
                getServer().broadcastMessage("§cCouldn't teleport " + p.getName() + ", because there aren't enough spawns-");
                return;
            } else {
                String spawn = spawns.get(i);
                p.teleport(Utils.getLocationFromString(worldName, spawn));
            }

            i++;
        }

        //Set attributes
        for(Player p : players) {
            p.getInventory().clear();

            module.helper.playersNonReady.add(p.getUniqueId().toString());
            p.setHealth(p.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
            p.setFoodLevel(20);
        }

        //Set items
        setItems();

        //Start countdown
        Utils.sendCountdown(players, "§aStarting in §c5§a...", 0);
        Utils.sendCountdown(players, "§aStarting in §c4§a...", 1);
        Utils.sendCountdown(players, "§aStarting in §c3§a...", 2);
        Utils.sendCountdown(players, "§aStarting in §c2§a...", 3);
        Utils.sendCountdown(players, "§aStarting in §c1§a...", 4);
    }

    public void processPost() {
        started = true;
        for(Player p : players) {
            module.helper.playersNonReady.remove(p.getUniqueId().toString());
            module.helper.playersInGame.add(p.getUniqueId().toString());
        }

        Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(module.plugin, new Runnable() {
            @Override
            public void run() {
                updateScoreboards();
            }
        }, 0L);
        timeScoreboards();
    }

    public void setItems() {
        for(Player p : players) {
            previousItems.put(p.getUniqueId().toString(), p.getInventory().getContents());

            for(String s : items) {
                Material mat = Material.getMaterial(s.substring(0, s.indexOf('>')));
                Integer size = Integer.parseInt(s.substring(s.indexOf('>') + 1, s.lastIndexOf('>')));
                Integer index = Integer.parseInt(s.substring(s.lastIndexOf('>') + 1));

                ItemStack item = new ItemStack(mat, size);
                p.getInventory().setItem(index, item);
            }
        }
    }

    public void timeScoreboards() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                timeElapsed += 1;
                updateScoreboards();

                if(!ended) {
                    timeScoreboards();
                }
            }
        }, 1000L);
    }

    public void updateScoreboards() {
        if(ended) { return; }
        if(scoreboard == null) {
            scoreboard = getServer().getScoreboardManager().getNewScoreboard();
        }
        if(scoreboard.getTeams().isEmpty()) {
            scoreboard.registerNewTeam("duels-" + teamID);
        }

        Team team = (Team) scoreboard.getTeams().toArray()[0];
        for(Player p : players) {
            if(!team.hasEntry(p.getName())) {
                team.addEntry(p.getName());
            }
        }

        Objective obj1 = scoreboard.getObjective("title");
        if(obj1 != null) {
            obj1.unregister();
        }

        obj1 = scoreboard.registerNewObjective("title", "dummy");
        obj1.setDisplayName("§e§lDuels");
        obj1.setDisplaySlot(DisplaySlot.SIDEBAR);

        for(Player p : getServer().getOnlinePlayers()) {
            Score score = obj1.getScore("§7ID: " + id.substring(0, 8) + "...");
            score.setScore(8 + players.size());

            Integer minutes = (timeElapsed % 3600) / 60;
            Integer seconds = timeElapsed % 60;
            String timeString = String.format("%02d:%02d", minutes, seconds);

            score = obj1.getScore("Time: §a" + timeString);
            score.setScore(7 + players.size());

            score = obj1.getScore("§b");
            score.setScore(6 + players.size());

            Integer i = 1;
            for(Player p2 : players) {
                DecimalFormat df = new DecimalFormat("0.0");
                score = obj1.getScore("§a" + p2.getName() + " " + df.format(p2.getHealth()) + "§c❤");
                score.setScore(5 + i);
                i++;
            }

            score = obj1.getScore("§e");
            score.setScore(5);

            score = obj1.getScore("Kit: §aNone");
            score.setScore(4);

            score = obj1.getScore("Type: §a" + type);
            score.setScore(3);

            score = obj1.getScore("§c");
            score.setScore(2);

            score = obj1.getScore("§eIP:");
            score.setScore(1);

            score = obj1.getScore("§enekonetwork.net");
            score.setScore(0);

            p.setScoreboard(scoreboard);
        }
    }

    public void removeScoreboard() {
        for(Player p : players) {
            p.setScoreboard(getScoreboardManager().getMainScoreboard());
        }
    }
}
