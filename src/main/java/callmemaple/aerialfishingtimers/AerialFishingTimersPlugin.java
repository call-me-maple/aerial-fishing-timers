package callmemaple.aerialfishingtimers;

import com.google.inject.Provides;
import javax.inject.Inject;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.FishingSpot;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import static net.runelite.client.util.RSTimeUnit.GAME_TICKS;

@Slf4j
@PluginDescriptor(
	name = "Aerial Fishing Timers",
	description = "Adds timers to indicate when aerial fishing spots will expire",
	tags = {"fishing","aerial","timer","despawn"}
)
public class AerialFishingTimersPlugin extends Plugin
{
	public static final int SPOT_MAX_SPAWN_TICKS = 19;
	public static final int SPOT_MIN_SPAWN_TICKS = 10;
	public static final Duration SPOT_MAX_SPAWN_DURATION = Duration.of(SPOT_MAX_SPAWN_TICKS, GAME_TICKS);
	public static final float SPOT_RANDOM_PERCENT_THRESHOLD = (float) SPOT_MIN_SPAWN_TICKS / SPOT_MAX_SPAWN_TICKS;

	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private AerialFishingTimersConfig config;

	@Inject
	private AerialFishingTimersOverlay overlay;

	@Getter(AccessLevel.PACKAGE)
	private final Map<NPC, AerialFishingSpot> activeFishingSpots = new HashMap<>();

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		activeFishingSpots.clear();
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onNpcSpawned(NpcSpawned event)
	{
		final NPC npc = event.getNpc();

		// Check if the npc spawn is a fishing spot
		if (FishingSpot.findSpot(npc.getId()) == null)
		{
			return;
		}
		// Store the npc and the time spawned
		activeFishingSpots.put(npc, new AerialFishingSpot(npc, Instant.now()));
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned npcDespawned)
	{
		final NPC spot = npcDespawned.getNpc();
		if (!activeFishingSpots.containsKey(spot))
		{
			return;
		}
		// Remove any despawned fishing spots
		activeFishingSpots.remove(spot);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Remove any stored fishing spots if they should be expired by now
		Instant now = Instant.now();
		activeFishingSpots.values().removeIf(spot -> Duration.between(now, spot.getSpawnTime()).compareTo(SPOT_MAX_SPAWN_DURATION) > 0);
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		switch (event.getGameState())
		{
			case HOPPING:
			case LOGGING_IN:
				activeFishingSpots.clear();
				break;
			default:
				break;
		}
	}

	@Provides
	AerialFishingTimersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AerialFishingTimersConfig.class);
	}
}
