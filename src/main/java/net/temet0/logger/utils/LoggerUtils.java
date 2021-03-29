package net.temet0.logger.utils;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LoggerUtils {
    public static String getWorldName(ServerPlayerEntity playerEntity) {
		net.minecraft.world.World world = playerEntity.getEntityWorld();
		Identifier worldId = world.getRegistryManager().get(Registry.DIMENSION_TYPE_KEY).getId(world.getDimension());
		return worldId.getPath();
	}
}
