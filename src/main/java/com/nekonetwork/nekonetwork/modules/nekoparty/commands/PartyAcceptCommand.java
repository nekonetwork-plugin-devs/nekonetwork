package com.nekonetwork.nekonetwork.modules.nekoparty.commands;

import com.nekonetwork.nekonetwork.command.RequiredArgument;
import com.nekonetwork.nekonetwork.modules.nekoparty.NekoParty;
import com.nekonetwork.nekonetwork.modules.nekoparty.command.NekoPartyCommand;
import com.nekonetwork.nekonetwork.modules.nekoparty.helpers.Party;
import org.bukkit.entity.Player;

public class PartyAcceptCommand extends NekoPartyCommand {

    public PartyAcceptCommand(NekoParty module, String name) {
        super(module, name);
    }

    @Override
    public void process(Player p, String label, String[] args) {
        if(!nekoParty.helper.partyInvites.containsKey(p.getUniqueId().toString())) {
            p.sendMessage("§cYou don't have any pending invites-");
            return;
        }
        if(nekoParty.helper.partiesByUserID.containsKey(p.getUniqueId().toString())) {
            p.sendMessage("§cYou're already in a party-");
            return;
        }

        Party party = nekoParty.helper.parties.get(nekoParty.helper.partyInvites.get(p.getUniqueId().toString()));

        party.players.add(p);
        nekoParty.helper.partiesByUserID.put(p.getUniqueId().toString(), party.id);
        nekoParty.helper.partyInvites.remove(p.getUniqueId().toString());

        p.sendMessage("§aJoined party of " + party.partyLeader.getName());
        for(Player p2 : party.players) {
            if(!p.getUniqueId().toString().equals(p2.getUniqueId().toString())) {
                p2.sendMessage("§a" + p.getName() + " joined the party-");
            }
        }
    }
}
