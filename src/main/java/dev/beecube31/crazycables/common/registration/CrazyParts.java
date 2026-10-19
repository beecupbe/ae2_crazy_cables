package dev.beecube31.crazycables.common.registration;

import appeng.api.util.AEColor;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableItem;
import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;
import dev.beecube31.crazycables.core.CrazyAE2Cables;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

import javax.annotation.Nullable;
import java.util.*;

public final class CrazyParts {
    private CrazyParts() {}

    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(CrazyAE2Cables.MODID);


    static final Map<CrazyCableType, EnumMap<AEColor, DeferredItem<CrazyCableItem>>> CABLES = new EnumMap<>(
            CrazyCableType.class);
    static final List<DeferredItem<CrazyCableItem>> ALL = new ArrayList<>();

    static {
        for (CrazyCableType type : CrazyCableType.values()) {
            EnumMap<AEColor, DeferredItem<CrazyCableItem>> byColor = new EnumMap<>(AEColor.class);
            for (AEColor color : AEColor.values()) {
                String id = color.registryPrefix + "_" + type.getIdSuffix();
                DeferredItem<CrazyCableItem> obj = ITEMS.registerItem(id,
                        properties -> new CrazyCableItem(properties, type, color));
                byColor.put(color, obj);
                ALL.add(obj);
            }
            CABLES.put(type, byColor);
        }
    }

    public static void register(IEventBus modBus) {
        ITEMS.register(modBus);
    }

    @Nullable
    public static CrazyCableItem cable(CrazyCableType type, AEColor color) {
        var byColor = CABLES.get(type);
        if (byColor == null) {
            return null;
        }
        var obj = byColor.get(color);
        return obj != null ? obj.get() : null;
    }

    public static List<DeferredItem<CrazyCableItem>> allCables() {
        return Collections.unmodifiableList(ALL);
    }
}
