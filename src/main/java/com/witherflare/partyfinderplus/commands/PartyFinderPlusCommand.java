package com.witherflare.partyfinderplus.commands;

import com.witherflare.partyfinderplus.PartyFinderPlus;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;
import gg.essential.api.commands.SubCommand;
import gg.essential.api.commands.ArgumentParser;

public class PartyFinderPlusCommand extends Command {
    public PartyFinderPlusCommand() {
        super("pfp");
    }

    @DefaultHandler
    public void guiHandler() {
        EssentialAPI.getGuiUtil().openScreen(PartyFinderPlus.config.gui());
    }

    @SubCommand("add")
    public void throwerAddHandler(String thrower) {

    }
}