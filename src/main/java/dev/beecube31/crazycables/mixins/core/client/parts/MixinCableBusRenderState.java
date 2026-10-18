package dev.beecube31.crazycables.mixins.core.client.parts;

import appeng.client.render.cablebus.CableBusRenderState;
import dev.beecube31.crazycables.common.interfaces.mixin.render.ICrazyCableBusRenderState;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Objects;

@Mixin(value = CableBusRenderState.class, remap = false)
public abstract class MixinCableBusRenderState implements ICrazyCableBusRenderState {

    @Unique
    @Nullable
    private CrazyCableType crazyae$crazyCableType;

    @Override
    public CrazyCableType crazyae$getCrazyCableType() {
        return this.crazyae$crazyCableType;
    }

    @Override
    public void crazyae$setCrazyCableType(@Nullable CrazyCableType type) {
        this.crazyae$crazyCableType = type;
    }

    @Inject(method = "hashCode", at = @At("RETURN"), cancellable = true)
    private void crazyae$hashCode(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(31 * cir.getReturnValueI() + Objects.hashCode(this.crazyae$crazyCableType));
    }

    @Inject(method = "equals", at = @At("RETURN"), cancellable = true)
    private void crazyae$equals(Object o, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValueZ() && o != this) {
            CrazyCableType other = o instanceof ICrazyCableBusRenderState state ? state.crazyae$getCrazyCableType() : null;
            if (other != this.crazyae$crazyCableType) {
                cir.setReturnValue(false);
            }
        }
    }
}
