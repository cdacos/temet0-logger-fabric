package net.temet0.logger.loggers;

import net.minecraft.server.network.ServerPlayerEntity;

public interface ILogger {
    void logMessage(String message);

    void logPosition(ServerPlayerEntity player, String message);
}
