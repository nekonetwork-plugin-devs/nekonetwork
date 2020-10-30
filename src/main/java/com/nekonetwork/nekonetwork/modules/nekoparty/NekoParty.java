package com.nekonetwork.nekonetwork.modules.nekoparty;

import com.nekonetwork.nekonetwork.NekoNetwork;
import com.nekonetwork.nekonetwork.helpers.NekoModule;
import com.nekonetwork.nekonetwork.webmessages.GenericDataJSON;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.bukkit.Bukkit.getServer;

public class NekoParty extends NekoModule {

    public NekoPartyHelper helper;
    public NekoParty(NekoNetwork plugin, String name) {
        super(plugin, name);
        helper = new NekoPartyHelper(this);
    }

    @Override
    public void enable() {
        super.enable();

        events = new NekoPartyEvents(this);
        getServer().getPluginManager().registerEvents(events, this.plugin);
    }

    @Override
    public void handleGET(GenericDataJSON mi) {
        super.handleGET(mi);

        HashMap<String, Object[]> a = new HashMap<>();
        for(String id : helper.parties.keySet()) {
            List<String> arr = new ArrayList<>();
            for(Player p : helper.parties.get(id).players) {
                arr.add(p.getName());
            }

            a.put(id, arr.toArray());
        }

        mi.data.put("parties", a);
    }
}
