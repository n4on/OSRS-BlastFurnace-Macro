package net.runelite.client.plugins.microbot.BlastFurnaceNeon;

import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.client.plugins.microbot.Microbot;
import net.runelite.client.plugins.natepainthelper.PaintFormat;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

import javax.inject.Inject;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.runelite.client.plugins.natepainthelper.Info.*;
import static net.runelite.client.plugins.natepainthelper.Info.xpTillNextLevel;

public class BlastFurnaceNeonOverlay extends OverlayPanel {
    @Inject
    BlastFurnaceNeonOverlay(BlastFurnaceNeonPlugin plugin)
    {
        super(plugin);
        setPosition(OverlayPosition.TOP_LEFT);
        setNaughty();
    }
    @Override
    public Dimension render(Graphics2D graphics) {
        try {
            long xpGained = Microbot.getClient().getSkillExperience(Skill.SMITHING) - BlastFurnaceNeonPlugin.expStarted;
            int xpPerHour = (int)( xpGained / ((System.currentTimeMillis() - BlastFurnaceNeonPlugin.timeBegan) / 3600000.0D));
            int copper = 44;
            int tin = 47;
            int bronzeBar = 161;

            int totalMaterialCost = (copper + tin) * BlastFurnaceNeonPlugin.barCount;
            int profit = bronzeBar - totalMaterialCost;


            panelComponent.setPreferredSize(new Dimension(200, 300));
            panelComponent.getChildren().add(TitleComponent.builder()
                    .text("BlastFurnaceNeon V" + BlastFurnaceNeonScript.version + " " + LocalDateTime.now(ZoneId.of("Europe/London")).format(DateTimeFormatter.ofPattern("HH:mm:ss")))
                    .color(Color.GREEN)
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Smithing Exp Gained (hr): " + (xpGained)  + " ("+xpPerHour+")")
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Smithing Levels Gained: " + (Microbot.getClient().getRealSkillLevel(Skill.SMITHING) - BlastFurnaceNeonPlugin.startingLevel))
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left(BlastFurnaceNeonScript.barName + "'s made: " + BlastFurnaceNeonPlugin.barCount)
                    .build());
            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Profit (no losses) = " + profit)
                    .build());


            panelComponent.getChildren().add(LineComponent.builder().build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("Coffer: " + Microbot.getVarbitValue(Varbits.BLAST_FURNACE_COFFER))
                    .build());

            panelComponent.getChildren().add(LineComponent.builder()
                    .left("State: " + BlastFurnaceNeonScript.state)
                    .left("Foreman time left: " + BlastFurnaceNeonPlugin.foremanTimerNeon.getEndTime().plusSeconds(3600).toString().replaceAll("^.*T", "").replaceAll("\\..*$", ""))
                    .build());

        } catch(Exception ex) {
            //System.out.println(ex.getMessage());
        }
        return super.render(graphics);
    }
}
