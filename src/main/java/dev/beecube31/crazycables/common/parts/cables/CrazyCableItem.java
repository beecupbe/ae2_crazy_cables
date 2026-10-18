package dev.beecube31.crazycables.common.parts.cables;

import appeng.api.util.AEColor;
import appeng.items.parts.ColoredPartItem;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;

public class CrazyCableItem extends ColoredPartItem<CrazyCablePart> {
    private final CrazyCableType cableType;

    public CrazyCableItem(Properties properties, CrazyCableType cableType, AEColor color) {
        super(properties, CrazyCablePart.class, item -> new CrazyCablePart(item, cableType), color);
        this.cableType = cableType;
    }

    public CrazyCableType getCableType() {
        return cableType;
    }

    @Override
    public void appendHoverText(@NotNull ItemStack stack, @NotNull TooltipContext context, @NotNull List<Component> lines,
                                @NotNull TooltipFlag advancedTooltips) {
        super.appendHoverText(stack, context, lines, advancedTooltips);
        lines.add(Component.translatable("tooltip.crazy_ae2_cables.cable_channels", cableType.getDisplayChannels())
                .withStyle(ChatFormatting.GRAY));
    }
}
