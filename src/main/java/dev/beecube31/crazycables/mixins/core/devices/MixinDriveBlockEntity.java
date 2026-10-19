package dev.beecube31.crazycables.mixins.core.devices;

import appeng.api.orientation.BlockOrientation;
import appeng.blockentity.storage.DriveBlockEntity;
import net.minecraft.core.Direction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;
import java.util.Set;

/**
 * AE2 ME Drives refuse grid connections on their front face. In a flat drive wall whose fronts face
 * along the column, every drive is cut off from the next one and only the row touching the cables gets
 * channels
 */
@Mixin(value = DriveBlockEntity.class, remap = false)
public abstract class MixinDriveBlockEntity {
    @Inject(method = "getGridConnectableSides", at = @At("HEAD"), cancellable = true)
    private void crazyae2cables$connectOnAllSides(BlockOrientation orientation, CallbackInfoReturnable<Set<Direction>> cir) {
        cir.setReturnValue(EnumSet.allOf(Direction.class));
    }
}
