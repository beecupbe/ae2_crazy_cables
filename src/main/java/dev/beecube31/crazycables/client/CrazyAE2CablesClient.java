package dev.beecube31.crazycables.client;

import dev.beecube31.crazycables.core.CrazyAE2Cables;
import dev.beecube31.updchecker.UpdateChecker;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.bus.api.IEventBus;

import appeng.client.render.StaticItemColor;

import dev.beecube31.crazycables.common.parts.cables.CrazyCableItem;
import dev.beecube31.crazycables.common.registration.CrazyParts;

public final class CrazyAE2CablesClient {
    private CrazyAE2CablesClient() {}

    public static void init(IEventBus modBus) {
        modBus.addListener(CrazyAE2CablesClient::registerItemColors);

        new UpdateChecker(CrazyAE2Cables.MODID, "Crazy AE2 Cables",
                "https://raw.githubusercontent.com/beecupbe/ae2_crazy_cables/refs/heads/main/updates.json");
    }

    private static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        for (var obj : CrazyParts.allCables()) {
            CrazyCableItem item = obj.get();
            event.register(new StaticItemColor(item.getColor()), item);
        }
    }
}
