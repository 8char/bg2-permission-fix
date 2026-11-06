package me.atlas.bg2permfix.mixin;

import com.direwolf20.buildinggadgets2.util.BuildingUtils;
import com.direwolf20.buildinggadgets2.util.EventHelpers;
import com.direwolf20.buildinggadgets2.util.datatypes.StatePos;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;

/**
 * Fixes https://github.com/Direwolf20-MC/BuildingGadgets2/issues/214
 *
 * BuildingUtils.build() and exchange() check build permission at the
 * template-relative position (StatePos.pos, near 0,0 for pastes) while the
 * block is actually placed at StatePos.pos.offset(lookingAt). This makes
 * spawn claims/protection at 0,0 block pasting everywhere, and conversely
 * lets pastes bypass claim protection everywhere once 0,0 is unprotected.
 *
 * These redirects re-point the permission checks at the real placement
 * position. For non-paste modes the position list is absolute and lookingAt
 * is BlockPos.ZERO, so the offset is a no-op there.
 */
@Mixin(value = BuildingUtils.class, remap = false)
public abstract class BuildingUtilsMixin {

    @Redirect(
            method = "build",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/direwolf20/buildinggadgets2/util/EventHelpers;mayPlace(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private static boolean bg2permfix$mayPlaceAtWorldPos(
            Level level, Player player, BlockPos relativePos,
            Level level0, Player player0, ArrayList<StatePos> blockPosList, BlockPos lookingAt, ItemStack gadget, boolean needItems) {
        return EventHelpers.mayPlace(level, player, relativePos.offset(lookingAt));
    }

    @Redirect(
            method = "exchange",
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/direwolf20/buildinggadgets2/util/EventHelpers;mayExchange(Lnet/minecraft/world/level/Level;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/core/BlockPos;)Z"
            )
    )
    private static boolean bg2permfix$mayExchangeAtWorldPos(
            Level level, Player player, BlockPos relativePos,
            Level level0, Player player0, ArrayList<StatePos> blockPosList, BlockPos lookingAt, ItemStack gadget, boolean needItems, boolean returnItems) {
        return EventHelpers.mayExchange(level, player, relativePos.offset(lookingAt));
    }
}
