package org.bukkit.block;

import org.bukkit.DyeColor;
import org.bukkit.block.sign.Side;
import org.bukkit.block.sign.SignSide;
import org.bukkit.material.Colorable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a captured state of either a SignPost or a WallSign.
 */
public interface Sign extends TileState, Colorable {

    /**
     * Gets all the lines of text currently on the {@link Side#FRONT} of this sign.
     *
     * @return Array of Strings containing each line of text
     * @see #getSide(Side)
     */
    @NotNull
    public String[] getLines();

    /**
     * Gets the line of text at the specified index.
     * <p>
     * For example, getLine(0) will return the first line of text on the {@link Side#FRONT}.
     *
     * @param index Line number to get the text from, starting at 0
     * @return Text on the given line
     * @throws IndexOutOfBoundsException Thrown when the line does not exist
     * @see #getSide(Side)
     */
    @NotNull
    public String getLine(int index) throws IndexOutOfBoundsException;

    /**
     * Sets the line of text at the specified index.
     * <p>
     * For example, setLine(0, "Line One") will set the first line of text to
     * "Line One".
     *
     * @param index Line number to set the text at, starting from 0
     * @param line New text to set at the specified index
     * @throws IndexOutOfBoundsException If the index is out of the range 0..3
     * @see #getSide(Side)
     */
    public void setLine(int index, @NotNull String line) throws IndexOutOfBoundsException;

    /**
     * Marks whether this sign can be edited by players.
     * <br>
     * This is a special value, which is not persisted. It should only be set if
     * a placed sign is manipulated during the BlockPlaceEvent. Behaviour
     * outside of this event is undefined.
     *
     * @return if this sign is currently editable
     */
    public boolean isEditable();

    /**
     * Marks whether this sign can be edited by players.
     * <br>
     * This is a special value, which is not persisted. It should only be set if
     * a placed sign is manipulated during the BlockPlaceEvent. Behaviour
     * outside of this event is undefined.
     *
     * @param editable if this sign is currently editable
     */
    public void setEditable(boolean editable);

    /**
     * Gets whether this sign has glowing text. Only affects the {@link Side#FRONT}.
     *
     * @return if this sign has glowing text
     * @see #getSide(Side)
     */
    public boolean isGlowingText();

    /**
     * Sets whether this sign has glowing text. Only affects the {@link Side#FRONT}.
     *
     * @param glowing if this sign has glowing text
     * @see #getSide(Side)
     */
    public void setGlowingText(boolean glowing);

    /**
     * {@inheritDoc}
     *
     * @see #getSide(Side)
     */
    @NotNull
    public DyeColor getColor();

    /**
     * {@inheritDoc}
     *
     * @see #getSide(Side)
     */
    public void setColor(@NotNull DyeColor color);

    /**
     * Return the side of the sign.
     *
     * @param side the side of the sign
     * @return the selected side of the sign
     */
    @Experimental
    @NotNull
    public SignSide getSide(@NotNull Side side);
}
