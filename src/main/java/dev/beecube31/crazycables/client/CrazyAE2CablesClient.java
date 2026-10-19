package dev.beecube31.crazycables.client;

import dev.beecube31.crazycables.core.CrazyAE2Cables;
import dev.beecube31.updchecker.UpdateChecker;
import net.neoforged.bus.api.IEventBus;

public final class CrazyAE2CablesClient {
    private CrazyAE2CablesClient() {}

    public static void init(IEventBus modBus) {
        // Item colors (formerly StaticItemColor via RegisterColorHandlersEvent.Item) are now declared as
        // "ae2:color" tint sources in assets/crazy_ae2_cables/items/*.json, the same way AE2 26.1 does it.

        new UpdateChecker(CrazyAE2Cables.MODID, "Crazy AE2 Cables",
                "https://raw.githubusercontent.com/beecupbe/ae2_crazy_cables/refs/heads/main/updates.json");
    }
}
