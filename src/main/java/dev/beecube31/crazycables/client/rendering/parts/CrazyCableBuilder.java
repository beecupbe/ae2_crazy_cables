package dev.beecube31.crazycables.client.rendering.parts;

import appeng.api.util.AEColor;
import appeng.client.render.CubeBuilder;
import appeng.client.render.cablebus.SmartCableTextures;
import com.mojang.math.Quadrant;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import dev.beecube31.crazycables.core.CrazyAE2Cables;
import net.minecraft.client.resources.model.ModelDebugName;
import net.minecraft.client.resources.model.geometry.BakedQuad;
import net.minecraft.client.resources.model.sprite.Material;
import net.minecraft.client.resources.model.sprite.MaterialBaker;
import net.minecraft.core.Direction;
import net.minecraft.resources.Identifier;

import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Locale;
import java.util.function.Consumer;

public class CrazyCableBuilder {
    private final EnumMap<CrazyCableType, EnumMap<AEColor, Material.Baked>> connectionTextures = new EnumMap<>(
            CrazyCableType.class);
    private final EnumMap<CrazyCableType, EnumMap<AEColor, Material.Baked>> coreTextures = new EnumMap<>(
            CrazyCableType.class);
    /**
     * Emissive overlay per cable tier (part/cable/emissive/<tier>), rendered full-bright on top of the base texture.
     */
    private final EnumMap<CrazyCableType, Material.Baked> emissiveTextures = new EnumMap<>(CrazyCableType.class);
    private final SmartCableTextures smartCableTextures;

    public CrazyCableBuilder(MaterialBaker materials) {
        ModelDebugName debugName = getClass()::toString;
        for (CrazyCableType type : CrazyCableType.values()) {
            EnumMap<AEColor, Material.Baked> byColor = new EnumMap<>(AEColor.class);
            for (AEColor color : AEColor.values()) {
                byColor.put(color, materials.get(getConnectionTexture(type, color), debugName));
            }
            this.coreTextures.put(type, byColor);
            this.connectionTextures.put(type, byColor);
            this.emissiveTextures.put(type, materials.get(getEmissiveTexture(type), debugName));
        }
        this.smartCableTextures = new SmartCableTextures(materials);
    }

    private static String colorName(AEColor color) {
        return color.name().toLowerCase(Locale.ROOT);
    }

    public static Material getConnectionTexture(CrazyCableType type, AEColor color) {
        return new Material(Identifier.fromNamespaceAndPath(CrazyAE2Cables.MODID,
                "part/cable/" + type.getTextureFolder() + "/" + colorName(color)));
    }

    public static Material getCoreTextureMaterial(CrazyCableType type, AEColor color) {
        return new Material(Identifier.fromNamespaceAndPath(CrazyAE2Cables.MODID,
                "part/cable/core/" + type.getTextureFolder() + "/" + colorName(color)));
    }

    public static Material getEmissiveTexture(CrazyCableType type) {
        return new Material(Identifier.fromNamespaceAndPath(CrazyAE2Cables.MODID,
                "part/cable/emissive/" + type.getTextureFolder()));
    }

    public Material.Baked getEmissiveSprite(CrazyCableType type) {
        return this.emissiveTextures.get(type);
    }

    public Material.Baked getCoreTexture(CrazyCableType type, AEColor color) {
        return this.coreTextures.get(type).get(color);
    }

    private Material.Baked getTexture(CrazyCableType type, AEColor color) {
        return this.connectionTextures.get(type).get(color);
    }

    public void addCableCore(CrazyCableType type, AEColor color, Consumer<BakedQuad> quadsOut) {
        QuadSink sink = new QuadSink(quadsOut);
        CubeBuilder cubeBuilder = new CubeBuilder(sink);
        cubeBuilder.setTexture(this.coreTextures.get(type).get(color));
        cubeBuilder.addCube(5, 5, 5, 11, 11, 11);

        addEmissiveLayer(cubeBuilder, sink, type, () -> cubeBuilder.addCube(5, 5, 5, 11, 11, 11));
    }

    public void addCoveredConnection(Direction facing, CrazyCableType type, AEColor cableColor,
                                     boolean cableBusAdjacent, Consumer<BakedQuad> quadsOut) {
        QuadSink sink = new QuadSink(quadsOut);
        CubeBuilder cubeBuilder = new CubeBuilder(sink);
        cubeBuilder.setDrawFaces(EnumSet.complementOf(EnumSet.of(facing)));
        cubeBuilder.setTexture(this.coreTextures.get(type).get(cableColor));

        if (!cableBusAdjacent) {
            addBigCoveredCableSizedCube(facing, cubeBuilder);
        }

        addCoveredCableSizedCube(facing, cubeBuilder);

        addEmissiveLayer(cubeBuilder, sink, type, () -> {
            if (!cableBusAdjacent) {
                addBigCoveredCableSizedCube(facing, cubeBuilder);
            }
            addCoveredCableSizedCube(facing, cubeBuilder);
        });
    }

    public void addStraightCoveredConnection(CrazyCableType type, Direction facing, AEColor cableColor,
                                             Consumer<BakedQuad> quadsOut) {
        QuadSink sink = new QuadSink(quadsOut);
        CubeBuilder cubeBuilder = new CubeBuilder(sink);

        cubeBuilder.setTexture(getTexture(type, cableColor));

        setStraightCableUVs(cubeBuilder, facing, 5 / 16f, 11 / 16f);

        addStraightCoveredCableSizedCube(facing, cubeBuilder);

        // Custom UVs stay set on the builder, so the overlay lines up with the base texture
        addEmissiveLayer(cubeBuilder, sink, type, () -> addStraightCoveredCableSizedCube(facing, cubeBuilder));
    }

    public void addConstrainedCoveredConnection(CrazyCableType type, Direction facing, AEColor cableColor,
                                                int distanceFromEdge, Consumer<BakedQuad> quadsOut) {
        if (distanceFromEdge >= 5) {
            return;
        }

        QuadSink sink = new QuadSink(quadsOut);
        CubeBuilder cubeBuilder = new CubeBuilder(sink);

        cubeBuilder.setTexture(getTexture(type, cableColor));

        addCoveredCableSizedCube(facing, distanceFromEdge, cubeBuilder);

        addEmissiveLayer(cubeBuilder, sink, type,
                () -> addCoveredCableSizedCube(facing, distanceFromEdge, cubeBuilder));
    }

    /**
     * Draws the same geometry again with the tier's emissive texture. The quads get full light emission
     * (via AE2's emissive material) and are rebuilt without diffuse shading and ambient occlusion, so the line glows
     * evenly on every side of the cable, day and night.
     */
    private void addEmissiveLayer(CubeBuilder cubeBuilder, QuadSink sink, CrazyCableType type, Runnable geometry) {
        cubeBuilder.setTexture(this.emissiveTextures.get(type));
        cubeBuilder.setEmissiveMaterial(true);
        sink.emissive = true;
        geometry.run();
        sink.emissive = false;
        cubeBuilder.setEmissiveMaterial(false);
    }

    /**
     * Forwards quads to the output; while {@link #emissive} is set, quads are rebuilt with shade and ambient
     * occlusion turned off (the 26.1 equivalent of the old {@code new BakedQuad(..., false, false)}).
     */
    private static final class QuadSink implements Consumer<BakedQuad> {
        private final Consumer<BakedQuad> out;
        boolean emissive;

        QuadSink(Consumer<BakedQuad> out) {
            this.out = out;
        }

        @Override
        public void accept(BakedQuad quad) {
            if (emissive) {
                var info = quad.materialInfo();
                var flatInfo = new BakedQuad.MaterialInfo(info.sprite(), info.layer(), info.itemRenderType(),
                        info.tintIndex(), false, info.lightEmission(), false);
                quad = new BakedQuad(quad.position0(), quad.position1(), quad.position2(), quad.position3(),
                        quad.packedUV0(), quad.packedUV1(), quad.packedUV2(), quad.packedUV3(),
                        quad.direction(), flatInfo, quad.bakedNormals(), quad.bakedColors());
            }
            out.accept(quad);
        }
    }

    private static void setStraightCableUVs(CubeBuilder cubeBuilder, Direction facing, float x, float y) {
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
                cubeBuilder.setUvRotation(Direction.EAST, Quadrant.R180);
                cubeBuilder.addCube(5, 0, 5, 11, 16, 11);
                cubeBuilder.setUvRotation(Direction.EAST, Quadrant.R0);
            }
            case EAST, WEST -> {
                cubeBuilder.setUvRotation(Direction.SOUTH, Quadrant.R180);
                cubeBuilder.setUvRotation(Direction.NORTH, Quadrant.R180);
                cubeBuilder.addCube(0, 5, 5, 16, 11, 11);
                cubeBuilder.setUvRotation(Direction.SOUTH, Quadrant.R0);
                cubeBuilder.setUvRotation(Direction.NORTH, Quadrant.R0);
            }
            case NORTH, SOUTH -> {
                cubeBuilder.setUvRotation(Direction.EAST, Quadrant.R180);
                cubeBuilder.setUvRotation(Direction.WEST, Quadrant.R180);
                cubeBuilder.addCube(5, 5, 0, 11, 11, 16);
                cubeBuilder.setUvRotation(Direction.EAST, Quadrant.R0);
                cubeBuilder.setUvRotation(Direction.WEST, Quadrant.R0);
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
