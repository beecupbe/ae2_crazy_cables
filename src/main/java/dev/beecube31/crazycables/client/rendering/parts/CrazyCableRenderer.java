package dev.beecube31.crazycables.client.rendering.parts;

import appeng.api.parts.IPartModel;
import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.client.render.cablebus.CableBusRenderState;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class CrazyCableRenderer {
    private CrazyCableRenderer() {}

    public static void addCableQuads(CrazyCableBuilder builder, CrazyCableType type, CableBusRenderState renderState,
            List<BakedQuad> quadsOut) {
        if (renderState.getCableType() == AECableType.NONE) {
            return;
        }

        AEColor cableColor = renderState.getCableColor();
        EnumMap<Direction, AECableType> connectionTypes = renderState.getConnectionTypes();

        boolean noAttachments = renderState.getAttachments().values().stream()
                .noneMatch(IPartModel::requireCableConnection);
        if (noAttachments && isStraightLine(connectionTypes)) {
            Direction facing = connectionTypes.keySet().iterator().next();
            builder.addStraightCoveredConnection(type, facing, cableColor, quadsOut);
            return;
        }

        builder.addCableCore(type, cableColor, quadsOut);

        for (Map.Entry<Direction, Integer> entry : renderState.getAttachmentConnections().entrySet()) {
            Direction facing = entry.getKey();
            builder.addConstrainedCoveredConnection(type, facing, cableColor, entry.getValue(), quadsOut);
        }

        for (Map.Entry<Direction, AECableType> connection : connectionTypes.entrySet()) {
            Direction facing = connection.getKey();
            boolean cableBusAdjacent = renderState.getCableBusAdjacent().contains(facing);
            builder.addCoveredConnection(facing, type, cableColor, cableBusAdjacent, quadsOut);
        }
    }

    private static int channels(EnumMap<Direction, Integer> channelsOnSide, Direction side) {
        Integer value = channelsOnSide.get(side);
        return value != null ? value : 0;
    }

    /**
     * Same as AE2's CableBusBakedModel#isStraightLine for the SMART cable type.
     */
    private static boolean isStraightLine(EnumMap<Direction, AECableType> sides) {
        if (sides.size() != 2) {
            return false;
        }
        var it = sides.entrySet().iterator();
        var first = it.next();
        var second = it.next();
        if (first.getKey().getOpposite() != second.getKey()) {
            return false;
        }
        return first.getValue() == AECableType.SMART && second.getValue() == AECableType.SMART;
    }
}
