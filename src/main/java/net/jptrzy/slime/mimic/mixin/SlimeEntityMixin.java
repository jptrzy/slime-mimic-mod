package net.jptrzy.slime.mimic.mixin;

import net.jptrzy.slime.mimic.Main;
import net.jptrzy.slime.mimic.entity.mob.SlimeMimicEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.mob.SlimeEntity;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SlimeEntity.class)
public class SlimeEntityMixin {

//	@Inject(at = @At("HEAD"), method = "remove")
//	private void remove(Entity.RemovalReason reason, CallbackInfo info){
//	}

	@Inject(at = @At("HEAD"), method = "getParticles", cancellable = true)
	private void getParticles(CallbackInfoReturnable<ParticleEffect> cir) {
//		Main.LOGGER.warn("FUCK");
//		if(((SlimeEntity) (Object) this) instanceof SlimeMimicEntity){
//			Main.LOGGER.warn("FUCK");
//		}
		cir.setReturnValue(ParticleTypes.DRIPPING_HONEY);
	}
}
