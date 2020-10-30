package com.nekonetwork.nekonetwork.modules.nekoparty.commands;

import com.nekonetwork.nekonetwork.modules.nekoparty.NekoParty;
import com.nekonetwork.nekonetwork.modules.nekoparty.command.NekoPartyCommand;
import com.nekonetwork.nekonetwork.modules.nekoparty.helpers.Party;
import org.bukkit.entity.Player;

public class PartyDenyCommand extends NekoPartyCommand {

    public PartyDenyCommand(NekoParty module, String name) {
        super(module, name);
    }

    @Override
    public void process(Player p, String label, String[] args) {
        if(!nekoParty.helper.partyInvites.containsKey(p.getUniqueId().toString())) {
            p.sendMessage("§cYou don't have any pending invites-");
            return;
        }

        Party party = nekoParty.helper.parties.get(nekoParty.helper.partyInvites.get(p.getUniqueId().toString()));

        party.players.add(p);
        nekoParty.helper.partiesByUserID.put(p.getUniqueId().toString(), party.id);
        nekoParty.helper.partyInvites.remove(p.getUniqueId().toString());

        p.sendMessage("§cDeclined the invite-");
    }
}
