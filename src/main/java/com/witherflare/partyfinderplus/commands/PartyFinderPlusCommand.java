package com.witherflare.partyfinderplus.commands;

import com.witherflare.partyfinderplus.PartyFinderPlus;
import gg.essential.api.EssentialAPI;
import gg.essential.api.commands.Command;
import gg.essential.api.commands.DefaultHandler;

public class PartyFinderPlusCommand extends Command {
    public PartyFinderPlusCommand() {
        super("pfp");
    }

    @DefaultHandler
    public void handle() {
        EssentialAPI.getGuiUtil().openScreen(PartyFinderPlus.config.gui());
    }
}
