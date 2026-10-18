package dev.beecube31.crazycables.client.rendering.parts;

import appeng.api.util.AECableType;
import appeng.api.util.AEColor;
import appeng.client.render.cablebus.CubeBuilder;
import appeng.client.render.cablebus.SmartCableTextures;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import dev.beecube31.crazycables.core.CrazyAE2Cables;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class CrazyCableBuilder {
    private final EnumMap<CrazyCableType, EnumMap<AEColor, TextureAtlasSprite>> connectionTextures = new EnumMap<>(
            CrazyCableType.class);
    private final EnumMap<CrazyCableType, EnumMap<AEColor, TextureAtlasSprite>> coreTextures = new EnumMap<>(
            CrazyCableType.class);
    /**
     * Emissive overlay per cable tier (part/cable/emissive/<tier>), rendered full-bright on top of the base texture.
     */
    private final EnumMap<CrazyCableType, TextureAtlasSprite> emissiveTextures = new EnumMap<>(CrazyCableType.class);

    public CrazyCableBuilder(Function<Material, TextureAtlasSprite> spriteGetter) {
        for (CrazyCableType type : CrazyCableType.values()) {
            EnumMap<AEColor, TextureAtlasSprite> byColor = new EnumMap<>(AEColor.class);
            for (AEColor color : AEColor.values()) {
                byColor.put(color, spriteGetter.apply(getConnectionTexture(type, color)));
            }
            this.coreTextures.put(type, byColor);
            this.connectionTextures.put(type, byColor);
            this.emissiveTextures.put(type, spriteGetter.apply(getEmissiveTexture(type)));
        }
    }

    private static String colorName(AEColor color) {
        return color.name().toLowerCase(Locale.ROOT);
    }

    public static Material getConnectionTexture(CrazyCableType type, AEColor color) {
        return new Material(TextureAtlas.LOCATION_BLOCKS, new ResourceLocation(CrazyAE2Cables.MODID,
                "part/cable/" + type.getTextureFolder() + "/" + colorName(color)));
    }

    public static Material getCoreTextureMaterial(CrazyCableType type, AEColor color) {
        return new Material(TextureAtlas.LOCATION_BLOCKS,
                new ResourceLocation(CrazyAE2Cables.MODID, "part/cable/core/" + type.getTextureFolder() + "/" + colorName(color)));
    }

    public static Material getEmissiveTexture(CrazyCableType type) {
        return new Material(TextureAtlas.LOCATION_BLOCKS,
                new ResourceLocation(CrazyAE2Cables.MODID, "part/cable/emissive/" + type.getTextureFolder()));
    }

    public TextureAtlasSprite getEmissiveSprite(CrazyCableType type) {
        return this.emissiveTextures.get(type);
    }

    public TextureAtlasSprite getCoreTexture(CrazyCableType type, AEColor color) {
        return this.coreTextures.get(type).get(color);
    }

    private TextureAtlasSprite getTexture(CrazyCableType type, AEColor color) {
        return this.connectionTextures.get(type).get(color);
    }

    public void addCableCore(CrazyCableType type, AEColor color, List<BakedQuad> quadsOut) {
        CubeBuilder cubeBuilder = new CubeBuilder(quadsOut);
        cubeBuilder.setTexture(this.coreTextures.get(type).get(color));
        cubeBuilder.addCube(5, 5, 5, 11, 11, 11);

        addEmissiveLayer(cubeBuilder, type, quadsOut, () -> cubeBuilder.addCube(5, 5, 5, 11, 11, 11));
    }

    public void addCoveredConnection(Direction facing, CrazyCableType type, AEColor cableColor,
                                     boolean cableBusAdjacent, List<BakedQuad> quadsOut) {
        CubeBuilder cubeBuilder = new CubeBuilder(quadsOut);
        cubeBuilder.setDrawFaces(EnumSet.complementOf(EnumSet.of(facing)));
        cubeBuilder.setTexture(this.coreTextures.get(type).get(cableColor));

        if (!cableBusAdjacent) {
            addBigCoveredCableSizedCube(facing, cubeBuilder);
        }

        addCoveredCableSizedCube(facing, cubeBuilder);

        addEmissiveLayer(cubeBuilder, type, quadsOut, () -> {
            if (!cableBusAdjacent) {
                addBigCoveredCableSizedCube(facing, cubeBuilder);
            }
            addCoveredCableSizedCube(facing, cubeBuilder);
        });
    }

    public void addStraightCoveredConnection(CrazyCableType type, Direction facing, AEColor cableColor, List<BakedQuad> quadsOut) {
        CubeBuilder cubeBuilder = new CubeBuilder(quadsOut);

        cubeBuilder.setTexture(getTexture(type, cableColor));

        setStraightCableUVs(cubeBuilder, facing, 5, 11);

        addStraightCoveredCableSizedCube(facing, cubeBuilder);

        // Custom UVs stay set on the builder, so the overlay lines up with the base texture
        addEmissiveLayer(cubeBuilder, type, quadsOut, () -> addStraightCoveredCableSizedCube(facing, cubeBuilder));
    }

    public void addConstrainedCoveredConnection(CrazyCableType type, Direction facing, AEColor cableColor, int distanceFromEdge,
                                                List<BakedQuad> quadsOut) {
        if (distanceFromEdge >= 5) {
            return;
        }

        CubeBuilder cubeBuilder = new CubeBuilder(quadsOut);

        cubeBuilder.setTexture(getTexture(type, cableColor));

        addCoveredCableSizedCube(facing, distanceFromEdge, cubeBuilder);

        addEmissiveLayer(cubeBuilder, type, quadsOut,
                () -> addCoveredCableSizedCube(facing, distanceFromEdge, cubeBuilder));
    }

    private void addEmissiveLayer(CubeBuilder cubeBuilder, CrazyCableType type, List<BakedQuad> quadsOut,
                                  Runnable geometry) {
        int start = quadsOut.size();

        cubeBuilder.setTexture(this.emissiveTextures.get(type));
        cubeBuilder.setEmissiveMaterial(true);
        geometry.run();
        cubeBuilder.setEmissiveMaterial(false);

        for (int i = start; i < quadsOut.size(); i++) {
            BakedQuad quad = quadsOut.get(i);
            quadsOut.set(i, new BakedQuad(quad.getVertices(), quad.getTintIndex(), quad.getDirection(), quad.getSprite(), false, false));
        }
    }

    private static void setStraightCableUVs(CubeBuilder cubeBuilder, Direction facing, int x, int y) {
        switch (facing) {
            case DOWN, UP -> {
                cubeBuilder.setCustomUv(Direction.NORTH, x, 0, y, x);
                cubeBuilder.setCustomUv(Direction.EAST, x, 0, y, x);
                cubeBuilder.setCustomUv(Direction.SOUTH, x, 0, y, x);
                cubeBuilder.setCustomUv(Direction.WEST, x, 0, y, x);
            }
            case EAST, WEST -> {
                cubeBuilder.setCustomUv(Direction.UP, 0, x, x, y);
                cubeBuilder.setCustomUv(Direction.DOWN, 0, x, x, y);
                cubeBuilder.setCustomUv(Direction.NORTH, 0, x, x, y);
                cubeBuilder.setCustomUv(Direction.SOUTH, 0, x, x, y);
            }
            case NORTH, SOUTH -> {
                cubeBuilder.setCustomUv(Direction.UP, x, 0, y, x);
                cubeBuilder.setCustomUv(Direction.DOWN, x, 0, y, x);
                cubeBuilder.setCustomUv(Direction.EAST, 0, x, x, y);
                cubeBuilder.setCustomUv(Direction.WEST, 0, x, x, y);
            }
        }
    }

    private static void addCoveredCableSizedCube(Direction facing, CubeBuilder cubeBuilder) {
        switch (facing) {
            case DOWN -> cubeBuilder.addCube(6, 0, 6, 10, 5, 10);
            case EAST -> cubeBuilder.addCube(11, 6, 6, 16, 10, 10);
            case NORTH -> cubeBuilder.addCube(6, 6, 0, 10, 10, 5);
            case SOUTH -> cubeBuilder.addCube(6, 6, 11, 10, 10, 16);
            case UP -> cubeBuilder.addCube(6, 11, 6, 10, 16, 10);
            case WEST -> cubeBuilder.addCube(0, 6, 6, 5, 10, 10);
        }
    }

    private static void addStraightCoveredCableSizedCube(Direction facing, CubeBuilder cubeBuilder) {
        switch (facing) {
            case DOWN, UP -> {
                cubeBuilder.setUvRotation(Direction.EAST, 2);
                cubeBuilder.addCube(5, 0, 5, 11, 16, 11);
                cubeBuilder.setUvRotation(Direction.EAST, 0);
            }
            case EAST, WEST -> {
                cubeBuilder.setUvRotation(Direction.SOUTH, 2);
                cubeBuilder.setUvRotation(Direction.NORTH, 2);
                cubeBuilder.addCube(0, 5, 5, 16, 11, 11);
                cubeBuilder.setUvRotation(Direction.SOUTH, 0);
                cubeBuilder.setUvRotation(Direction.NORTH, 0);
            }
            case NORTH, SOUTH -> {
                cubeBuilder.setUvRotation(Direction.EAST, 2);
                cubeBuilder.setUvRotation(Direction.WEST, 2);
                cubeBuilder.addCube(5, 5, 0, 11, 11, 16);
                cubeBuilder.setUvRotation(Direction.EAST, 0);
                cubeBuilder.setUvRotation(Direction.WEST, 0);
            }
        }
    }

    private static void addCoveredCableSizedCube(Direction facing, int distanceFromEdge, CubeBuilder cubeBuilder) {
        switch (facing) {
            case DOWN -> cubeBuilder.addCube(6, distanceFromEdge, 6, 10, 5, 10);
            case EAST -> cubeBuilder.addCube(11, 6, 6, 16 - distanceFromEdge, 10, 10);
            case NORTH -> cubeBuilder.addCube(6, 6, distanceFromEdge, 10, 10, 5);
            case SOUTH -> cubeBuilder.addCube(6, 6, 11, 10, 10, 16 - distanceFromEdge);
            case UP -> cubeBuilder.addCube(6, 11, 6, 10, 16 - distanceFromEdge, 10);
            case WEST -> cubeBuilder.addCube(distanceFromEdge, 6, 6, 5, 10, 10);
        }
    }

    private static void addBigCoveredCableSizedCube(Direction facing, CubeBuilder cubeBuilder) {
        switch (facing) {
            case DOWN -> cubeBuilder.addCube(5, 0, 5, 11, 4, 11);
            case EAST -> cubeBuilder.addCube(12, 5, 5, 16, 11, 11);
            case NORTH -> cubeBuilder.addCube(5, 5, 0, 11, 11, 4);
            case SOUTH -> cubeBuilder.addCube(5, 5, 12, 11, 11, 16);
            case UP -> cubeBuilder.addCube(5, 12, 5, 11, 16, 11);
            case WEST -> cubeBuilder.addCube(0, 5, 5, 4, 11, 11);
        }
    }
}
