package dev.beecube31.crazycables.common.parts.cables;

import net.minecraft.world.entity.player.Player;

import appeng.api.networking.GridFlags;
import appeng.api.util.AEColor;
import appeng.items.parts.ColoredPartItem;
import appeng.parts.networking.SmartCablePart;

import dev.beecube31.crazycables.common.me.CrazyGridHelper;
import dev.beecube31.crazycables.common.registration.CrazyParts;

public class CrazyCablePart extends SmartCablePart {
    private final CrazyCableType crazyCableType;

    public CrazyCablePart(ColoredPartItem<?> partItem, CrazyCableType type) {
        super(partItem);
        this.crazyCableType = type;
        // DENSE_CAPACITY puts the cable into the first pathfinding queue,
        // the capacity itself is overridden by the crazy flag
        this.getMainNode().setFlags(GridFlags.PREFERRED, GridFlags.DENSE_CAPACITY);
        CrazyGridHelper.setCrazyGridFlags(this.getMainNode(), type.getGridFlag());
    }

    public CrazyCableType getCrazyCableType() {
        return crazyCableType;
    }

    @Override
    public boolean changeColor(AEColor newColor, Player who) {
        if (this.getCableColor() == newColor) {
            return false;
        }

        ColoredPartItem<CrazyCablePart> newPart = CrazyParts.cable(this.crazyCableType, newColor);
        if (newPart == null) {
            return false;
        }

        if (this.isClientSide()) {
            return true;
        }

        this.setPartItem(newPart);
        this.getMainNode().setGridColor(newColor);
        this.getHost().partChanged();
        this.getHost().markForUpdate();
        this.getHost().markForSave();
        return true;
    }

    @Override
    public int getMaxChannelsInfo() {
        var node = this.getGridNode();
        if (node == null) {
            return 0;
        }
        int max = node.getMaxChannels();
        return max == Integer.MAX_VALUE ? -1 : max;
    }

}
