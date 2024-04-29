package net.runelite.client.plugins.microbot.BlastFurnaceNeon.Enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum bar {
    NONE("NONE", 1, "NONE", "NONE", "NONE"),
    BRONZE("Bronze bar", 1, "Copper ore", "Tin ore", "NONE"),
    IRON("Iron bar", 15, "Iron ore", "NONE", "NONE"),
    SILVER("Silver bar", 20, "Silver ore", "NONE", "NONE"),
    STEEL("Steel bar", 30, "Iron ore", "Coal", "NONE"),
    GOLD("Gold bar", 40, "Gold ore", "NONE", "NONE"),
    MITHRIL("Mithril bar", 50, "Mithril ore", "Coal", "Coal"),
    ADAMANTITE("Adamantite bar", 70, "Adamantite ore", "Coal", "NONE"),
    RUNITE("Runite bar", 85, "Runite ore", "Coal", "NONE");

    private final String name;
    private final int levelRequired;
    private final String materialOne;
    private final String materialTwo;
    private final String materialThree;

    @Override
    public String toString()
    {
        return name;
    }
}

