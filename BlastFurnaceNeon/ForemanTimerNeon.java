package net.runelite.client.plugins.microbot.BlastFurnaceNeon;

import net.runelite.api.ItemID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.Timer;

import java.time.temporal.ChronoUnit;

class ForemanTimerNeon extends Timer
{
	private static final String TOOLTIP_TEXT = "Foreman Fee";

	ForemanTimerNeon(BlastFurnaceNeonPlugin plugin, ItemManager itemManager)
	{
		super(10, ChronoUnit.MINUTES, itemManager.getImage(ItemID.COAL_BAG), plugin);

		setTooltip(TOOLTIP_TEXT);
	}
}