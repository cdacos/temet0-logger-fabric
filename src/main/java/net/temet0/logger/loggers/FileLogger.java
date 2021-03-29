package net.temet0.logger.loggers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;
import net.temet0.logger.utils.LoggerUtils;

public class FileLogger implements ILogger {
    private final DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	private File writeFile;

    public FileLogger(File dir) {
        writeFile = new File(dir + "/log.txt");
    }

    @Override
    public void logMessage(String message) {
		writeToFile("MESSAGE", null, message);
    }

    @Override
    public void logPosition(ServerPlayerEntity player, String message) {
		writeToFile("POSITION", player, message);
    }

    @Override
    public void logChat(ServerPlayerEntity player, String message) {
        writeToFile("CHAT", player, message);
    }

    private void writeToFile(String event, ServerPlayerEntity player, String message) {
		try {
			String line;

			if (player != null) {
				String playerName = player.getEntityName();
				String worldName = LoggerUtils.getWorldName(player);
				Vec3d position = player.getPos();
				message = message != null ? message : "";
				line = String.format("[%s] %s: {%s} %s %s %s", getCurrentTime(), event, playerName, worldName, position, message);
			} else {
				line = String.format("[%s] %s: %s", getCurrentTime(), event, message);
			}

			BufferedWriter out = new BufferedWriter(new FileWriter(writeFile, true));
			out.append(line + "\n");
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String getCurrentTime() {
		return LocalDateTime.now().format(timeFormat);
	}
}
