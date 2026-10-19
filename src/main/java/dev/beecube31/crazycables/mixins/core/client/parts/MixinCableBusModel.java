package dev.beecube31.crazycables.mixins.core.client.parts;

import appeng.api.parts.IPartItem;
import appeng.block.networking.CableBusRenderState;
import appeng.block.networking.CableCoreType;
import appeng.client.api.model.parts.PartModel;
import appeng.client.render.cablebus.CableBusModel;
import appeng.client.render.cablebus.FacadeBuilder;
import dev.beecube31.crazycables.client.rendering.parts.CrazyCableBuilder;
import dev.beecube31.crazycables.client.rendering.parts.CrazyCableRenderer;
import dev.beecube31.crazycables.common.interfaces.mixin.render.ICrazyCableBuilderHolder;
import dev.beecube31.crazycables.common.interfaces.mixin.render.ICrazyCableBusRenderState;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * AE2 26.1 replaced CableBusBakedModel with CableBusModel (a DynamicBlockStateModel). Same hooks as before:
 * cable quads and the core particle texture.
 */
@Mixin(value = CableBusModel.class, remap = false)
public abstract class MixinCableBusModel {

    @Unique
    private CrazyCableBuilder crazyae2cables$crazyCableBuilder;

    @Shadow
    private boolean canAllAttachmentsRenderOnStraightCable(CableBusRenderState renderState) {
        throw new AssertionError();
    }

    @Inject(method = "<init>", at = @At("RETURN"))
    private void crazyae2cables$init(@Coerce Object cableBuilder, FacadeBuilder facadeBuilder,
            Map<IPartItem<?>, PartModel[]> partModels, Material.Baked particleTexture, CallbackInfo ci) {
        if (cableBuilder instanceof ICrazyCableBuilderHolder holder) {
            this.crazyae2cables$crazyCableBuilder = holder.crazyae2cables$getCrazyCableBuilder();
        }
    }

    @Inject(method = "getCableQuads", at = @At("HEAD"), cancellable = true)
    private void crazyae2cables$addCrazyCableQuads(CableBusRenderState renderState, Consumer<BakedQuad> quadsOut,
            CallbackInfo ci) {
        CrazyCableType type = ((ICrazyCableBusRenderState) renderState).crazyae2cables$getCrazyCableType();
        if (type != null && this.crazyae2cables$crazyCableBuilder != null) {
            CrazyCableRenderer.addCableQuads(this.crazyae2cables$crazyCableBuilder, type, renderState,
                    this.canAllAttachmentsRenderOnStraightCable(renderState), quadsOut);
            ci.cancel();
        }
    }

    @Inject(method = "getParticleMaterials", at = @At("RETURN"))
    private void crazyae2cables$particleTextures(CableBusRenderState renderState,
            CallbackInfoReturnable<List<Material.Baked>> cir) {
        CrazyCableType type = ((ICrazyCableBusRenderState) renderState).crazyae2cables$getCrazyCableType();
        if (type == null || this.crazyae2cables$crazyCableBuilder == null
                || CableCoreType.fromCableType(renderState.getCableType()) == null) {
            return;
        }
        List<Material.Baked> result = cir.getReturnValue();
        if (!result.isEmpty()) {
            // AE2 puts the core texture first
            result.set(0, this.crazyae2cables$crazyCableBuilder.getCoreTexture(type, renderState.getCableColor()));
        }
    }
}
