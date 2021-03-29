package net.temet0.logger.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.temet0.logger.loggers.LoggerManager;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
	@Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getPrimeAdversary()Lnet/minecraft/entity/LivingEntity;"))
	public void onDeath(DamageSource source, CallbackInfo ci) {
		ServerPlayerEntity player = (ServerPlayerEntity) (Object)this;
		String deathMessage = player.getDamageTracker().getDeathMessage().getString();
		LoggerManager.INSTANCE.logPosition((ServerPlayerEntity) (Object) this, deathMessage);
	}
}