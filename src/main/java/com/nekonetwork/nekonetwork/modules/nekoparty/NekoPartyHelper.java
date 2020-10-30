package com.nekonetwork.nekonetwork.modules.nekoparty;

import com.nekonetwork.nekonetwork.modules.nekoparty.helpers.Party;
import java.util.HashMap;
import java.util.Map;

public class NekoPartyHelper {

    public Map<String, Party> parties = new HashMap<>();
    public Map<String, String> partiesByUserID = new HashMap<>();
    public Map<String, String> partyInvites = new HashMap<>();

    public NekoParty module;
    public NekoPartyHelper(NekoParty module) {
        this.module = module;
    }
}
