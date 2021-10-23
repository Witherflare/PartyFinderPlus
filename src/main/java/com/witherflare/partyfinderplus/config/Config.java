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
        category = "1. General"
    )
    public boolean toggled = true;

    @Property(
        type = PropertyType.TEXT,
        name = "API Key",
        description = "Enter a valid API key for PartyFinderPlus to work. If you do not input a valid API key, PartyFinderPlus will not work.",
        category = "1. General",
        protectedText = true
    )
    public String apiKey = "";

    @Property(
            type = PropertyType.SWITCH,
            name = "Enable Autokick",
            category = "2. Autokick"
    )
    public boolean autoKickToggled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Send Kick Reason in Party Chat",
            description = "When enabled, when Autokick kicks someone, it will send the kick reason in party chat.",
            category = "2. Autokick"
    )
    public boolean autoKickReason = false;

    @Property(
            type = PropertyType.SELECTOR,
            name = "Secret Minimum",
            description = "If someone's secret count is below this number, they will be kicked from the party automatically.",
            category = "2. Autokick",
            options = {"None", "1000", "2500", "5000", "7500", "10000", "12500", "15000", "20000"}
    )
    public int secretMin = 0;

    @Property(
            type = PropertyType.SWITCH,
            name = "Require Necron Blade",
            description = "When enabled, if someone does not have a necron blade (Hyperion/Valkyrie/Scylla/Astraea), they will be kicked from the party.",
            category = "2. Autokick"
    )
    public boolean needsNecronBlade = false;

    @Property(
            type = PropertyType.SWITCH,
            name = "Require Terminator",
            description = "When enabled, if someone does not have a Terminator they will be kicked from the party.",
            category = "2. Autokick"
    )
    public boolean needsTerm = false;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Healer Allowed",
            category = "2. Autokick"
    )
    public boolean healerAllowed = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Mage Allowed",
            category = "2. Autokick"
    )
    public boolean mageAllowed = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Berserk Allowed",
            category = "2. Autokick"
    )
    public boolean berserkAllowed = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Archer Allowed",
            category = "2. Autokick"
    )
    public boolean archerAllowed = true;

    @Property(
            type = PropertyType.CHECKBOX,
            name = "Tank Allowed",
            category = "2. Autokick"
    )
    public boolean tankAllowed = true;


    public Config() {
        super(new File(PartyFinderPlus.configLocation));

//        addDependency("swagText", "toggleSwag");

        initialize();
    }
}