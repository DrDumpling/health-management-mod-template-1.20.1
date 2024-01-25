package net.dumploid.healthmanagementmod.util;

import net.dumploid.healthmanagementmod.HealthManagementMod;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.world.GameMode;

import java.util.Objects;

public class HealthManagement {
    public static void manageDeath(ServerPlayerEntity killedPlayer) {
        DeathData.incrementDeaths((IEntityDataSaver) killedPlayer);

        int deathCount = DeathData.getDeathCount((IEntityDataSaver) killedPlayer);
        int maxDeathCount = killedPlayer.getEntityWorld().getGameRules().getInt(HealthManagementMod.MAX_DEATH_COUNT);

        if(deathCount >= maxDeathCount) {
            if(maxDeathCount > 1)
                killedPlayer.sendMessage(Text.literal("You have died " + deathCount + " times, you no longer may respawn.").formatted(Formatting.RED));
            else
                killedPlayer.sendMessage(Text.literal("You have died.").formatted(Formatting.RED));

            killedPlayer.changeGameMode(GameMode.SPECTATOR);
        } else {
            String deathCountMessage = "You have died " + deathCount + " times.";
            if(deathCount == 1) {
                deathCountMessage = "You have died " + deathCount + " time.";
            }
            killedPlayer.sendMessage(Text.literal(deathCountMessage).formatted(Formatting.RED));

            int livesLeft = maxDeathCount - deathCount;

            String livesLeftMessage = "You have " + livesLeft + " lives left.";
            if(livesLeft == 1) {
                livesLeftMessage = "You have " + livesLeft + " life left.";
            }
            killedPlayer.sendMessage(Text.literal(livesLeftMessage).formatted(Formatting.RED));
        }
    }

    public static void manageAttacker(ServerPlayerEntity attacker) {
        DeathData.incrementPlayerKillCount((IEntityDataSaver) attacker);

        updateMaxHealth(attacker);
        sendKillMessages(attacker);

        if(DeathData.getPlayerKillCount((IEntityDataSaver) attacker) >= 4) {
            attacker.changeGameMode(GameMode.SPECTATOR);

            attacker.getServer().getPlayerManager().broadcast(
                    Text.literal(attacker.getEntityName() + " has lost all of their hearts and perished."),
                    false
            );
        }
    }

    public static void updateMaxHealth(PlayerEntity attacker) {
        int playerKillCount = DeathData.getPlayerKillCount((IEntityDataSaver) attacker);

        if (playerKillCount == 0) return;
        int removedMaxHealth = playerKillCount * -2;

        Objects.requireNonNull(attacker.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH))
                .addPersistentModifier(new EntityAttributeModifier("foobar", removedMaxHealth, EntityAttributeModifier.Operation.ADDITION));

        double newMaxHealth = Objects.requireNonNull(attacker.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)).getValue();
        if(newMaxHealth < attacker.getHealth()) attacker.setHealth((float) newMaxHealth);

        HealthManagementMod.LOGGER.info(attacker.getEntityName() + "'s max health has been set to " + newMaxHealth);
    }

    public static void sendKillMessages(PlayerEntity attacker) {
        int playerKillCount = DeathData.getPlayerKillCount((IEntityDataSaver) attacker);
        boolean firstKill = playerKillCount == 1;
        boolean killAttackingPlayer = playerKillCount == 4;

        attacker.sendMessage(Text.literal("You have killed another player.").formatted(Formatting.RED));

        if (firstKill) {
            attacker.sendMessage(Text.literal("Your max health has been reduced by 1 heart.").formatted(Formatting.RED)); // plurality clause
            attacker.sendMessage(Text.literal("Killing more players will reduce your health further.").formatted(Formatting.RED));
        }
        if (!firstKill && !killAttackingPlayer) {
            attacker.sendMessage(Text.literal("Your max health has been reduced by " + playerKillCount + " hearts.").formatted(Formatting.RED));
        }

        if (killAttackingPlayer) {
            attacker.sendMessage(Text.literal("Bye bye!").formatted(Formatting.RED));
        }
    }

}
