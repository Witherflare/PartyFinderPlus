package com.witherflare.partyfinderplus;

import com.witherflare.partyfinderplus.commands.PartyFinderPlusCommand;
import com.witherflare.partyfinderplus.config.Config;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(
    modid = PartyFinderPlus.MOD_ID,
    name = PartyFinderPlus.MOD_NAME,
    version = PartyFinderPlus.VERSION
)
public class PartyFinderPlus {

    public static final String MOD_ID = "partyfinderplus";
    public static final String MOD_NAME = "PartyFinderPlus";
    public static final String VERSION = "1.0";
    public static final String configLocation = "./config/partyfinderplus.toml";

    public static final Logger logger = LogManager.getLogger();
    public static Config config;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        config = new Config();
        config.preload();

        new PartyFinderPlusCommand().register();
    }
}
