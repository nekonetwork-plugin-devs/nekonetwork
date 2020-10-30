package com.nekonetwork.nekonetwork.modules.nekoparty.commands;

import com.nekonetwork.nekonetwork.modules.nekoparty.NekoParty;
import com.nekonetwork.nekonetwork.modules.nekoparty.command.NekoPartyCommand;
import com.nekonetwork.nekonetwork.modules.nekoparty.helpers.Party;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.UUID;

import static org.bukkit.Bukkit.getServer;

public class PartyCommand extends NekoPartyCommand {

    public PartyCommand(NekoParty module, String name) {
        super(module, name);
    }

    @Override
    public void process(Player p, String label, String[] args) {
        if(args.length < 1) {
            if(nekoParty.helper.partiesByUserID.containsKey(p.getUniqueId().toString())) {
                Party party = nekoParty.helper.parties.get(nekoParty.helper.partiesByUserID.get(p.getUniqueId().toString()));
                String message = "§aParty (" + party.players.size() + ")\nLeader: §c" + party.partyLeader.getName() + "\n§aPlayers: §c";

                for(Player p2 : party.players) {
                    message += p2.getName() + ", ";
                }

                message = message.endsWith(", ") ? message.substring(0, message.length() - 2) : message;
                p.sendMessage(message);
                return;
            }

            p.sendMessage("§cYou aren't in a party-");
            return;
        }

        String username = String.join(" ", args);
        Player p2 = getServer().getPlayer(username);
        if(p2 == null) {
            p.sendMessage("§cHaven't found anyone named " + username + "-");
            return;
        }

        if(!p2.isOnline()) {
            p.sendMessage("§c" + p2.getName() + " is offline-");
            return;
        }

        if(nekoParty.helper.partiesByUserID.containsKey(p.getUniqueId().toString())) {
            Party party = nekoParty.helper.parties.get(nekoParty.helper.partiesByUserID.get(p.getUniqueId().toString()));

            if(!party.partyLeader.getUniqueId().toString().equals(p.getUniqueId().toString())) {
                p.sendMessage("§cYou aren't the party leader");
                return;
            }

            ArrayList<Player> a = new ArrayList<>(party.players);
            a.removeIf(p3 -> !(p3.getName().equals(p2.getName())));
            if(!a.isEmpty()) {
                p.sendMessage("§c" + p2.getName() + " is already in the party-");
                return;
            }

            TextComponent message = new TextComponent("§a[Accept] ");
            message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "paccept" ));
            TextComponent message2 = new TextComponent("§c[Deny]");
            message2.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "pdeny" ));
            message.addExtra(message2);

            p2.sendMessage("§eYou've been invited to party of " + party.partyLeader.getName() + "-");
            p2.spigot().sendMessage(message);

            p.sendMessage("§Invited " + p2.getName() + " to the party-");
            return;
        }

        if(p2.getUniqueId().toString().equals(p.getUniqueId().toString())) {
            p.sendMessage("§cYou can't add yourself to a party silly-");
            return;
        }

        Party party = new Party(nekoParty);
        String id = UUID.randomUUID().toString();
        ArrayList<Player> players = new ArrayList<>();
        players.add(p);
        players.add(p2);

        party.id = id;
        party.partyLeader = p;
        party.players = players;

        nekoParty.helper.parties.put(id, party);
        nekoParty.helper.partiesByUserID.put(p.getUniqueId().toString(), party.id);
        nekoParty.helper.partiesByUserID.put(p2.getUniqueId().toString(), party.id);

        p.sendMessage("§aCreated party with " + p2.getName() + "-");
    }
}
