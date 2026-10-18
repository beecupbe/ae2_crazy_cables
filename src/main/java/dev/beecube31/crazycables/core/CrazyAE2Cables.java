package dev.beecube31.crazycables.core;

import dev.beecube31.crazycables.common.registration.CrazyCreativeTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

import dev.beecube31.crazycables.client.CrazyAE2CablesClient;
import dev.beecube31.crazycables.common.registration.CrazyParts;

@Mod(CrazyAE2Cables.MODID)
public class CrazyAE2Cables {
    public static final String MODID = "crazy_ae2_cables";
    public static final Logger LOGGER = LoggerFactory.getLogger("CrazyAE2Cables");

    public CrazyAE2Cables(IEventBus modBus, ModContainer container, Dist dist) {
        container.registerConfig(ModConfig.Type.COMMON, CrazyCablesConfig.SPEC);
        modBus.addListener(CrazyCablesConfig::onConfigEvent);

        CrazyParts.register(modBus);
        CrazyCreativeTab.register(modBus);

        if (dist.isClient()) {
            CrazyAE2CablesClient.init(modBus);
        }
    }
}
