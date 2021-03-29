package net.temet0.logger.loggers;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;

public enum LoggerManager implements ILogger {
    INSTANCE;

    private static final long MILLISECONDS_BETWEEN_AUTO_POSITION_LOGS = 60 * 1000;

    private final List<ILogger> loggers = new ArrayList<ILogger>();

    public void Add(ILogger logger) {
        loggers.add(logger);
    }

    private static long lastLogTime = 0;
	public static void serverTick(MinecraftServer server) {
		long currentTime = System.currentTimeMillis();
		if (currentTime - lastLogTime > MILLISECONDS_BETWEEN_AUTO_POSITION_LOGS)
		{
			server.getWorlds().forEach((world)->{
				List<ServerPlayerEntity> players = world.getPlayers();
				for (ServerPlayerEntity player : players) {
                    LoggerManager.INSTANCE.logPosition(player, null);
				}
			});
			lastLogTime = currentTime;
		}
	}

    @Override
    public void logMessage(String message) {
        for (ILogger logger : loggers) {
            logger.logMessage(message);
        }
    }

    @Override
    public void logPosition(ServerPlayerEntity player, String message) {
        for (ILogger logger : loggers) {
            logger.logPosition(player, message);
        }
    }

    @Override
    public void logChat(ServerPlayerEntity player, String message) {
        for (ILogger logger : loggers) {
            logger.logChat(player, message);
        }
    }
}
