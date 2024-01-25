package net.dumploid.healthmanagementmod.mixin;

import net.dumploid.healthmanagementmod.util.HealthManagement;
import net.minecraft.entity.Entity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin {
    @Inject(method="onDeath", at=@At("TAIL"))
    private void injectOnDeath(DamageSource damageSource, CallbackInfo ci) {
        HealthManagement.manageDeath((ServerPlayerEntity) (Object) this);

        Entity attacker = damageSource.getAttacker();
        if(attacker != null && attacker.isPlayer()) {
            HealthManagement.manageAttacker((ServerPlayerEntity) attacker);
        }
    }
}
