package dev.beecube31.crazycables.mixins.core.client.parts;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.block.networking.CableBusRenderState;
import appeng.parts.CableBusContainer;

import dev.beecube31.crazycables.common.interfaces.mixin.render.ICrazyCableBusRenderState;
import dev.beecube31.crazycables.common.parts.cables.CrazyCablePart;

@Mixin(value = CableBusContainer.class, remap = false)
public abstract class MixinCableBusContainer {

    @Inject(method = "getRenderState", at = @At("RETURN"))
    private void crazyae2cables$addCrazyCableType(CallbackInfoReturnable<CableBusRenderState> cir) {
        CableBusRenderState state = cir.getReturnValue();
        if (state == null) {
            return;
        }
        var center = ((CableBusContainer) (Object) this).getPart(null);
        ((ICrazyCableBusRenderState) state).crazyae2cables$setCrazyCableType(
                center instanceof CrazyCablePart crazy ? crazy.getCrazyCableType() : null);
    }
}
