package com.witherflare.partyfinderplus;

import com.google.gson.*;
import com.typesafe.config.ConfigException;
import com.witherflare.partyfinderplus.commands.PartyFinderPlusCommand;
import com.witherflare.partyfinderplus.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import scala.math.BigInt;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.stream.Collectors;

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

        MinecraftForge.EVENT_BUS.register(this);

        new PartyFinderPlusCommand().register();
    }

    void chat (String message) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.addChatMessage(new ChatComponentText(message));
    }
    void say (String message) {
        Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) throws IOException {
        int KICKDELAY = 500;
        String PREFIX = "§dPartyFinderPlus §r>§e";
        String msg = event.message.getUnformattedText();
        String formattedMsg = event.message.getFormattedText();

        if (msg.contains("joined the dungeon group! ")) {
            if (!config.toggled) return;
            if (!config.autoKickToggled) return;
            if (config.apiKey.length() < 1) {
                chat(PREFIX + " §cYour API key is not set! Make sure you set your API key in /pfp.");
            }

            new Thread(() -> {
                // Get the user and their class
                String user = msg.split("Dungeon Finder > ")[1];
                String userClass = user.split(" joined the dungeon group! ")[1];
                userClass = userClass.split(" Level ")[0];
                userClass = userClass.replaceAll("[()]", "");
                user = user.split(" joined the dungeon group! ")[0];

                chat(user);

                HttpClient client = HttpClients.createDefault();
                JsonParser parser = new JsonParser();

                HttpResponse res = null;
                try {
                    res = client.execute(new HttpGet("https://api.mojang.com/users/profiles/minecraft/" + user));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String body = null;
                try {
                    body = EntityUtils.toString(res.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonObject data = parser.parse(body).getAsJsonObject();

                String uuid = data.get("id").getAsString();

                HttpResponse res1 = null;
                try {
                    res1 = client.execute(new HttpGet("https://api.hypixel.net/skyblock/profiles?key=" + config.apiKey + "&uuid=" + uuid));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                String body1 = null;
                try {
                    body1 = EntityUtils.toString(res1.getEntity());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                JsonObject hypixelProfileData = parser.parse(body1).getAsJsonObject();

                JsonArray profiles = null;
                try {
                    profiles = hypixelProfileData.get("profiles").getAsJsonArray();
                } catch (NullPointerException e) {
                    chat(PREFIX + " §cAn error occured while grabbing the profile data of §e" + user + "§c. Make sure your API key is valid and try again.");
                }
                try {
                    ArrayList<String> profileIds = new ArrayList<String>();
                    ArrayList<Long> saves = new ArrayList<Long>();

                    profiles.forEach(profileData -> {
                        profileIds.add(profileData.getAsJsonObject().get("profile_id").getAsString());
                        saves.add(profileData.getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("last_save").getAsLong());
                    });
                    long maxSave = -999999999999999999l;

                    for (int i = 0; i < saves.size(); i++) {
                        if (saves.get(i) > maxSave) {
                            maxSave = saves.get(i);
                        }
                    }


                    String currentProfileId = profileIds.get(saves.indexOf(maxSave));

                    HttpResponse res2 = null;

                    res2 = client.execute(new HttpGet("https://api.hypixel.net/player?key=" + config.apiKey + "&uuid=" + uuid));

                    String body2 = null;

                    body2 = EntityUtils.toString(res2.getEntity());


                    JsonObject generalData = parser.parse(body2).getAsJsonObject();
                    int secretCount = generalData.get("player").getAsJsonObject().get("achievements").getAsJsonObject().get("skyblock_treasure_hunter").getAsInt();
                    chat(Integer.toString(secretCount));

                    int requiredSecrets = 0;
                    if (config.secretMin == 1) {requiredSecrets=1000;}
                    if (config.secretMin == 2) {requiredSecrets=2500;}
                    if (config.secretMin == 3) {requiredSecrets=5000;}
                    if (config.secretMin == 4) {requiredSecrets=7500;}
                    if (config.secretMin == 5) {requiredSecrets=10000;}
                    if (config.secretMin == 6) {requiredSecrets=12500;}
                    if (config.secretMin == 7) {requiredSecrets=15000;}
                    if (config.secretMin == 8) {requiredSecrets=20000;}

                    chat(userClass);

                    if (userClass.contains("Healer") && !config.healerAllowed) {
                        chat(PREFIX + " §c<!> §eThis user is playing a class you have not allowed to join! Kicking user.");
                        if (config.autoKickReason) say("/pc " + user + " kicked for: Disallowed Class");
                        Thread.sleep(KICKDELAY);
                        say("/p kick " + user);
                    } else if (userClass.contains("Mage") && !config.mageAllowed) {
                        chat(PREFIX + " §c<!> §eThis user is playing a class you have not allowed to join! Kicking user.");
                        if (config.autoKickReason) say("/pc " + user + " kicked for: Disallowed Class");
                        Thread.sleep(KICKDELAY);
                        say("/p kick " + user);
                    } else if (userClass.contains("Berserk") && !config.berserkAllowed) {
                        chat(PREFIX + " §c<!> §eThis user is playing a class you have not allowed to join! Kicking user.");
                        if (config.autoKickReason) say("/pc " + user + " kicked for: Disallowed Class");
                        Thread.sleep(KICKDELAY);
                        say("/p kick " + user);
                    } else if (userClass.contains("Archer") && !config.archerAllowed) {
                        chat(PREFIX + " §c<!> §eThis user is playing a class you have not allowed to join! Kicking user.");
                        if (config.autoKickReason) say("/pc " + user + " kicked for: Disallowed Class");
                        Thread.sleep(KICKDELAY);
                        say("/p kick " + user);
                    } else if (userClass.contains("Tank") && !config.tankAllowed) {
                        chat(PREFIX + " §c<!> §eThis user is playing a class you have not allowed to join! Kicking user.");
                        if (config.autoKickReason) say("/pc " + user + " kicked for: Disallowed Class");
                        Thread.sleep(KICKDELAY);
                        say("/p kick " + user);
                    } else if (requiredSecrets >= secretCount) {
                        chat(PREFIX + " §c<!> §eThis user does not have the required amount of secrets to join! Kicking user.");
                        if (config.autoKickReason) say("/pc " + user + " kicked for: Low Secrets");
                        Thread.sleep(KICKDELAY);
                        say("/p kick " + user);
                    } else {
                        // The user made it past the first checks, hooray!
                        HttpResponse res3 = null;

                        res3 = client.execute(new HttpGet("https://api.hypixel.net/skyblock/profile?key=" + config.apiKey + "&uuid=" + uuid + "&profile=" + currentProfileId));

                        String body3 = null;

                        body3 = EntityUtils.toString(res3.getEntity());

                        JsonObject inventoryData = parser.parse(body3).getAsJsonObject();

                        Set<String> keys = new HashSet<>();
                        for (Map.Entry<String, JsonElement> entry : inventoryData.get("profile").getAsJsonObject().get("members").getAsJsonObject().entrySet()) {
                            keys.add(entry.getKey());
                        }


                        String invContents = null;
                        try {
                            for (String x : keys) {
                                invContents = inventoryData.get("profile").getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("inv_contents").getAsJsonObject().get("data").getAsString();
                            }
                        } catch (NullPointerException e) {
                            chat(PREFIX + " &cThis user does not have their API on!");
                        }

                        byte[] bytearray = java.util.Base64.getDecoder().decode(invContents);
                        ByteArrayInputStream inputstream = new java.io.ByteArrayInputStream(bytearray);
                        NBTTagCompound nbt = net.minecraft.nbt.CompressedStreamTools.readCompressed(inputstream);
                        NBTTagList itemTagList = nbt.getTagList("i", 10);
                        String invItems = itemTagList.toString();

                        if (!invItems.contains(":\"WITHER_SHIELD_SCROLL\"") && config.needsNecronBlade) {
                            chat(PREFIX + " §c<!> §eThis user does not have a Necron Blade! Kicking user.");
                            if (config.autoKickReason) say("/pc " + user + " kicked for: No Necron Blade");
                            Thread.sleep(KICKDELAY);
                            say("/p kick " + user);
                        }
                        if (!invItems.contains("id:\"TERMINATOR\"") && config.needsTerm) {
                            chat(PREFIX + " §c<!> §eThis user does not have a Terminator! Kicking user.");
                            if (config.autoKickReason) say("/pc " + user + " kicked for: No Terminator");
                            Thread.sleep(KICKDELAY);
                            say("/p kick " + user);
                        }

                    }
                } catch (Error | InterruptedException | IOException e) {
                    chat(PREFIX + " §cAn error has occured.");
                }
                // Dungeon Finder > [NAME] joined the dungeon group! ([CLASS] Level [CLASS LEVEL])
            }).start();
        }
    }
}
