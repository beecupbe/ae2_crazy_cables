package dev.beecube31.crazycables.mixins.core.channelsystem;

import appeng.me.GridNode;
import appeng.me.ManagedGridNode;
import dev.beecube31.crazycables.common.interfaces.mixin.me.ICrazyGridFlagsHolder;
import dev.beecube31.crazycables.common.me.CrazyGridFlags;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.Set;

@Mixin(value = ManagedGridNode.class, remap = false)
public abstract class MixinManagedGridNode implements ICrazyGridFlagsHolder {

    @Shadow
    @Nullable
    private GridNode node;

    @Unique
    private Set<CrazyGridFlags> crazyae2cables$crazyFlags = EnumSet.noneOf(CrazyGridFlags.class);

    @Override
    public Set<CrazyGridFlags> crazyae2cables$getCrazyGridFlags() {
        return this.crazyae2cables$crazyFlags;
    }

    @Override
    public void crazyae2cables$setCrazyGridFlags(Set<CrazyGridFlags> flags) {
        this.crazyae2cables$crazyFlags = flags.isEmpty() ? EnumSet.noneOf(CrazyGridFlags.class) : EnumSet.copyOf(flags);

        if (this.node != null) {
            ((ICrazyGridFlagsHolder) this.node).crazyae2cables$setCrazyGridFlags(this.crazyae2cables$crazyFlags);
            if (this.node.getGrid() != null) {
                this.node.getGrid().getPathingService().repath();
            }
        }
    }

    @Inject(method = "createNode", at = @At(value = "INVOKE", target = "Lappeng/me/GridNode;markReady()V"))
    private void crazyae2cables$applyCrazyFlags(CallbackInfo ci) {
        if (this.node != null && !this.crazyae2cables$crazyFlags.isEmpty()) {
            ((ICrazyGridFlagsHolder) this.node).crazyae2cables$setCrazyGridFlags(this.crazyae2cables$crazyFlags);
        }
    }
}
