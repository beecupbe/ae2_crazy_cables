package dev.beecube31.crazycables.mixins.core.client.parts;

import dev.beecube31.crazycables.client.rendering.parts.CrazyCableBuilder;
import dev.beecube31.crazycables.common.interfaces.mixin.render.ICrazyCableBuilderHolder;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(targets = "appeng.client.render.cablebus.CableBuilder", remap = false)
public abstract class MixinCableBuilder implements ICrazyCableBuilderHolder {

    @Unique
    private CrazyCableBuilder crazyae2cables$crazyCableBuilder;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void crazyae2cables$init(MaterialBaker materials, CallbackInfo ci) {
        this.crazyae2cables$crazyCableBuilder = new CrazyCableBuilder(materials);
    }

    @Override
    public CrazyCableBuilder crazyae2cables$getCrazyCableBuilder() {
        return this.crazyae2cables$crazyCableBuilder;
    }
}
