package dev.beecube31.crazycables.common.parts.cables;

import dev.beecube31.crazycables.common.me.CrazyGridFlags;

public enum CrazyCableType {
    X128("cable_x128", "x128", CrazyGridFlags.CABLE_X128, "128"),
    X512("cable_x512", "x512", CrazyGridFlags.CABLE_X512, "512"),
    X2048("cable_x2048", "x2048", CrazyGridFlags.CABLE_X2048, "2048"),
    X8192("cable_x8192", "x8192", CrazyGridFlags.CABLE_X8192, "8192"),
    CREATIVE("cable_creative", "creative", CrazyGridFlags.CABLE_CREATIVE, "∞");

    private final String idSuffix;
    private final String textureFolder;
    private final CrazyGridFlags gridFlag;
    private final String displayChannels;

    CrazyCableType(String idSuffix, String textureFolder, CrazyGridFlags gridFlag, String displayChannels) {
        this.idSuffix = idSuffix;
        this.textureFolder = textureFolder;
        this.gridFlag = gridFlag;
        this.displayChannels = displayChannels;
    }

    public String getIdSuffix() {
        return idSuffix;
    }

    public String getTextureFolder() {
        return textureFolder;
    }

    public CrazyGridFlags getGridFlag() {
        return gridFlag;
    }

    public int getChannelCapacity() {
        return gridFlag.getChannelCapacity();
    }

    public String getDisplayChannels() {
        return displayChannels;
    }

    public boolean isCreative() {
        return this == CREATIVE;
    }
}
