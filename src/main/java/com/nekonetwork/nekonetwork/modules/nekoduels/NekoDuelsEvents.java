package com.nekonetwork.nekonetwork.modules.nekoduels;

import com.nekonetwork.nekonetwork.helpers.EventsListener;
import com.nekonetwork.nekonetwork.modules.nekoduels.helpers.Duel;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.junit.internal.runners.statements.RunAfters;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.*;

import static org.bukkit.Bukkit.*;

public class NekoDuelsEvents extends EventsListener {

    public NekoDuels nekoDuels;
    public NekoDuelsEvents(NekoDuels module) {
        super(module);
        nekoDuels = module;
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (nekoDuels.helper.playersNonReady.contains(event.getPlayer().getUniqueId().toString()) && (from.getX() != to.getX() || from.getZ() != to.getZ())) {
            event.setTo(from);
        }
    }

    @EventHandler
    public void onPlayerBreak(BlockBreakEvent event) {
        if(!event.getPlayer().isOp()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        onEntityHealthChanged(event.getEntity(), event);
    }

    @EventHandler
    public void onEntityRegen(EntityRegainHealthEvent event) {
        onEntityHealthChanged(event.getEntity(), null);
    }

    public void onEntityHealthChanged(Entity e, EntityDamageEvent damageEvent) {
        if(nekoDuels.helper.playersInGame.contains(e.getUniqueId().toString())) {
            Player p = (Player) e;
            DecimalFormat df = new DecimalFormat("0.0");

            //Get player's duel
            List<Duel> a = new ArrayList<>(nekoDuels.helper.duelsInProgress.values());
            a.removeIf(d -> !(d.players.contains(p)));
            Duel duel = a.get(0);
            if(duel == null) { return; }

            //Broadcast a message to everyone except the damaged player
            for(Player p2 : duel.players) {
                if(!p2.getUniqueId().toString().equals(p.getUniqueId().toString())) {
                    p.sendMessage("§c" + e.getName() + " now has " + df.format(p.getHealth()) + "❤");
                }
            }

            //Update leaderboards
            Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(module.plugin, new Runnable() {
                @Override
                public void run() {
                    duel.updateScoreboards();
                }
            }, 0L);

            //Check if player died
            if(damageEvent != null && (p.getHealth() - damageEvent.getFinalDamage()) <= 0) {
                //Cancel the death event and set player to spectator
                damageEvent.setCancelled(true);
                p.setGameMode(GameMode.SPECTATOR);
                duel.deadPlayers.add(p);

                //Get remaining players
                List<Player> b = new ArrayList<>(duel.players);
                b.removeIf(p3 -> duel.deadPlayers.contains(p3));
                Player winner = b.size() == 1 ? b.get(0) : (b.size() == 0 ? p : null);

                //Send a message to everyone that the player died
                for(Player p2 : duel.players) {
                    p2.sendMessage("§c" + e.getName() + " died-");

                    //Send a message to everyone announcing the winner, if there's one
                    if(winner != null) {
                        p2.sendMessage("§e" + e.getName() + " won the duel!");
                        p2.sendTitle("§e" + e.getName() + " won the duel!", "-", 0, 80, 0);
                    }
                }

                //End the duel if there's a winner
                if(winner != null) {
                    duel.ended = true;
                    duel.removeScoreboard();
                    nekoDuels.helper.duelsInProgress.remove(duel.id);
                    nekoDuels.helper.updateHolograms();

                    final int taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(module.plugin, new Runnable() {
                        @Override
                        public void run() {
                            p.getWorld().spawnEntity(p.getLocation(), EntityType.FIREWORK);
                        }
                    }, 0L, 20L);

                    Bukkit.getScheduler().scheduleSyncDelayedTask(module.plugin, new Runnable() {
                        @Override
                        public void run() {
                            List<World> a = new ArrayList<>(getServer().getWorlds());
                            a.removeIf(w -> !(w.getName().equals(nekoDuels.helper.duelLobbyWorld)));
                            if(a.isEmpty()) {
                                getServer().broadcastMessage("§cCouldn't teleport players back, because LobbyWorld(name: " + nekoDuels.helper.duelLobbyWorld + ") doesn't exist-");
                                return;
                            }

                            Bukkit.getScheduler().cancelTask(taskID);
                            for(Player p3 : duel.players) {
                                p3.setGameMode(GameMode.SURVIVAL);
                                p3.getInventory().setContents(duel.previousItems.get(p3.getUniqueId().toString()));
                                p3.teleport(getServer().getWorld(nekoDuels.helper.duelLobbyWorld).getSpawnLocation());
                            }
                        }
                    }, 80L);
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(PlayerInteractEntityEvent event) {
        Player p = event.getPlayer();
        Entity e = event.getRightClicked();
        String playerID = p.getUniqueId().toString();
        String entityID = e.getUniqueId().toString();

        if(nekoDuels.helper.waitingDuelQueueEntities.containsKey(playerID)) {
            String type = nekoDuels.helper.waitingDuelQueueEntities.get(playerID);
            p.sendMessage("§c" + e.getName() + " is now listening");
            nekoDuels.helper.duelQueueEntities.put(entityID, type);
            e.setInvulnerable(true);
            ((LivingEntity) e).setAI(false);

            nekoDuels.helper.waitingDuelQueueEntities.remove(playerID);
        } else if(nekoDuels.helper.duelQueueEntities.containsKey(entityID)) {
            List<ArrayList<Player>> a = new ArrayList<>(nekoDuels.helper.duelQueue.values());
            a.removeIf(list -> !(list.contains(p)));

            if(a.isEmpty()) {
                nekoDuels.helper.queueForDuel(e, p, nekoDuels.helper.duelQueueEntities.get(entityID));
            } else {
                event.getPlayer().sendMessage("§cYou're already queued up for a duel-");
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        nekoDuels.helper.updateScoreboards();
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player p = event.getPlayer();
        for(ArrayList<Player> queue : nekoDuels.helper.duelQueue.values()) {
            queue.removeIf(p2 -> p2.getUniqueId().toString().equals(p.getUniqueId().toString()));
        }

        nekoDuels.helper.updateScoreboards();
    }
}
