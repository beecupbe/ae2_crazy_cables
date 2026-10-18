package dev.beecube31.crazycables.common.registration;

import appeng.api.util.AEColor;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableItem;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import dev.beecube31.crazycables.core.CrazyAE2Cables;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import static dev.beecube31.crazycables.common.registration.CrazyParts.ALL;
import static dev.beecube31.crazycables.common.registration.CrazyParts.cable;

public final class CrazyCreativeTab {
    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
            CrazyAE2Cables.MODID);

    private CrazyCreativeTab() {}

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> TAB = TABS.register("main", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.crazy_ae2_cables"))
            .icon(() -> new ItemStack(cable(CrazyCableType.X8192, AEColor.TRANSPARENT)))
            .displayItems((params, output) -> {
                for (DeferredItem<CrazyCableItem> item : ALL) {
                    output.accept(item.get());
                }
            })
            .build());

    public static void register(IEventBus modBus) {
        TABS.register(modBus);
    }
}
