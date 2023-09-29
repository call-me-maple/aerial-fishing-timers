package callmemaple.aerialfishingtimers;

import net.runelite.api.Client;
import net.runelite.api.InventoryID;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.time.Instant;
import java.util.Map;

import static callmemaple.aerialfishingtimers.AerialFishingTimersPlugin.*;

public class AerialFishingTimersOverlay extends Overlay
{
    private final Client client;
    private final AerialFishingTimersPlugin plugin;
    private final AerialFishingTimersConfig config;

    @Inject
    private AerialFishingTimersOverlay(Client client, AerialFishingTimersPlugin plugin, AerialFishingTimersConfig config)
    {
        setPosition(OverlayPosition.DYNAMIC);
        setLayer(OverlayLayer.ABOVE_SCENE);
        this.plugin = plugin;
        this.client = client;
        this.config = config;
    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        // Don't render anything if the player isn't fishing
        if (!plugin.isPlayingFishing(client.getItemContainer(InventoryID.EQUIPMENT)))
        {
            return null;
        }

        Map<NPC, Instant> spots = plugin.getActiveFishingSpots();
        Instant now = Instant.now();
        float warningThreshold = (float) (SPOT_MIN_SPAWN_TICKS-config.getWarningThreshold()) / SPOT_MAX_SPAWN_TICKS;

        for (NPC spot : spots.keySet())
        {
            LocalPoint location = spot.getLocalLocation();
            if (location == null)
            {
                continue;
            }

            // Calculate the percentage of the time into the fishing spot's availability
            Instant spawnTime = spots.get(spot);
            long maxRespawn = SPOT_MAX_SPAWN_DURATION.toMillis();
            float percentProgress = (now.toEpochMilli() - spawnTime.toEpochMilli()) / (float) maxRespawn;

            // Find where to draw the indicator
            Point point = Perspective.localToCanvas(client, location, client.getPlane());
            if (point == null || percentProgress > 1.0f)
            {
                continue;
            }

            // Pick the corresponding fill color based on the progress
            Color pieFillColor = config.getAvailableColor();
            if (percentProgress > SPOT_RANDOM_PERCENT_THRESHOLD)
            {
                pieFillColor = config.getExpiringColor();
            } else if (percentProgress > warningThreshold)
            {
                pieFillColor = config.getWarningColor();
            }

            ProgressPieComponent ppc = new ProgressPieComponent();
            ppc.setDiameter(config.getCircleSize());
            ppc.setBorderColor(pieFillColor.darker());
            ppc.setFill(pieFillColor);
            ppc.setPosition(point);
            ppc.setProgress(percentProgress);
            ppc.render(graphics);
        }

        return null;
    }
}