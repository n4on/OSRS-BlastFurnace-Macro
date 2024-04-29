package net.runelite.client.plugins.microbot.BlastFurnaceNeon;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.widgets.ComponentID;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.ui.overlay.OverlayManager;
import net.runelite.client.util.Text;

import javax.inject.Inject;
import java.awt.*;
import java.time.Duration;
import java.time.Instant;

@PluginDescriptor(
        name = PluginDescriptor.Default + "BlastFurnaceNeon",
        description = "Blast furnace script",
        tags = {"BlastFurnace", "microbot"},
        enabledByDefault = false
)
@Slf4j
public class BlastFurnaceNeonPlugin extends Plugin {
    private static final String FOREMAN_PERMISSION_TEXT = "Okay, you can use the furnace for ten minutes. Remember, you only need half as much coal as with a regular furnace.";
    public static int expStarted;
    public static int startingLevel;
    public static long timeBegan;
    public static int barCount;

    @Inject
    private BlastFurnaceNeonConfig config;
    @Provides
    BlastFurnaceNeonConfig provideConfig(ConfigManager configManager) {
        return configManager.getConfig(BlastFurnaceNeonConfig.class);
    }

    public static ForemanTimerNeon foremanTimerNeon;

    @Inject
    private OverlayManager overlayManager;
    @Inject
    private BlastFurnaceNeonOverlay BlastFurnaceNeonOverlay;

    @Inject
    BlastFurnaceNeonScript BlastFurnaceNeonScript;

    @Inject
    private Client client;

    @Inject
    private ItemManager itemManager;


    @Override
    protected void startUp() throws AWTException {
        if (overlayManager != null) {
            overlayManager.add(BlastFurnaceNeonOverlay);
        }
        BlastFurnaceNeonScript.run(config);

        expStarted = Microbot.getClient().getSkillExperience(Skill.SMITHING);
        startingLevel = Microbot.getClient().getRealSkillLevel(Skill.SMITHING);
        timeBegan = System.currentTimeMillis();
        barCount = 0;
    }

    protected void shutDown() {
        BlastFurnaceNeonScript.shutdown();
        overlayManager.remove(BlastFurnaceNeonOverlay);
        foremanTimerNeon = null;
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        Widget npcDialog = client.getWidget(ComponentID.DIALOG_NPC_TEXT);
        if (npcDialog == null)
        {
            return;
        }

        boolean shouldCheckForemanFee = client.getRealSkillLevel(Skill.SMITHING) < 60
                && (foremanTimerNeon == null || Duration.between(Instant.now(), foremanTimerNeon.getEndTime()).toMinutes() <= 5);

        if (shouldCheckForemanFee)
        {
            String npcText = Text.sanitizeMultilineText(npcDialog.getText());

            if (npcText.equals(FOREMAN_PERMISSION_TEXT))
            {
                foremanTimerNeon = new ForemanTimerNeon(this, itemManager);
            }
        }
    }
}
