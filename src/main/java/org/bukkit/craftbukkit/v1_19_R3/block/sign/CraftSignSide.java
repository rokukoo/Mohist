package org.bukkit.craftbukkit.v1_19_R3.block.sign;

import net.minecraft.world.level.block.entity.SignBlockEntity;
import org.bukkit.DyeColor;
import org.bukkit.block.sign.SignSide;
import org.bukkit.craftbukkit.v1_19_R3.block.CraftSign;
import org.bukkit.craftbukkit.v1_19_R3.util.CraftChatMessage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CraftSignSide implements SignSide {

    // Lazily initialized only if requested:
    private String[] originalLines = null;
    private String[] lines = null;
    private final SignBlockEntity signText;

    public CraftSignSide(SignBlockEntity signText) {
        this.signText = signText;
    }

    @NotNull
    @Override
    public String[] getLines() {
        if (lines == null) {
            // Lazy initialization:
            lines = new String[signText.messages.length];
            System.arraycopy(CraftSign.revertComponents(signText.messages), 0, lines, 0, lines.length);
            originalLines = new String[lines.length];
            System.arraycopy(lines, 0, originalLines, 0, originalLines.length);
        }
        return lines;
    }

    @NotNull
    @Override
    public String getLine(int index) throws IndexOutOfBoundsException {
        return getLines()[index];
    }

    @Override
    public void setLine(int index, @NotNull String line) throws IndexOutOfBoundsException {
        getLines()[index] = line;
    }

    @Override
    public boolean isGlowingText() {
        return signText.hasGlowingText();
    }

    @Override
    public void setGlowingText(boolean glowing) {
        signText.setHasGlowingText(glowing);
    }

    @Nullable
    @Override
    public DyeColor getColor() {
        return DyeColor.getByWoolData((byte) signText.getColor().getId());
    }

    @Override
    public void setColor(@NotNull DyeColor color) {
        signText.setColor(net.minecraft.world.item.DyeColor.byId(color.getWoolData()));
    }

    public void applyLegacyStringToSignSide() {
        if (lines != null) {
            for (int i = 0; i < lines.length; i++) {
                String line = (lines[i] == null) ? "" : lines[i];
                if (line.equals(originalLines[i])) {
                    continue; // The line contents are still the same, skip.
                }
                signText.setMessage(i, CraftChatMessage.fromString(line)[0]);
            }
        }
    }
}
