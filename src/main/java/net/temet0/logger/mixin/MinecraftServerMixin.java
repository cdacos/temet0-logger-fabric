package net.temet0.logger.mixin;

import java.util.UUID;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.temet0.logger.loggers.LoggerManager;

@Environment(EnvType.SERVER)
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {
    private final String[] chatKeys = { "chat.type.advancement", "chat.type.announcement", "chat.type.text", "death", "multiplayer" };

    @Shadow
    public abstract PlayerManager getPlayerManager();

    @Inject(at = @At("TAIL"), method = "sendSystemMessage")
    public void sendSystemMessage(Text message, UUID senderUuid, CallbackInfo ci) {
        if (message instanceof TranslatableText) {
            TranslatableText translatableText = (TranslatableText) message;
            String key = translatableText.getKey();

            for (String chatKey : chatKeys) {
                if (key.startsWith(chatKey)) {
                    ServerPlayerEntity player = this.getPlayerManager().getPlayer(senderUuid);
                    LoggerManager.INSTANCE.logChat(player, translatableText.getString());
                }
            }
        }
    }
}