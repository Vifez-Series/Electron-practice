package lol.vifez.electron;

import co.aikar.commands.BukkitCommandManager;
import com.github.retrooper.packetevents.PacketEvents;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.LongSerializationPolicy;
import io.github.retrooper.packetevents.factory.spigot.SpigotPacketEventsBuilder;
import lol.vifez.electron.arena.commands.ArenaCommand;
import lol.vifez.electron.arena.commands.ArenasCommand;
import lol.vifez.electron.arena.ArenaManager;
import lol.vifez.electron.chat.MessageCommand;
import lol.vifez.electron.chat.ReplyCommand;
import lol.vifez.electron.commands.admin.*;
import lol.vifez.electron.commands.staff.MoreCommand;
import lol.vifez.electron.commands.user.RematchCommand;
import lol.vifez.electron.commands.user.SurrenderCommand;
import lol.vifez.electron.divisions.commands.DivisionsCommand;
import lol.vifez.electron.duel.command.DuelCommand;
import lol.vifez.electron.hotbar.Hotbar;
import lol.vifez.electron.hotbar.HotbarListener;
import lol.vifez.electron.kit.KitManager;
import lol.vifez.electron.kit.commands.KitCommands;
import lol.vifez.electron.leaderboard.Leaderboard;
import lol.vifez.electron.leaderboard.command.LeaderboardCommand;
import lol.vifez.electron.listener.DeathListener;
import lol.vifez.electron.match.MatchListener;
import lol.vifez.electron.listener.PearlListener;
import lol.vifez.electron.listener.SpawnListener;
import lol.vifez.electron.match.MatchManager;
import lol.vifez.electron.match.task.MatchTask;
import lol.vifez.electron.mongo.MongoAPI;
import lol.vifez.electron.navigator.command.NavigatorCommand;
import lol.vifez.electron.queue.command.ForceQueueCommand;
import lol.vifez.electron.util.placeholderapi.ElectronPlaceholders;
import lol.vifez.electron.profile.ProfileManager;
import lol.vifez.electron.profile.repository.ProfileRepository;
import lol.vifez.electron.queue.QueueManager;
import lol.vifez.electron.queue.listener.QueueListener;
import lol.vifez.electron.scoreboard.PracticeScoreboard;
import lol.vifez.electron.scoreboard.ScoreboardConfig;
import lol.vifez.electron.settings.command.SettingsCommand;
import lol.vifez.electron.tab.ElectronTab;
import lol.vifez.electron.util.AutoRespawn;
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
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.api.skin.SkinAPI;
import xyz.refinedev.api.tablist.TablistHandler;

import java.io.File;

/* 
 * Electron © Vifez
 * Developed by Vifez
 * Copyright (c) 2025 Vifez. All rights reserved.
*/

public final class Practice extends JavaPlugin {

    @Getter private static Practice instance;

    @Getter private ConfigFile arenasFile, kitsFile, tabFile;
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
    @Getter private BukkitCommandManager bukkitCommandManager;
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
        String uri = getConfig().getString("MONGO.URI");
        String dbName = getConfig().getString("MONGO.DATABASE");

        mongoAPI = new MongoAPI(uri, dbName);
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
        manager.registerCommand(new ElectronCommand());
        manager.registerCommand(new BuildModeCommand());
        manager.registerCommand(new EloCommand());
        manager.registerCommand(new SetSpawnCommand());
        manager.registerCommand(new LeaderboardCommand());
        manager.registerCommand(new MessageCommand());
        manager.registerCommand(new ReplyCommand());
        manager.registerCommand(new MoreCommand());
        manager.registerCommand(new DuelCommand());
        manager.registerCommand(new SettingsCommand());
        manager.registerCommand(new NavigatorCommand());
        manager.registerCommand(new DivisionsCommand());
        manager.registerCommand(new SurrenderCommand());
        manager.registerCommand(new RenameCommand());
        manager.registerCommand(new RematchCommand());
        manager.registerCommand(new ForceQueueCommand());
    }

    private void initializeListeners() {
        new SpawnListener();
        new MatchListener();
        new QueueListener();
        Bukkit.getPluginManager().registerEvents(new DeathListener(), this);
        new AutoRespawn();
        new HotbarListener();
        new PearlListener();
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
        sendMessage("&b&lElectron Practice &7[V" + getDescription().getVersion() + "]");
        sendMessage("&fAuthors: &bvifez &f& &eMTR");
        sendMessage(" ");
        sendMessage("&fProtocol: &b" + getServer().getBukkitVersion());
        sendMessage("&fSpigot: &b" + getServer().getName());
        sendMessage(" ");
        sendMessage("&fKits: &b" + kitManager.getKits().size());
        sendMessage("&fArenas: &b" + arenaManager.getArenas().size());
        sendMessage(" ");


        Hotbar.loadAll();
    }

    private void sendMessage(String message) {
        Bukkit.getConsoleSender().sendMessage(CC.translate(message));
    }

    @Override
    public void onDisable() {
        if (profileManager != null) profileManager.close();
        if (arenaManager != null) arenaManager.close();
        if (kitManager != null) kitManager.close();
        if (mongoAPI != null) mongoAPI.close();

        PacketEvents.getAPI().terminate();
    }
}