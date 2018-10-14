package io.asbyth.command;

import cc.hyperium.commands.BaseCommand;
import cc.hyperium.commands.CommandException;
import io.asbyth.TTT;
import io.asbyth.gui.ConfigGui;

public class TTTCommand implements BaseCommand {

    private TTT mod;

    public TTTCommand(TTT mod) {
        this.mod = mod;
    }

    @Override
    public String getName() {
        return "20";
    }

    @Override
    public String getUsage() {
        return "20";

    }

    @Override
    public void onExecute(String[] args) throws CommandException {
        new ConfigGui(mod).show();
    }
}
