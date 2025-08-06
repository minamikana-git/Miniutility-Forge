package org.hotamachisubaru.miniutility.Nickname;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.hotamachisubaru.miniutility.MiniutilityLoader;

import java.io.File;
import java.nio.file.Path;
import java.sql.*;
import java.util.Set;
import java.util.logging.Logger;

/**
 * ニックネームYAML→SQLite移行用
 * データベースパスやログ取得はMiniutilityLoaderから
 */
public class NicknameMigration {

    private final MiniutilityLoader plugin;

    public NicknameMigration(MiniutilityLoader plugin) {
        this.plugin = plugin;
    }

    /**
     * YAMLファイルからSQLiteデータベースへニックネームを移行
     */
    public void migrateToDatabase() {
        File yamlFile = new File(plugin.getDataFolder(), "nickname.yml");
        String dbPath = new File(plugin.getDataFolder(), "nicknames.db").getPath();
        Logger logger = plugin.getLogger();

        if (!yamlFile.exists()) {
            logger.warning("ニックネームの保存ファイルがありません。統合をスキップします。");
            return;
        }

        FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);
        Set<String> keys = yamlConfig.getKeys(false);
        if (keys == null || keys.isEmpty()) {
            logger.info("ニックネームが存在しません。もしくは壊れています。統合をスキップします。");
            return;
        }

        String dbUrl = "jdbc:sqlite:" + Path.of(dbPath).toAbsolutePath();
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // テーブルなければ作成
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS nicknames (uuid TEXT PRIMARY KEY, nickname TEXT)");
            }

            String insertQuery = "REPLACE INTO nicknames (uuid, nickname) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                for (String uuid : keys) {
                    String nickname = yamlConfig.getString(uuid);
                    if (nickname != null) {
                        pstmt.setString(1, uuid);
                        pstmt.setString(2, nickname);
                        pstmt.executeUpdate();
                        logger.info("データベースへのニックネームの統合に成功しました: " + uuid);
                    }
                }
            }
        } catch (SQLException e) {
            logger.warning("データベースへのニックネームの統合に失敗しました: " + e.getMessage());
        }
    }

    /**
     * Forge用にSQLiteファイルを作成し、データを取得
     */
    public void migrateToDatabaseForge() {
        File yamlFile = new File(plugin.getDataFolder(), "nickname.yml");
        String dbPath = new File(plugin.getDataFolder(), "nicknames.db").getPath();
        Logger logger = plugin.getLogger();

        if (!yamlFile.exists()) {
            logger.warning("ニックネームの保存ファイルがありません。統合をスキップします。");
            return;
        }

        Set<String> keys = null;
        try {
            FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);
            keys = yamlConfig.getKeys(false);
        } catch (Exception e) {
            logger.warning("YAMLファイルの読み込みに失敗しました: " + e.getMessage());
            return;
        }

        if (keys == null || keys.isEmpty()) {
            logger.info("ニックネームが存在しません。もしくは壊れています。統合をスキップします。");
            return;
        }

        String dbUrl = "jdbc:sqlite:" + Path.of(dbPath).toAbsolutePath();
        try (Connection connection = DriverManager.getConnection(dbUrl)) {
            // Forge環境でも使用可能なSQLテーブルを準備
            try (Statement stmt = connection.createStatement()) {
                stmt.executeUpdate("CREATE TABLE IF NOT EXISTS nicknames (uuid TEXT PRIMARY KEY, nickname TEXT)");
            }

            String insertQuery = "REPLACE INTO nicknames (uuid, nickname) VALUES (?, ?)";
            try (PreparedStatement pstmt = connection.prepareStatement(insertQuery)) {
                for (String uuid : keys) {
                    String nickname = null;
                    try {
                        FileConfiguration yamlConfig = YamlConfiguration.loadConfiguration(yamlFile);
                        nickname = yamlConfig.getString(uuid);
                    } catch (Exception e) {
                        logger.warning("UUID " + uuid + " のニックネーム取得に失敗しました: " + e.getMessage());
                    }
                    if (nickname != null) {
                        pstmt.setString(1, uuid);
                        pstmt.setString(2, nickname);
                        pstmt.executeUpdate();
                        logger.info("Forge用データベースへのニックネームの統合に成功しました: " + uuid);
                    }
                }
            }
        } catch (SQLException e) {
            logger.warning("Forge用データベースへのニックネームの統合に失敗しました: " + e.getMessage());
        }
    }
}
