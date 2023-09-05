package com.foo;

import java.awt.Color;

import net.runelite.client.config.*;


@ConfigGroup("easyarceuusrunecrafting")
public interface EasyArceuusRunecraftingConfig extends Config
{

	@ConfigSection(
			name = "Highlight Next Action",
			description = "Highlight the clickbox of the next thing to click.",
			position = 3
	)
	String sectionAction = "action";

	@ConfigSection(
			name = "Idle Screen Tint",
			description = "Tint the whole screen when idle.",
			position = 4
	)
	String sectionIdleTint = "idleTint";
	@ConfigSection(
			name = "Active Screen Tint",
			description = "Tint the whole screen when active.",
			position = 5,
			closedByDefault = true
	)
	String sectionActiveTint = "activeTint";

	@ConfigItem(
			keyName = "whichRunes",
			name = "Enabled",
			description = "Highlight the clickbox of the next thing to click.",
			position = 1
	)
	default RuneType whichRunes()
	{
		return RuneType.BLOOD;
	}

	@ConfigItem(
			keyName = "suggestRun",
			name = "Suggest Enabling Run",
			description = "Highlight the Run Minimap Orb when run is off and energy is over 50.",
			position = 2
	)
	default boolean suggestRun()
	{
		return true;
	}

	@ConfigItem(
			keyName = "actionEnabled",
			name = "Enabled",
			description = "Highlight the clickbox of the next thing to click.",
			section = sectionAction,
			position = 1
	)
	default boolean actionEnabled()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
			keyName = "actionColor",
			name = "Color",
			description = "Color used.",
			section = sectionAction,
			position = 2
	)
	default Color actionColor()
	{
		return new Color(Color.YELLOW.getRed(),Color.YELLOW.getGreen(),Color.YELLOW.getBlue(), 128);
	}
	@ConfigItem(
			keyName = "actionFlash",
			name = "Flashing",
			description = "Flash between two colors.",
			section = sectionAction,
			position = 3
	)
	default boolean actionFlash()
	{
		return true;
	}
	@Alpha
	@ConfigItem(
			keyName = "actionColor2",
			name = "Color 2",
			description = "Second color used when flashing.",
			section = sectionAction,
			position = 4
	)
	default Color actionColor2()
	{
		return new Color(Color.CYAN.getRed(),Color.CYAN.getGreen(),Color.CYAN.getBlue(), 128);
	}
	@Range(min = 1)
	@Units(value = Units.MILLISECONDS)
	@ConfigItem(
			keyName = "actionColor1Time",
			name = "Color 1 Time",
			description = "Milliseconds to show color 1 when flashing.",
			section = sectionAction,
			position = 5
	)
	default int actionColor1Time()
	{
		return 100;
	}
	@Range(min = 1)
	@Units(value = Units.MILLISECONDS)
	@ConfigItem(
			keyName = "actionColor2Time",
			name = "Color 2 Time",
			description = "Milliseconds to show color 2 when flashing.",
			section = sectionAction,
			position = 6
	)
	default int actionColor2Time()
	{
		return 100;
	}
//	@Alpha
//	@ConfigItem( // TODO implement?
//			keyName = "actionHoverColor",
//			name = "Hover Color",
//			description = "Color used for a hovered action's highlight.",
//			section = sectionAction,
//			position = 7
//	)
//	default Color actionHoverColor()
//	{
//		return new Color(Color.GREEN.getRed(),Color.GREEN.getGreen(),Color.GREEN.getBlue(), 128);
//	}
	@ConfigItem(
			keyName = "idleTint",
			name = "Enabled",
			description = "Tint the whole screen when idle.",
			section = sectionIdleTint,
			position = 1
	)
	default boolean idleTint()
	{
		return true;
	}
	@Alpha
	@ConfigItem(
			keyName = "idleTintColor",
			name = "Color",
			description = "Color used.",
			section = sectionIdleTint,
			position = 2
	)
	default Color idleTintColor()
	{
		return new Color(Color.PINK.getRed(),Color.PINK.getGreen(),Color.PINK.getBlue(), 64);
	}
	@ConfigItem(
			keyName = "idleTintFlash",
			name = "Flashing",
			description = "Flash between two colors.",
			section = sectionIdleTint,
			position = 3
	)
	default boolean idleTintFlash()
	{
		return true;
	}
	@Alpha
	@ConfigItem(
			keyName = "idleTintColor2",
			name = "Color 2",
			description = "Second color used when flashing.",
			section = sectionIdleTint,
			position = 4
	)
	default Color idleTintColor2()
	{
		return new Color(Color.BLACK.getRed(),Color.BLACK.getGreen(),Color.BLACK.getBlue(), 64);
	}
	@Range(min = 1)
	@Units(value = Units.MILLISECONDS)
	@ConfigItem(
			keyName = "idleTintColor1Time",
			name = "Color 1 Time",
			description = "Milliseconds to show color 1 when flashing.",
			section = sectionIdleTint,
			position = 5
	)
	default int idleTintColor1Time()
	{
		return 600;
	}
	@Range(min = 1)
	@Units(value = Units.MILLISECONDS)
	@ConfigItem(
			keyName = "idleTintColor2Time",
			name = "Color 2 Time",
			description = "Milliseconds to show color 2 when flashing.",
			section = sectionIdleTint,
			position = 6
	)
	default int idleTintColor2Time()
	{
		return 600;
	}
	@Alpha
	@ConfigItem(
			keyName = "activeTint",
			name = "Enabled",
			description = "Tint the whole screen when active.",
			section = sectionActiveTint,
			position = 1
	)
	default boolean activeTint()
	{
		return false;
	}
	@Alpha
	@ConfigItem(
			keyName = "activeTintColor",
			name = "Color",
			description = "Color used.",
			section = sectionActiveTint,
			position = 2
	)
	default Color activeTintColor()
	{
		return new Color(Color.GREEN.getRed(),Color.GREEN.getGreen(),Color.GREEN.getBlue(), 64);
	}
}
