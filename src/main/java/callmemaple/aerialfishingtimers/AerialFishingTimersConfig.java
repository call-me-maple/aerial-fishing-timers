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
	@ConfigItem(
			keyName = "tickIncrement",
			name = "Increment by Tick",
			description = "This will have the circle increment every game tick instead of continuously",
			position = 1
	)
	default boolean getTickIncrement()
	{
		return false;
	}

	@ConfigItem(
			keyName = "drawExpirationLine",
			name = "Draw Expiration Line",
			description = "This will add a dotted line on each circle to indicate when a fishing spot could despawn",
			position = 2
	)
	default boolean getDrawExpirationLine()
	{
		return false;
	}

	@Range(max=50, min=10)
	@ConfigItem(
		keyName = "circleSize",
		name = "Circle Size",
		description = "How big to make the circular timers",
		position = 3
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
			position = 4
	)
	default int getWarningThreshold()
	{
		return 0;
	}

	@ConfigSection(
			name = "Colors",
			description = "",
			position = 5
	) String colors = "colors";

	@ConfigItem(
			keyName = "availableColor",
			name = "Available Color",
			description = "Color of the circle when the spot cannot expire",
			position = 6,
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
			position = 7,
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
			position = 8,
			section = colors
	)
	default Color getExpiringColor()
	{
		return new Color(255, 150, 30);
	}
}
