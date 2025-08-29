package lol.vifez.electron;

import co.aikar.commands.BukkitCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lol.vifez.electron.arena.commands.ArenaCommand;
import lol.vifez.electron.arena.commands.ArenasCommand;
import lol.vifez.electron.arena.manager.ArenaManager;
import lol.vifez.electron.chat.MessageCommand;
import lol.vifez.electron.chat.ReplyCommand;
import lol.vifez.electron.commands.admin.*;
import lol.vifez.electron.commands.staff.MoreCommand;
import lol.vifez.electron.duel.command.DuelCommand;
import lol.vifez.electron.hotbar.Hotbar;
import lol.vifez.electron.hotbar.HotbarListener;
import lol.vifez.electron.kit.KitManager;
import lol.vifez.electron.kit.commands.KitCommands;
import lol.vifez.electron.kit.commands.KitEditorCommand;
import lol.vifez.electron.leaderboard.Leaderboard;
import lol.vifez.electron.leaderboard.command.LeaderboardCommand;
import lol.vifez.electron.listener.MatchListener;
import lol.vifez.electron.listener.SpawnListener;
import lol.vifez.electron.match.MatchManager;
import lol.vifez.electron.match.task.MatchTask;
import lol.vifez.electron.mongo.MongoAPI;
import lol.vifez.electron.mongo.MongoCredentials;
import lol.vifez.electron.placeholderapi.ElectronPlaceholders;
import lol.vifez.electron.profile.ProfileManager;
import lol.vifez.electron.profile.repository.ProfileRepository;
import lol.vifez.electron.queue.QueueManager;
import lol.vifez.electron.queue.listener.QueueListener;
import lol.vifez.electron.scoreboard.PracticeScoreboard;
import lol.vifez.electron.scoreboard.ScoreboardConfig;
import lol.vifez.electron.tab.ElectronTab;
import lol.vifez.electron.util.CC;
import lol.vifez.electron.util.ConfigFile;
import lol.vifez.electron.util.SerializationUtil;
import lol.vifez.electron.util.adapter.ItemStackArrayTypeAdapter;
import lol.vifez.electron.util.assemble.Assemble;
import lol.vifez.electron.util.menu.MenuAPI;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.api.skin.SkinAPI;
import xyz.refinedev.api.tablist.TablistHandler;

import java.io.File;

/*
 * Copyright (c) 2025 Vifez. All rights reserved.
 * Unauthorized use or distribution is prohibited.
 */

public final class Practice extends JavaPlugin {

    @Getter private static Practice instance;
    
    @Getter private ConfigFile arenasFile, kitsFile, tabFile;
    @Getter private FileConfiguration languageConfig;
    @Getter private ScoreboardConfig scoreboardConfig;
    
    @Getter private MongoAPI mongoAPI;
    @Getter private Gson gson;
    @Getter private ProfileManager profileManager;
    @Getter private ArenaManager arenaManager;
    @Getter private KitManager kitManager;
    @Getter private MatchManager matchManager;
    @Getter private QueueManager queueManager;
    @Getter private Leaderboard leaderboards;
    
    @Setter
    @Getter private Location spawnLocation;

    @Override
    public void onLoad() {
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        PacketEvents.getAPI().init();
    }

    @Override
    public void onEnable() {
        instance = this;

        initializePlugin();
        new Assemble(this, new PracticeScoreboard());
    }


    private void initializePlugin() {
        saveDefaultConfig();
        loadScoreboardConfig();
        initializeConfigFiles();
        
        initializeServices();
        initializeManagers();
        registerCommands();
        initializeListeners();
        initializeDesign();
        
        displayStartupInfo();
    }

    private void loadScoreboardConfig() {
        File file = new File(getDataFolder(), "scoreboard.yml");
        if (!file.exists()) {
            saveResource("scoreboard.yml", false);
        }
        scoreboardConfig = new ScoreboardConfig();
    }

    private void initializeConfigFiles() {
        arenasFile = new ConfigFile(this, "arenas.yml");
        kitsFile = new ConfigFile(this, "kits.yml");
        tabFile = new ConfigFile(this, "tab.yml");

        if (!tabFile.getConfiguration().contains("enabled")) {
            sendMessage("&c[ERROR] tab.yml is missing essential data!");
        } else {
            sendMessage("&aSuccessfully loaded tab.yml!");
        }
    }

    private void initializeManagers() {
        matchManager = new MatchManager();
        new MatchTask(matchManager).runTaskTimer(this, 0L, 20L);
        
        profileManager = new ProfileManager(new ProfileRepository(mongoAPI, gson));
        arenaManager = new ArenaManager();
        kitManager = new KitManager();
        queueManager = new QueueManager();
        leaderboards = new Leaderboard(profileManager);
    }

    private void initializeServices() {
        initializeGson();
        initializeMongo();
        initializeSpawnLocation();
        initializePlaceholderAPI();
    }

    private void initializeGson() {
        gson = new GsonBuilder()
                .serializeNulls()
                .setPrettyPrinting()
                .setLongSerializationPolicy(LongSerializationPolicy.STRING)
                .registerTypeAdapter(ItemStack[].class, new ItemStackArrayTypeAdapter())
                .create();
    }

    private void initializeMongo() {
        mongoAPI = new MongoAPI(new MongoCredentials(
                getConfig().getString("mongo.host"),
                getConfig().getInt("mongo.port"),
                getConfig().getString("mongo.database"),
                getConfig().getString("mongo.user"),
                getConfig().getString("mongo.password")
        ));
    }

    private void initializeSpawnLocation() {
        spawnLocation = SerializationUtil.deserializeLocation(
                getConfig().getString("settings.spawn-location", "world,0,100,0,0,0")
        );
    }

    private void initializePlaceholderAPI() {
        if (getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")) {
            new ElectronPlaceholders(this).register();
        }
    }

    private void registerCommands() {
        BukkitCommandManager manager = new BukkitCommandManager(this);
        
        manager.registerCommand(new ArenaCommand(arenaManager));
        manager.registerCommand(new ArenasCommand());
        manager.registerCommand(new KitCommands());
        manager.registerCommand(new KitEditorCommand());
        manager.registerCommand(new ElectronCommand());
        manager.registerCommand(new BuildModeCommand());
        manager.registerCommand(new EloCommand());
        manager.registerCommand(new SetSpawnCommand());
        manager.registerCommand(new LeaderboardCommand());
        manager.registerCommand(new MessageCommand());
        manager.registerCommand(new ReplyCommand());
        manager.registerCommand(new MoreCommand());
        manager.registerCommand(new DuelCommand());
    }

    private void initializeListeners() {
        new SpawnListener();
        new MatchListener();
        new QueueListener();
        new HotbarListener();
        new MenuAPI(this);
    }

    private void initializeDesign() {
        if (getConfig().getBoolean("scoreboard.enabled")) {
            new Assemble(this, new PracticeScoreboard());
        }

        if (tabFile.getBoolean("enabled")) {
            initializeTablist();
        }
    }

    private void initializeTablist() {
        TablistHandler tablistHandler = new TablistHandler(this);
        SkinAPI skinAPI = new SkinAPI(this, gson);

        tablistHandler.setIgnore1_7(false);
        tablistHandler.setupSkinCache(skinAPI);
        tablistHandler.init(PacketEvents.getAPI());
        tablistHandler.registerAdapter(
                new ElectronTab(this, getServer().getPluginManager().isPluginEnabled("PlaceholderAPI")), 
                20
        );
    }

    private void displayStartupInfo() {
        sendMessage(" ");
        sendMessage("&b&lElectron Practice&7 [Open SRC]");
        sendMessage("&7&oLightweight open-source practice core");
        sendMessage(" ");
        sendMessage("&fAuthors: &bvifez &f& &eMTR");
        sendMessage("&fVersion: &b" + getDescription().getVersion());
        sendMessage(" ");
        sendMessage("&fProtocol: &b" + getServer().getBukkitVersion());
        sendMessage("&fSpigot: &b" + getServer().getName());
        sendMessage(" ");
        sendMessage("&fDiscord: &bhttps://discord.vifez.lol");
        sendMessage("&fGithub: &bhttps://electron.vifez.lol");
        sendMessage(" ");

        Hotbar.loadAll();
    }

    private void sendMessage(String message) {
        System.out.println(CC.translate(message));
    }

    @Override
    public void onDisable() {
        if (profileManager != null) profileManager.close();
        if (arenaManager != null) arenaManager.close();
        if (kitManager != null) kitManager.close();

        PacketEvents.getAPI().terminate();
    }
}