package net.runelite.client.plugins.microbot.BlastFurnaceNeon;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.plugins.microbot.BlastFurnaceNeon.Enums.states;
import net.runelite.client.plugins.microbot.BlastFurnaceNeon.Enums.bar;

@ConfigGroup("blast")
public interface BlastFurnaceNeonConfig extends Config {
    @ConfigItem(
            keyName = "guide",
            name = "How to use",
            description = "How to use this plugin",
            position = 0,
            section = generalSection
    )
    default String GUIDE() {
        return "Only tested with bronze bar and steel bar, mithril adamantite wont work, other idk. make sure there are no ores in when you start the script.";
    }

    @ConfigSection(
            name = "General",
            description = "General",
            position = 1
    )
    String generalSection = "general";

    @ConfigItem(
            keyName = "State",
            name = "State",
            description = "Choose bot state.",
            position = 0,
            section = generalSection
    )
    default states botState()
    {
        return states.checking;
    }

    @ConfigSection(
            name = "Bar",
            description = "Pick your bar.",
            position = 0
    )
    String configSection = "config";

    @ConfigItem(
            keyName = "Bar",
            name = "Bar",
            description = "Choose bar.",
            position = 0,
            section = configSection
    )
    default bar barType()
    {
        return bar.NONE;
    }

    @ConfigItem(
            keyName = "Coffer",
            name = "Coffer coin",
            description = "Amount of coins to put in Coffer.",
            position = 2,
            section = configSection
    )
    default int CofferCoins() { return 10_000; }
}
