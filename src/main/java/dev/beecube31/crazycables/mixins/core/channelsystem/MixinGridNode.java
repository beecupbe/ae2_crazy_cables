package dev.beecube31.crazycables.mixins.core.channelsystem;

import appeng.api.networking.GridFlags;
import appeng.api.networking.IGrid;
import appeng.api.parts.IPart;
import appeng.blockentity.networking.ControllerBlockEntity;
import appeng.me.GridNode;
import appeng.me.pathfinding.IPathItem;
import dev.beecube31.crazycables.common.interfaces.mixin.me.ICrazyGridFlagsHolder;
import dev.beecube31.crazycables.common.interfaces.mixin.me.ICrazyPathNode;
import dev.beecube31.crazycables.common.me.CrazyGridFlags;
import dev.beecube31.crazycables.common.me.CrazyGridHelper;
import dev.beecube31.crazycables.core.CrazyCablesConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.EnumSet;
import java.util.Set;

@Mixin(value = GridNode.class, remap = false)
public abstract class MixinGridNode implements ICrazyGridFlagsHolder, ICrazyPathNode {

    @Unique
    private Set<CrazyGridFlags> crazyae$crazyFlags = EnumSet.noneOf(CrazyGridFlags.class);

    @Unique
    private int crazyae$crazyCapacity = -1;

    /**
     * Capacity inherited from the CrazyAE cable this device is routed through, -1 = none.
     * Set by the pathing BFS (setControllerRoute) before the node asks for a channel.
     */
    @Unique
    private int crazyae$inheritedCapacity = -1;

    @Shadow
    public abstract IGrid getGrid();

    @Shadow
    public abstract Object getOwner();

    @Shadow
    public abstract boolean hasFlag(GridFlags flag);

    @Override
    public Set<CrazyGridFlags> crazyae$getCrazyGridFlags() {
        return this.crazyae$crazyFlags;
    }

    @Override
    public void crazyae$setCrazyGridFlags(Set<CrazyGridFlags> flags) {
        this.crazyae$crazyFlags = flags.isEmpty() ? EnumSet.noneOf(CrazyGridFlags.class) : EnumSet.copyOf(flags);
        this.crazyae$crazyCapacity = this.crazyae$crazyFlags.isEmpty() ? -1 : 0;
    }

    @Override
    public boolean crazyae2cables$hasCrazyCapacity() {
        return this.crazyae$crazyCapacity != -1;
    }

    @Override
    public int crazyae2cables$getInheritedCapacity() {
        return this.crazyae$inheritedCapacity;
    }

    /**
     * 1.12.2 behavior: a device behind a Crazy cable is not a bottleneck of 8 channels anymore,
     * so more than 8 devices can hang off one side of the cable (e.g. a column of drives).
     * Only full-block devices inherit; cables, dense nodes, parts and the controller stay vanilla.
     */
    @Inject(method = "setControllerRoute", at = @At("HEAD"))
    private void crazyae$inheritCableCapacity(IPathItem fast, CallbackInfo ci) {
        this.crazyae$inheritedCapacity = -1;

        if (!CrazyCablesConfig.devicesInheritCableCapacity || this.crazyae$crazyCapacity != -1) {
            return;
        }
        if (this.hasFlag(GridFlags.CANNOT_CARRY) || this.hasFlag(GridFlags.PREFERRED)
                || this.hasFlag(GridFlags.DENSE_CAPACITY) || this.getOwner() instanceof IPart) {
            return;
        }
        if (!(fast.getControllerRoute() instanceof GridNode parent)
                || parent.getOwner() instanceof ControllerBlockEntity) {
            return;
        }

        var parentPath = (ICrazyPathNode) parent;
        if (parentPath.crazyae2cables$hasCrazyCapacity() || parentPath.crazyae2cables$getInheritedCapacity() > 0) {
            int capacity = parent.getMaxChannels();
            if (capacity > 0) {
                this.crazyae$inheritedCapacity = capacity;
            }
        }
    }

    @Inject(method = "getMaxChannels", at = @At("RETURN"), cancellable = true)
    private void crazyae$getMaxChannels(CallbackInfoReturnable<Integer> cir) {
        final int base = cir.getReturnValueI();

        // CANNOT_CARRY (0) and ChannelMode.INFINITE (MAX_VALUE) stay as AE2 decided
        if (base <= 0 || base == Integer.MAX_VALUE) {
            return;
        }

        if (this.crazyae$crazyCapacity != -1) {
            // Capacity is read every call because wireless connector values come from the config
            int capacity = CrazyGridHelper.getCrazyCapacity(this.crazyae$crazyFlags);
            if (capacity > 0) {
                cir.setReturnValue(capacity);
                return;
            }
        }

        if (this.crazyae$inheritedCapacity > base) {
            cir.setReturnValue(this.crazyae$inheritedCapacity);
        }
    }
}
