package com.witherflare.partyfinderplus;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.witherflare.partyfinderplus.commands.PartyFinderPlusCommand;
import com.witherflare.partyfinderplus.config.Config;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Arrays;

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

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) throws IOException {

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
                    ArrayList<Integer> saves = new ArrayList<Integer>();

                    profiles.forEach(profileData -> {
                        profileIds.add(profileData.getAsJsonObject().get("profile_id").getAsString());
                        saves.add(profileData.getAsJsonObject().get("members").getAsJsonObject().get(uuid).getAsJsonObject().get("last_save").getAsInt());
                    });
                    int maxSave = -2147483647;

                    for (int i = 0; i < saves.size(); i++) {
                        if (saves.get(i) > maxSave) {
                            maxSave = saves.get(i);
                        }
                    }

                    String currentProfileId = profileIds.get(saves.indexOf(maxSave));

                    HttpResponse res2 = null;
                    try {
                        res2 = client.execute(new HttpGet("https://api.hypixel.net/skyblock/profiles?key=" + config.apiKey + "&uuid=" + uuid));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    String body2 = null;
                    try {
                        body2 = EntityUtils.toString(res1.getEntity());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    JsonObject skyblockData = parser.parse(body2).getAsJsonObject();

                    


                } catch (Error e) {
                    chat(PREFIX + " §cAn error has occured.");
                }
                // Dungeon Finder > [NAME] joined the dungeon group! ([CLASS] Level [CLASS LEVEL])
            }).start();
        }
    }
}
