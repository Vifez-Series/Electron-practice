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
import lol.vifez.electron.chat.ChatListener;
import lol.vifez.electron.chat.MessageCommand;
import lol.vifez.electron.chat.ReplyCommand;
import lol.vifez.electron.commands.admin.*;
import lol.vifez.electron.commands.staff.MoreCommand;
import lol.vifez.electron.divisions.commands.DivisionsCommand;
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
import lol.vifez.electron.navigator.command.NavigatorCommand;
import lol.vifez.electron.placeholderapi.ElectronPlaceholders;
import lol.vifez.electron.profile.ProfileManager;
import lol.vifez.electron.profile.repository.ProfileRepository;
import lol.vifez.electron.queue.QueueManager;
import lol.vifez.electron.queue.listener.QueueListener;
import lol.vifez.electron.ranks.RankManager;
import lol.vifez.electron.ranks.commands.RankCommands;
import lol.vifez.electron.scoreboard.PracticeScoreboard;
import lol.vifez.electron.scoreboard.ScoreboardConfig;
import lol.vifez.electron.util.conversation.ConversationManager;
import lol.vifez.electron.util.conversation.ConversationListener;
import lol.vifez.electron.settings.command.SettingsCommand;
import lol.vifez.electron.tab.ElectronTab;
import lol.vifez.electron.util.nametag.NameTagAPI;
import lol.vifez.electron.util.nametag.adapter.RankNametagAdapter;
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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import xyz.refinedev.api.skin.SkinAPI;
import xyz.refinedev.api.tablist.TablistHandler;


/**
 * @author vifez
 * @project Electron
 * @website https://vifez.lol
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
    @Getter private RankManager rankManager;
    @Getter private ConversationManager conversationManager;
    
    @Setter
    @Getter private Location spawnLocation;

    @Override
    public void onLoad() {
        // Setup PacketEvents API
        PacketEvents.setAPI(SpigotPacketEventsBuilder.build(this));
        // Load PacketEvents
        PacketEvents.getAPI().load();
    }

    @Override
    public void onEnable() {
        instance = this;
        
        // Initialize and configure PacketEvents
        PacketEvents.getAPI().init();
        PacketEvents.getAPI().getSettings()
            .checkForUpdates(false)
            .bStats(true)
            .debug(false);
            
        // Initialize the rest of the plugin
        initializePlugin();
        
        // Initialize scoreboard
        if (scoreboardConfig.getBoolean("scoreboard.enabled")) {
            try {
                new Assemble(this, new PracticeScoreboard());
                sendMessage("&aScoreboard initialized successfully!");
            } catch (Exception e) {
                sendMessage("&cFailed to initialize scoreboard: " + e.getMessage());
                e.printStackTrace();
            }
        } else {
            sendMessage("&eScoreboard is disabled in config.");
        }
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
        // Make sure default config exists
        saveResource("scoreboard.yml", false);
        scoreboardConfig = new ScoreboardConfig();
        sendMessage("&aScoreboard config loaded successfully!");
    }

    private void initializeConfigFiles() {
        // Save default config files if they don't exist
        saveResource("arenas.yml", false);
        saveResource("kits.yml", false);
        saveResource("ranks.yml", false);
        saveResource("tab.yml", false);
        
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
        
        // Initialize profile system even if MongoDB is not available
        ProfileRepository profileRepo = new ProfileRepository(mongoAPI, gson);
        profileManager = new ProfileManager(profileRepo);
        if (!mongoAPI.isConnected()) {
            sendMessage("&e[WARNING] Running in memory-only mode. Player data will not persist!");
        }
        
        arenaManager = new ArenaManager();
        kitManager = new KitManager();
        queueManager = new QueueManager();
        leaderboards = new Leaderboard(profileManager);
        rankManager = new RankManager(this);
        conversationManager = new ConversationManager();
        
        // Initialize NameTagAPI with rank adapter
        new NameTagAPI(this, new RankNametagAdapter(this), getServer().getScoreboardManager().getMainScoreboard(), 20L);
        
        // Register listeners
        getServer().getPluginManager().registerEvents(new ConversationListener(this), this);
        getServer().getPluginManager().registerEvents(new ChatListener(this), this);
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
        FileConfiguration databaseConfig = new ConfigFile(this, "database.yml").getConfiguration();
        mongoAPI = new MongoAPI(new MongoCredentials(
                databaseConfig.getString("mongo.uri", ""),
                databaseConfig.getBoolean("mongo.use uri", false),
                databaseConfig.getString("mongo.host", "localhost"),
                databaseConfig.getInt("mongo.port", 27017),
                databaseConfig.getString("mongo.database", "Electron"),
                databaseConfig.getString("mongo.user", ""),
                databaseConfig.getString("mongo.password", "")
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
        manager.registerCommand(new SettingsCommand());
        manager.registerCommand(new NavigatorCommand());
        manager.registerCommand(new DivisionsCommand());
        manager.registerCommand(new RankCommands(this));
    }

    private void initializeListeners() {
        new SpawnListener();
        new MatchListener();
        new QueueListener();
        new AutoRespawn();
        new HotbarListener();
        new MenuAPI(this);
    }

    private void initializeDesign() {
        // Initialize tablist if enabled in config
        if (tabFile != null && tabFile.getBoolean("enabled")) {
            getServer().getScheduler().runTaskLater(this, () -> {
                try {
                    // Create and configure tablist handler
                    TablistHandler tablistHandler = new TablistHandler(this);
                    
                    // Setup skin cache
                    SkinAPI skinAPI = new SkinAPI(this, gson);
                    tablistHandler.setupSkinCache(skinAPI);
                    
                    // Configure settings
                    tablistHandler.setIgnore1_7(false);
                    
                    // Initialize tablist system
                    tablistHandler.init(PacketEvents.getAPI());
                    
                    // Register our adapter with 1-second update interval
                    boolean hasPAPI = getServer().getPluginManager().isPluginEnabled("PlaceholderAPI");
                    ElectronTab tabAdapter = new ElectronTab(this, hasPAPI);
                    tablistHandler.registerAdapter(tabAdapter, 20L);
                    
                    sendMessage("&aTablist initialized successfully!");
                } catch (Exception e) {
                    sendMessage("&cFailed to initialize tablist: " + e.getMessage());
                    e.printStackTrace();
                }
            }, 20L); // Delay tablist initialization by 1 second
        }
    }

    private void displayStartupInfo() {
        sendMessage("§r");
        sendMessage("§3§lElectron Practice §8[V" + getDescription().getVersion() + "]");
        sendMessage("§fAuthors: §3vifez §f& §eMTR");
        sendMessage("§fCollaborators: §3aysha.rip");
        sendMessage("§r");
        sendMessage("§fProtocol: §3" + getServer().getBukkitVersion());
        sendMessage("§fSpigot: §3" + getServer().getName());
        sendMessage("§r");
        sendMessage("§fKits: §3" + kitManager.getKits().size());
        sendMessage("§fArenas: §3" + arenaManager.getArenas().size());
        sendMessage("§r");


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

        PacketEvents.getAPI().terminate();
    }
}