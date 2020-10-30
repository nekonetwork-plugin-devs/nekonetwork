package com.nekonetwork.nekonetwork.modules.nekoduels;

import com.google.gson.internal.LinkedTreeMap;
import com.nekonetwork.nekonetwork.NekoNetwork;
import com.nekonetwork.nekonetwork.helpers.NekoModule;
import com.nekonetwork.nekonetwork.modules.nekoduels.helpers.DuelType;
import com.nekonetwork.nekonetwork.webmessages.GenericDataJSON;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import java.util.*;
import static org.bukkit.Bukkit.getServer;

public class NekoDuels extends NekoModule {

    public NekoDuelsHelper helper;
    public NekoDuels(NekoNetwork plugin, String name) {
        super(plugin, name);
        helper = new NekoDuelsHelper(this);
    }

    @Override
    public void enable() {
        super.enable();

        events = new NekoDuelsEvents(this);
        getServer().getPluginManager().registerEvents(events, this.plugin);
        helper.updateScoreboards();
    }

    @Override
    public void loadConfig(FileConfiguration config) {
        super.loadConfig(config);

        Map<String, Object> a = config.getConfigurationSection("nekoDuels.duelTypes").getValues(false);
        for(String o : a.keySet()) {
            DuelType duel = new DuelType();
            duel.name = o;
            List<String> a2 = (List<String>) config.getList("nekoDuels.duelTypes." + o + ".spawns");
            duel.spawns = new ArrayList<>(a2);
            List<String> a3 = (List<String>) config.getList("nekoDuels.duelTypes." + o + ".items");
            duel.items = new ArrayList<>(a3);
            List<String> a4 = (List<String>) config.getList("nekoDuels.duelTypes." + o + ".arenaWorlds");
            duel.arenaWorlds = new ArrayList<>(a4);
            duel.requiredPlayers = (Integer) config.get("nekoDuels.duelTypes." + o + ".requiredPlayers");

            helper.duelTypes.put(o, duel);
            helper.duelQueue.put(o, new ArrayList<>());
        }

        Map<String, Object> b = config.getConfigurationSection("nekoDuels.queueEntities").getValues(false);
        for(Map.Entry<String, Object> o : b.entrySet()) {
            helper.duelQueueEntities.put(o.getKey(), ((String) o.getValue()));

            Entity e = getServer().getEntity(UUID.fromString(o.getKey()));
            if(e != null) {
                e.setInvulnerable(true);
                ((LivingEntity) e).setAI(false);
            }
        }

        helper.duelLobbyWorld = (String) config.get("nekoDuels.duelLobbyWorld");
        helper.updateHolograms();
    }

    @Override
    public void editConfig(FileConfiguration config) {
        super.editConfig(config);

        config.set("nekoDuels.duelTypes", new HashMap<>());
        for(DuelType o : helper.duelTypes.values()) {
            config.set("nekoDuels.duelTypes." + o.name + ".name", o.name);
            config.set("nekoDuels.duelTypes." + o.name + ".spawns", o.spawns);
            config.set("nekoDuels.duelTypes." + o.name + ".items", o.items);
            config.set("nekoDuels.duelTypes." + o.name + ".arenaWorlds", o.arenaWorlds);
            config.set("nekoDuels.duelTypes." + o.name + ".requiredPlayers", o.requiredPlayers);
        }

        config.set("nekoDuels.queueEntities", helper.duelQueueEntities);
        config.set("nekoDuels.duelLobbyWorld", helper.duelLobbyWorld);
    }

    @Override
    public void handleGET(GenericDataJSON mi) {
        super.handleGET(mi);

        HashMap<String, GenericDataJSON> a = new HashMap<>();
        for(DuelType dt : helper.duelTypes.values()) {
            GenericDataJSON mi2 = new GenericDataJSON();
            mi2.data.put("name", dt.name);
            mi2.data.put("spawns", dt.spawns.toArray());
            mi2.data.put("items", dt.items.toArray());
            mi2.data.put("arenaWorlds", dt.arenaWorlds.toArray());
            mi2.data.put("requiredPlayers", dt.requiredPlayers);
            a.put((String) mi2.data.get("name"), mi2);
        }

        Map<String, Object[]> b = new HashMap<>();
        for(String dt : helper.duelQueue.keySet()) {
            List<String> arr = new ArrayList<>();
            for(Player p : helper.duelQueue.get(dt)) {
                arr.add(p.getName());
            }

            b.put(dt, arr.toArray());
        }

        mi.data.put("duelTypes", a);
        mi.data.put("duelQueue", b);
    }

    @Override
    public void handlePOST(GenericDataJSON body, LinkedTreeMap obj) {
        super.handlePOST(body, obj);

        switch ((String) body.data.get("type")) {
            case "submit-editDuelType":
                DuelType dt = this.helper.duelTypes.get(obj.get("name"));
                dt.requiredPlayers = Double.class.cast(obj.get("requiredPlayers")).intValue();
                break;

            case "delete-editDuelType":
                this.helper.duelTypes.remove(obj.get("name"));
                break;
        }
    }
}
