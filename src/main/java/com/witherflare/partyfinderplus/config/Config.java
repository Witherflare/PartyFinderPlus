package com.witherflare.partyfinderplus.config;

import com.witherflare.partyfinderplus.PartyFinderPlus;
import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {

    @Property(
        type = PropertyType.SWITCH,
        name = "Toggle PartyFinderPlus",
        description = "Enables PartyFinderPlus.",
        category = "General"
    )
    public boolean toggled = true;

    @Property(
        type = PropertyType.TEXT,
        name = "API Key",
        description = "Enter a valid API key for PartyFinderPlus to work. If you do not input a valid API key, PartyFinderPlus will not work.",
        category = "General",
        protectedText = true
    )
    public String apiKey = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "Enable Autokick",
            category = "Autokick"
    )
    public boolean autoKickToggled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Send Kick Reason in Party Chat",
            description = "When enabled, when Autokick kicks someone, it will send the kick reason in party chat.",
            category = "Autokick"
    )
    public boolean autoKickReason = false;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Secret Minimum",
            description = "If someone's secret count is below this number, they will be kicked from the party automatically.",
            category = "Autokick",
            options = {"None", "1000", "2500", "5000", "7500", "10000", "12500", "15000", "20000"}
    )
    public int secretMin = 0;


    public Config() {
        super(new File(PartyFinderPlus.configLocation));

//        addDependency("swagText", "toggleSwag");

        initialize();
    }
}