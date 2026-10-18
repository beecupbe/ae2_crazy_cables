package dev.beecube31.crazycables.mixins.core.client.parts;

import appeng.client.render.cablebus.CableBusBakedModel;
import appeng.client.render.cablebus.CableBusRenderState;
import appeng.client.render.cablebus.CableCoreType;
import appeng.client.render.cablebus.FacadeBuilder;
import dev.beecube31.crazycables.client.rendering.parts.CrazyCableBuilder;
import dev.beecube31.crazycables.client.rendering.parts.CrazyCableRenderer;
import dev.beecube31.crazycables.common.interfaces.mixin.render.ICrazyCableBuilderHolder;
import dev.beecube31.crazycables.common.interfaces.mixin.render.ICrazyCableBusRenderState;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;
import java.util.Map;

@Mixin(value = CableBusBakedModel.class, remap = false)
public abstract class MixinCableBusBakedModel {

    @Unique
    private CrazyCableBuilder crazyae$crazyCableBuilder;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void crazyae$init(@Coerce Object cableBuilder, FacadeBuilder facadeBuilder,
            Map<ResourceLocation, BakedModel> partModels, TextureAtlasSprite particleTexture, CallbackInfo ci) {
        if (cableBuilder instanceof ICrazyCableBuilderHolder holder) {
            this.crazyae$crazyCableBuilder = holder.crazyae$getCrazyCableBuilder();
        }
    }

    @Inject(method = "addCableQuads", at = @At("HEAD"), cancellable = true)
    private void crazyae$addCrazyCableQuads(CableBusRenderState renderState, List<BakedQuad> quadsOut,
            CallbackInfo ci) {
        CrazyCableType type = ((ICrazyCableBusRenderState) renderState).crazyae$getCrazyCableType();
        if (type != null && this.crazyae$crazyCableBuilder != null) {
            CrazyCableRenderer.addCableQuads(this.crazyae$crazyCableBuilder, type, renderState, quadsOut);
            ci.cancel();
        }
    }

    @Inject(method = "getParticleTextures", at = @At("RETURN"))
    private void crazyae$particleTextures(CableBusRenderState renderState,
            CallbackInfoReturnable<List<TextureAtlasSprite>> cir) {
        CrazyCableType type = ((ICrazyCableBusRenderState) renderState).crazyae$getCrazyCableType();
        if (type == null || this.crazyae$crazyCableBuilder == null
                || CableCoreType.fromCableType(renderState.getCableType()) == null) {
            return;
        }
        List<TextureAtlasSprite> result = cir.getReturnValue();
        if (!result.isEmpty()) {
            // AE2 puts the core texture first
            result.set(0, this.crazyae$crazyCableBuilder.getCoreTexture(type, renderState.getCableColor()));
        }
    }
}
