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
	@Range(max=50)
	@ConfigItem(
		keyName = "circleSize",
		name = "Circle Size",
		description = "How big to make the circular timers",
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
			description = "Adds a warning phase X ticks before the final RNG expiration phase (0 to turn off)",
			position = 2
	)
	default int getWarningThreshold()
	{
		return 0;
	}

	@ConfigSection(
			name = "Colors",
			description = "",
			position = 3
	) String colors = "colors";

	@ConfigItem(
			keyName = "availableColor",
			name = "Available Color",
			description = "Color of the circle when the spot cannot expire",
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
			description = "Color of the circle right before the RNG expiration phase",
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
			description = "Color of the circle when the spot may expire at any moment",
			position = 6,
			section = colors
	)
	default Color getExpiringColor()
	{
		return new Color(255, 150, 30);
	}
}
