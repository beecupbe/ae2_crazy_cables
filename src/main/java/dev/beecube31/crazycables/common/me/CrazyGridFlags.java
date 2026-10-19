package dev.beecube31.crazycables.common.me;

import java.util.function.IntSupplier;

import dev.beecube31.crazycables.core.CrazyCablesConfig;

public enum CrazyGridFlags {
    CABLE_X128(() -> CrazyCablesConfig.improvedCableMaxCapacity),
    CABLE_X512(() -> CrazyCablesConfig.advancedCableMaxCapacity),
    CABLE_X2048(() -> CrazyCablesConfig.perfectCableMaxCapacity),
    CABLE_X8192(() -> CrazyCablesConfig.quantumCableMaxCapacity),
    CABLE_CREATIVE(() -> Integer.MAX_VALUE);

    private final IntSupplier capacity;

    CrazyGridFlags(IntSupplier capacity) {
        this.capacity = capacity;
    }

    public int getChannelCapacity() {
        return this.capacity.getAsInt();
    }
}
