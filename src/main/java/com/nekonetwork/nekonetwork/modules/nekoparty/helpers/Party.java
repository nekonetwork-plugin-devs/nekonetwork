package com.nekonetwork.nekonetwork.modules.nekoparty.helpers;

import com.nekonetwork.nekonetwork.helpers.NekoModule;
import com.nekonetwork.nekonetwork.modules.nekoparty.NekoParty;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.List;

public class Party {

    public NekoModule module;
    public String id;
    public Player partyLeader;
    public List<Player> players = new ArrayList<>();

    public Party(NekoParty module) {
        this.module = module;
    }
}
