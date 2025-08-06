package org.hotamachisubaru.miniutility;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.event.server.ServerStartingEvent;

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
    private MiniutilityConfig config;

    public Miniutility() {
        MinecraftForge.EVENT_BUS.addListener(this::onServerStarting);
        MinecraftForge.EVENT_BUS.addListener(this::setup);
    }

    private void setup(final FMLCommonSetupEvent event) {
        config = new MiniutilityConfig();
        config.loadDefaultConfig();
        setupDatabase();
        registerListeners();
        checkUpdates();
        logger.info("Miniutility の初期化が正常に完了しました。");
    }

    private void onServerStarting(final ServerStartingEvent event) {
        logger.info("Miniutility を使用してサーバーを起動しています。");
    }

    private void registerListeners() {
        MinecraftForge.EVENT_BUS.register(new DeathListener(this));
        MinecraftForge.EVENT_BUS.register(new ChatListener(this));
        MinecraftForge.EVENT_BUS.register(new CreeperProtectionListener(this));
        MinecraftForge.EVENT_BUS.register(new Menu());
        MinecraftForge.EVENT_BUS.register(new NicknameListener(this));
        MinecraftForge.EVENT_BUS.register(new TrashListener(this));
    }

    private void checkUpdates() {
        String owner = "minamikana-git";
        String repo = "Miniutility";
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
                logger.warning("アップデートチェックに失敗しました: HTTP " + response.statusCode());
                return;
            }
            JsonObject json = JsonParser.parseString(response.body()).getAsJsonObject();
            String latestTag = json.get("tag_name").getAsString().replaceFirst("^v", "");
            String currentVersion = "1.0.0"; // Placeholder for mod version
            if (!currentVersion.equals(latestTag)) {
                String url = json.get("html_url").getAsString();
                logger.info("新しいバージョンが利用可能です: " + latestTag + ". ダウンロード: " + url);
            }
        } catch (IOException | InterruptedException e) {
            logger.warning("アップデートチェック中にエラーが発生しました: " + e.getMessage());
        }
    }

    private void setupDatabase() {
        File dbFile = new File("config/miniutility/nickname.db");
        if (dbFile.exists()) {
            logger.info("nickname.db はすでに存在します。セットアップをスキップします。");
            return;
        }
        try {
            String dbUrl = "jdbc:sqlite:" + dbFile.getAbsolutePath();
            try (java.sql.Connection connection = java.sql.DriverManager.getConnection(dbUrl)) {
                try (java.sql.Statement stmt = connection.createStatement()) {
                    stmt.executeUpdate("CREATE TABLE IF NOT EXISTS nicknames (uuid TEXT PRIMARY KEY, nickname TEXT)");
                }
            }
            logger.info("nickname.db を初期化し、必要なテーブルを作成しました。");
        } catch (Exception e) {
            logger.severe("nickname.db の初期化に失敗しました: " + e.getMessage());
        }
    }
}
