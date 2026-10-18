package dev.beecube31.crazycables.common.interfaces.mixin.render;

import javax.annotation.Nullable;

import dev.beecube31.crazycables.common.parts.cables.CrazyCableType;

public interface ICrazyCableBusRenderState {
    @Nullable
    CrazyCableType crazyae$getCrazyCableType();

    void crazyae$setCrazyCableType(@Nullable CrazyCableType type);
}
