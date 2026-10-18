package dev.beecube31.crazycables.core;

import dev.beecube31.crazycables.common.registration.CrazyCreativeTab;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import dev.beecube31.crazycables.client.CrazyAE2CablesClient;
import dev.beecube31.crazycables.common.registration.CrazyParts;

@Mod(CrazyAE2Cables.MODID)
public class CrazyAE2Cables {
    public static final String MODID = "crazy_ae2_cables";
    public static final Logger LOGGER = LoggerFactory.getLogger("CrazyAE2Cables");

    public CrazyAE2Cables() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CrazyCablesConfig.SPEC);
        modBus.addListener(CrazyCablesConfig::onConfigEvent);

        CrazyParts.register(modBus);
        CrazyCreativeTab.register(modBus);

        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> CrazyAE2CablesClient.init(modBus));
    }
}
