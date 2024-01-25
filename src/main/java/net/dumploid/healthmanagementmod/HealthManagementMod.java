package net.dumploid.healthmanagementmod;

import net.dumploid.healthmanagementmod.util.PlayerCopyHandler;
import net.dumploid.healthmanagementmod.util.PlayerRespawnHandler;
import net.fabricmc.api.ModInitializer;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HealthManagementMod implements ModInitializer {
	public static final String MOD_ID = "health_management_mod";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static final GameRules.Key<GameRules.IntRule> MAX_DEATH_COUNT = GameRuleRegistry.register(
			"maxDeathCount", GameRules.Category.PLAYER, GameRuleFactory.createIntRule(1, 1)
	);

	@Override
	public void onInitialize() {
		LOGGER.info("Started Health Management Mod!");

		ServerPlayerEvents.COPY_FROM.register(new PlayerCopyHandler());
		ServerPlayerEvents.AFTER_RESPAWN.register(new PlayerRespawnHandler());
	}
}