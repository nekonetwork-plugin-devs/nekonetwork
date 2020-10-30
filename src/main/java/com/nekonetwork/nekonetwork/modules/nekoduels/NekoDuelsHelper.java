package com.nekonetwork.nekonetwork.modules.nekoduels;

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import com.nekonetwork.nekonetwork.helpers.Utils;
import com.nekonetwork.nekonetwork.modules.nekoduels.helpers.Duel;
import com.nekonetwork.nekonetwork.modules.nekoduels.helpers.DuelType;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.*;
import java.util.*;
import static org.bukkit.Bukkit.getServer;

public class NekoDuelsHelper {

    public Map<String, DuelType> duelTypes = new HashMap<>();
    public String duelLobbyWorld = "world";

    public Map<String, ArrayList<Player>> duelQueue = new HashMap<>();
    public Map<String, Duel> duelsInProgress = new HashMap<>();
    public List<String> playersNonReady = new ArrayList<>();
    public List<String> playersInGame = new ArrayList<>();

    public Map<String, String> waitingDuelQueueEntities = new HashMap<>();
    public Map<String, String> duelQueueEntities = new HashMap<>();

    public Scoreboard lobbyScoreboard;
    public Integer lastTeamID = 0;

    public NekoDuels module;
    public NekoDuelsHelper(NekoDuels module) {
        this.module = module;
    }

    public void queueForDuel(Entity e, Player player, String type) {
        ArrayList<Player> currentQueue = duelQueue.get(type);
        currentQueue.add(player);

        DuelType duel = duelTypes.get(type);
        if(currentQueue.size() >= duel.requiredPlayers) {
            ArrayList<Player> players = new ArrayList<>();
            for(int i = 0; i < duel.requiredPlayers; i++) {
                players.add(currentQueue.get(i));
            }

            for(int i = 0; i < duel.requiredPlayers; i++) {
                currentQueue.remove(0);
            }

            createDuel(players, type);
        } else {
            player.sendMessage("§aYou're now queued up for §c" + type.toLowerCase() + "§a duel-");
        }

        duelQueue.put(type, currentQueue);
        updateScoreboards();
    }

    public void createDuel(List<Player> players, String type) {
        Duel duel = new Duel(module);

        String id = UUID.randomUUID().toString();
        duel.id = id;
        duel.teamID = lastTeamID;
        duel.type = type;
        duel.spawns = duelTypes.get(type).spawns;
        duel.items = duelTypes.get(type).items;
        duel.arenaWorlds = duelTypes.get(type).arenaWorlds;
        duel.players = players;
        lastTeamID += 1;

        duelsInProgress.put(id, duel);
        updateHolograms();
        updateScoreboards();

        getServer().broadcastMessage("§aCreating Duel between §c" + Utils.getPlayerNames(players) + "§a-");

        duel.processPre();
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                duel.processPost();
            }
        }, 5000L);
    }

    public void updateScoreboards() {
        if(lobbyScoreboard == null) {
            lobbyScoreboard = getServer().getScoreboardManager().getNewScoreboard();
        }

        if(lobbyScoreboard.getTeams().isEmpty()) {
            lobbyScoreboard.registerNewTeam("global");
        }

        Integer inGame = 0;
        for(Duel duel : duelsInProgress.values()) {
            inGame += duel.players.size();
        }

        Integer inQueue = 0;
        for(ArrayList<Player> players : duelQueue.values()) {
            inQueue += players.size();
        }

        Team team = (Team) lobbyScoreboard.getTeams().toArray()[0];
        for(Player p : getServer().getOnlinePlayers()) {
            if(!team.hasEntry(p.getName())) {
                team.addEntry(p.getName());
            }
        }

        Objective obj1 = lobbyScoreboard.getObjective("title");
        if(obj1 != null) {
            obj1.unregister();
        }

        obj1 = lobbyScoreboard.registerNewObjective("title", "dummy");
        obj1.setDisplayName("§e§lDuels");
        obj1.setDisplaySlot(DisplaySlot.SIDEBAR);

        for(Player p : getServer().getOnlinePlayers()) {
            if(!playersInGame.contains(p.getUniqueId().toString())) {
                Score score = obj1.getScore("§a§b> In game  - §c" + inGame);
                score.setScore(6);

                score = obj1.getScore("§a> In queue - §c" + inQueue);
                score.setScore(5);

                score = obj1.getScore("§e");
                score.setScore(4);

                score = obj1.getScore("§a>");
                score.setScore(3);

                score = obj1.getScore("§c");
                score.setScore(2);

                score = obj1.getScore("§eIP:");
                score.setScore(1);

                score = obj1.getScore("§enekonetwork.net");
                score.setScore(0);

                p.setScoreboard(lobbyScoreboard);
            }
        }
    }

    public void updateHolograms() {
        Collection<Hologram> holograms = HologramsAPI.getHolograms(module.plugin);
        for(Hologram holo : holograms) {
            holo.delete();
        }

        for(Map.Entry<String, String> entry : duelQueueEntities.entrySet()) {
            Entity e = getServer().getEntity(UUID.fromString(entry.getKey()));
            DuelType duel = duelTypes.get(entry.getValue());
            if(e != null) {
                Hologram holo = HologramsAPI.createHologram(module.plugin, e.getLocation().add(0, e.getHeight() + 1.5, 0));
                holo.appendTextLine("§e§lDuels");
                holo.appendTextLine("§c[" + duel.name + "]");

                Integer inGame = 0;
                for(Duel duel2 : duelsInProgress.values()) {
                    if(duel.name.equals(duel2.type)) {
                        inGame += duel2.players.size();
                    }
                }

                holo.appendTextLine("§c" + inGame + " §ain game");
            }
        }
    }

}
