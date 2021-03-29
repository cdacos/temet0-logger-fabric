package net.temet0.logger.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import net.temet0.logger.models.Player;

public class ChatData {
    public boolean Create(Connection connection, Player player, String message) {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO chats (player_id, message) VALUES(?, ?);");
            stmt.setInt(1, player.PlayerId);
            stmt.setString(2, message);
            stmt.execute();
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    public boolean Schema(Connection connection)
    {
        boolean result = false;
        try {
            PreparedStatement stmt = connection.prepareStatement("CREATE TABLE IF NOT EXISTS chats ( \n" +
            "    chat_id INTEGER PRIMARY KEY AUTOINCREMENT, \n" +
            "    player_id INTEGER, \n" +
            "    message TEXT, \n" +
            "    created DATETIME DEFAULT CURRENT_TIMESTAMP, \n" +
            "    FOREIGN KEY(player_id) REFERENCES players(player_id) \n" +
            ");");
            result = stmt.execute();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
}
