package org.hotamachisubaru.miniutility.Nickname;

import org.json.JSONObject;
import org.hotamachisubaru.miniutility.Miniutility;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.Set;
import java.util.logging.Logger;

/**
 * ニックネームJSON→SQLite移行用
 * データベースパスやログ取得はMiniutilityLoaderから
 */
public class NicknameMigration {

    private final Miniutility mod;

    public NicknameMigration(Miniutility mod) {
        this.mod = mod;
    }

    /**
     * JSONファイルからSQLiteデータベースへニックネームを移行
     */
    public void migrateToDatabase() {
        File jsonFile = new File(mod.getDataFolder(), "nickname.json");
        String dbPath = new File(mod.getDataFolder(), "nicknames.db").getPath();
        Logger logger = mod.getLogger();

        if (!jsonFile.exists()) {
            logger.warning("ニックネームの保存ファイルがありません。統合をスキップします。");
            return;
        }

        JSONObject jsonConfig;
        try {
            String content = Files.readString(jsonFile.toPath());
            jsonConfig = new JSONObject(content);
        } catch (Exception e) {
            logger.warning("JSONファイルの読み込みに失敗しました: " + e.getMessage());
            return;
        }

        Set<String> keys = jsonConfig.keySet();
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
                    String nickname = jsonConfig.optString(uuid, "").trim();
                    if (!nickname.isBlank()) {
                        pstmt.setString(1, uuid);
                        pstmt.setString(2, nickname);
                        pstmt.executeUpdate();
                        logger.info("データベースへのニックネームの統合に成功しました: " + uuid);
                    } else {
                        logger.warning("UUID " + uuid + " のニックネームが無効です。スキップします。");
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
        File jsonFile = new File(mod.getDataFolder(), "nickname.json");
        String dbPath = new File(mod.getDataFolder(), "nicknames.db").getPath();
        Logger logger = mod.getLogger();

        if (!jsonFile.exists()) {
            logger.warning("ニックネームの保存ファイルがありません。統合をスキップします。");
            return;
        }

        JSONObject jsonConfig;
        try {
            String content = Files.readString(jsonFile.toPath());
            jsonConfig = new JSONObject(content);
        } catch (Exception e) {
            logger.warning("JSONファイルの読み込みに失敗しました: " + e.getMessage());
            return;
        }

        Set<String> keys = jsonConfig.keySet();
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
                    String nickname = jsonConfig.optString(uuid, "").trim();
                    if (!nickname.isBlank()) {
                        pstmt.setString(1, uuid);
                        pstmt.setString(2, nickname);
                        pstmt.executeUpdate();
                        logger.info("Forge用データベースへのニックネームの統合に成功しました: " + uuid);
                    } else {
                        logger.warning("UUID " + uuid + " のニックネームが無効です。スキップします。");
                    }
                }
            }
        } catch (SQLException e) {
            logger.warning("Forge用データベースへのニックネームの統合に失敗しました: " + e.getMessage());
        }
    }
}
