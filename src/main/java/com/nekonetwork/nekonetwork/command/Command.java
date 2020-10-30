package com.nekonetwork.nekonetwork.command;

import com.nekonetwork.nekonetwork.helpers.NekoModule;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public abstract class Command {

    public NekoModule module;
    public String name;
    public boolean needsOp = false;
    public List<RequiredArgument> requiredArguments = new ArrayList<>();

    public final DecimalFormat df = new DecimalFormat("0.00");

    public Command(NekoModule module, String name) {
        this.module = module;
        this.name = name;
    }

    public void process(Player p, String label, String[] args) {

    }
}
