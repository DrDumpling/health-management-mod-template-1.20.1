package net.dumploid.healthmanagementmod.mixin;

import net.dumploid.healthmanagementmod.HealthManagementMod;
import net.minecraft.entity.Entity;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerMixin {
    @Inject(method="onDeath", at=@At("TAIL"))
    private void died(DamageSource damageSource, CallbackInfo ci) {
        Entity attacker = damageSource.getAttacker();

        if(attacker == null) return;
        if(!attacker.isPlayer()) return;
        PlayerEntity attackingPlayer = (PlayerEntity) attacker;

        double attackingPlayerMaxHealth = attackingPlayer.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getValue();
        int removedHealth = switch((int) attackingPlayerMaxHealth) {
            case 20 -> -2;
            case 18 -> -4;
            case 14 -> -6;
            default -> -8;
        };

        boolean firstKill = removedHealth == -2;
        boolean killAttackingPlayer = removedHealth == -8;

        attackingPlayer.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .addPersistentModifier(new EntityAttributeModifier("foobar", removedHealth, EntityAttributeModifier.Operation.ADDITION));


        double newMaxHealth = attackingPlayer.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH).getValue();
        if(newMaxHealth < attackingPlayer.getHealth()) attackingPlayer.setHealth((float) newMaxHealth);

        HealthManagementMod.LOGGER.info(attackingPlayer.getEntityName() + "'s health has been set to " + (attackingPlayerMaxHealth + removedHealth));

        {
            attackingPlayer.sendMessage(Text.literal("You have killed another player.").formatted(Formatting.RED));

            if (firstKill) {
                attackingPlayer.sendMessage(Text.literal("Your max health has been reduced by 1 heart.").formatted(Formatting.RED)); // plurality clause
                attackingPlayer.sendMessage(Text.literal("Killing more players will reduce your health further.").formatted(Formatting.RED));
            }
            if (!firstKill && !killAttackingPlayer) {
                int displayedRemovedHearts = -removedHealth / 2;
                attackingPlayer.sendMessage(Text.literal("Your max health has been reduced by " + displayedRemovedHearts + " hearts.").formatted(Formatting.RED));
            }

            if (killAttackingPlayer) attackingPlayer.sendMessage(Text.literal("Bye bye!").formatted(Formatting.RED));
        }

        if(killAttackingPlayer) {
            attackingPlayer.kill();
            HealthManagementMod.LOGGER.info(attackingPlayer.getEntityName() + " has been killed for killing 4 players.");
        }
    }
}
