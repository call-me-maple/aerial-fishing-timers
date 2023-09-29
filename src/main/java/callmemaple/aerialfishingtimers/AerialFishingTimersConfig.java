package callmemaple.aerialfishingtimers;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Range;

import java.awt.Color;

@ConfigGroup("aerialfishingtimers")
public interface AerialFishingTimersConfig extends Config
{
	@Range(max=100)
	@ConfigItem(
		keyName = "circleSize",
		name = "Circle Size",
		description = "How big to make the overlay's circle indicators",
		position = 1
	)
	default int getCircleSize()
	{
		return 22;
	}

	@Range(max=AerialFishingTimersPlugin.SPOT_MIN_SPAWN_TICKS-1)
	@ConfigItem(
			keyName = "warningThreshold",
			name = "Warning Threshold",
			description = "How many ticks before the RNG portion should the circle turn the warning color (0 for no warning)",
			position = 2
	)
	default int getWarningThreshold()
	{
		return 4;
	}

	@ConfigSection(
			name = "Colors",
			description = "",
			position = 3,
			closedByDefault = true
	) String colors = "colors";

	@ConfigItem(
			keyName = "availableColor",
			name = "Available Color",
			description = "What color should the circle be when the spot cannot expire",
			position = 4,
			section = colors
	)
	default Color getAvailableColor()
	{
		return Color.GREEN;
	}

	@ConfigItem(
			keyName = "warningColor",
			name = "Warning Color",
			description = "What color should the circle turn to warn before the RNG expiring portion",
			position = 5,
			section = colors
	)
	default Color getWarningColor()
	{
		return Color.YELLOW;
	}

	@ConfigItem(
			keyName = "expiringColor",
			name = "Expiring Color",
			description = "What color should the circle turn when the spot can expire at any time",
			position = 6,
			section = colors
	)
	default Color getExpiringColor()
	{
		return Color.RED;
	}
}
