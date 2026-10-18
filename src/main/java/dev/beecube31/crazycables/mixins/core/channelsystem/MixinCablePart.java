package dev.beecube31.crazycables.mixins.core.channelsystem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import appeng.parts.networking.CablePart;

import dev.beecube31.crazycables.common.parts.cables.CrazyCablePart;

@Mixin(value = CablePart.class, remap = false)
public abstract class MixinCablePart {

    @Inject(method = "getVisualChannels", at = @At("HEAD"), cancellable = true)
    private void crazyae$creativeVisualChannels(int channels, CallbackInfoReturnable<Byte> cir) {
        if ((Object) this instanceof CrazyCablePart crazy && crazy.getCrazyCableType().isCreative()) {
            cir.setReturnValue((byte) (channels > 0 ? 8 : 0));
        }
    }
}
