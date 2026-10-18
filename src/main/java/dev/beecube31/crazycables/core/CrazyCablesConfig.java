package dev.beecube31.crazycables.core;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;

public final class CrazyCablesConfig {
    public static final ForgeConfigSpec SPEC;

    private static final ForgeConfigSpec.IntValue IMPROVED_CABLE_CAPACITY;
    private static final ForgeConfigSpec.IntValue ADVANCED_CABLE_CAPACITY;
    private static final ForgeConfigSpec.IntValue PERFECT_CABLE_CAPACITY;
    private static final ForgeConfigSpec.IntValue QUANTUM_CABLE_CAPACITY;

    private static final ForgeConfigSpec.BooleanValue DEVICES_INHERIT_CABLE_CAPACITY;

    public static int improvedCableMaxCapacity = 32;
    public static int advancedCableMaxCapacity = 512;
    public static int perfectCableMaxCapacity = 2048;
    public static int quantumCableMaxCapacity = 8192;
    public static boolean devicesInheritCableCapacity = true;

    static {
        ForgeConfigSpec.Builder b = new ForgeConfigSpec.Builder();

        b.push("cables");
        IMPROVED_CABLE_CAPACITY = b.comment("Channel capacity for Improved Cable")
                .defineInRange("improvedCableCapacity", 128, 1, Integer.MAX_VALUE);
        ADVANCED_CABLE_CAPACITY = b.comment("Channel capacity for Advanced Cable")
                .defineInRange("advancedCableCapacity", 512, 1, Integer.MAX_VALUE);
        PERFECT_CABLE_CAPACITY = b.comment("Channel capacity for Perfect Cable")
                .defineInRange("perfectCableCapacity", 2048, 1, Integer.MAX_VALUE);
        QUANTUM_CABLE_CAPACITY = b.comment("Channel capacity for Quantum Cable")
                .defineInRange("quantumCableCapacity", 8192, 1, Integer.MAX_VALUE);
        b.pop();

        b.push("connectivity");
        DEVICES_INHERIT_CABLE_CAPACITY = b.comment(
                        "All ME Devices (drives, interfaces, pattern providers, ...) that get their channels from a Crazy cable",
                        "carry as many channels as that cable instead of AE2's 8.",
                        "A chain of devices behind one side of an x128 cable can therefore hold up to 128 devices.",
                        "Devices behind vanilla AE2 cables or the controller keep the vanilla limit.")
                .define("devicesInheritCableCapacity", true);
        b.pop();

        SPEC = b.build();
    }

    private CrazyCablesConfig() {}

    public static void onConfigEvent(ModConfigEvent event) {
        if (event.getConfig().getSpec() != SPEC) {
            return;
        }
        improvedCableMaxCapacity = IMPROVED_CABLE_CAPACITY.get();
        advancedCableMaxCapacity = ADVANCED_CABLE_CAPACITY.get();
        perfectCableMaxCapacity = PERFECT_CABLE_CAPACITY.get();
        quantumCableMaxCapacity = QUANTUM_CABLE_CAPACITY.get();
        devicesInheritCableCapacity = DEVICES_INHERIT_CABLE_CAPACITY.get();
    }
}
