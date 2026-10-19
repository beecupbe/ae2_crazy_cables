package dev.beecube31.crazycables.common.me;

import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;

import javax.annotation.Nullable;

import appeng.api.networking.IGridNode;
import appeng.api.networking.IManagedGridNode;

import dev.beecube31.crazycables.common.interfaces.mixin.me.ICrazyGridFlagsHolder;

public final class CrazyGridHelper {
    private CrazyGridHelper() {}

    public static void setCrazyGridFlags(IManagedGridNode managedNode, CrazyGridFlags... flags) {
        EnumSet<CrazyGridFlags> set = EnumSet.noneOf(CrazyGridFlags.class);
        Collections.addAll(set, flags);
        ((ICrazyGridFlagsHolder) managedNode).crazyae2cables$setCrazyGridFlags(set);
    }

    public static Set<CrazyGridFlags> getCrazyGridFlags(@Nullable IGridNode node) {
        if (node instanceof ICrazyGridFlagsHolder holder) {
            return holder.crazyae2cables$getCrazyGridFlags();
        }
        return Collections.emptySet();
    }

    public static boolean hasCrazyGridFlag(@Nullable IGridNode node, CrazyGridFlags flag) {
        return getCrazyGridFlags(node).contains(flag);
    }

    /**
     * @return the biggest capacity among the node's crazy flags, or -1 if it has none
     */
    public static int getCrazyCapacity(Set<CrazyGridFlags> flags) {
        if (flags.isEmpty()) {
            return -1;
        }
        int max = 0;
        for (CrazyGridFlags flag : flags) {
            max = Math.max(max, flag.getChannelCapacity());
        }
        return max;
    }
}
