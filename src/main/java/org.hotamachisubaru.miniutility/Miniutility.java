package org.hotamachisubaru.miniutility;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.hotamachisubaru.miniutility.Listener.*;
import org.hotamachisubaru.miniutility.Menu.MiniutilityMenu;
import org.hotamachisubaru.miniutility.Nickname.NicknameDatabase;
import org.hotamachisubaru.miniutility.Nickname.NicknameManager;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Mod("miniutility")
public class Miniutility {

    private final Map<UUID, String> deathLocations = new ConcurrentHashMap<>();
    private final Logger logger = Logger.getLogger("Miniutility");
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, "miniutility");
    public static final RegistryObject<MenuType<MiniutilityMenu>> MINIUTILITY_MENU = MENUS.register("menu", () -> IForgeMenuType.create(MiniutilityMenu::new));

    private Config config;
    private NicknameDatabase nicknameDatabase;
    private NicknameManager nicknameManager;

    public Miniutility() {
        // Register MenuType
        MENUS.register(FMLJavaModLoadingContext.get().getModEventBus());

        // Register Listeners
        MinecraftForge.EVENT_BUS.register(new DeathListener(this));
        MinecraftForge.EVENT_BUS.register(new Chat(this, new NicknameDatabase(new File("config/miniutility")), new NicknameManager(this, new NicknameDatabase(new File("config/miniutility")))));
        MinecraftForge.EVENT_BUS.register(new CreeperProtectionListener(this));

        MinecraftForge.EVENT_BUS.register(new NicknameListener(this));
        MinecraftForge.EVENT_BUS.register(new TrashListener(this));

        // Initialize database and configuration
        setupDatabase();
        nicknameDatabase = new NicknameDatabase(new File("config/miniutility/nickname.db"));
        nicknameManager = new NicknameManager(this, nicknameDatabase);

        // Other initialization processes
        checkUpdates();
    }

    private void setup(final FMLCommonSetupEvent event) {
        config = new Config();
        config.loadDefaultConfig();
        setupDatabase();
        checkUpdates();
        logger.info("Miniutility initialization completed successfully.");
    }

    protected void checkUpdates() {
        String owner = "minamikana-git";
        String repo = "Miniutility-Forge";
        String apiUrl = String.format(
                "https://api.github.com/repos/%s/%s/releases/latest",
                owner, repo
        );
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiUrl))
                .header("Accept", "application/vnd.github.v3+json")
                .GET()
                .build();
        try {
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                logger.warning("Update check failed: HTTP " + response.statusCode());
                return;
            }
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String latestTag = json.get("tag_name").getAsString().replaceFirst("^v", "");
            String currentVersion = "1.0.0"; // Placeholder for mod version
            if (!currentVersion.equals(latestTag)) {
                String url = json.get("html_url").getAsString();
                logger.info("A new version is available: " + latestTag + ". Download: " + url);
            }
        } catch (IOException | InterruptedException e) {
            logger.warning("An error occurred during the update check: " + e.getMessage());
        }
    }

    private void setupDatabase() {
        File dbFile = new File("config/miniutility/nickname.db");
        if (dbFile.exists()) {
            logger.info("nickname.db already exists. Skipping setup.");
            return;
        }
        try {
            String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            try (java.sql.Connection connection = java.sql.DriverManager.getConnection(dbUrl)) {
                try (java.sql.Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS nicknames (uuid TEXT PRIMARY KEY, nickname TEXT)");
                }
            }
            logger.info("nickname.db initialized and necessary tables created.");
        } catch (Exception e) {
            logger.severe("Failed to initialize nickname.db: " + e.getMessage());
        }
    }

    public NicknameManager getNicknameManager() {
        return nicknameManager;
    }

    public NicknameDatabase getNicknameDatabase() {
        return nicknameDatabase;
    }

    public Miniutility getMiniutility() {
        return this;
    }
}
