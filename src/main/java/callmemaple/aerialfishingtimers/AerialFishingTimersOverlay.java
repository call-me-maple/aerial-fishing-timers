package callmemaple.aerialfishingtimers;

import net.runelite.api.Client;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.InventoryID;
import net.runelite.api.Item;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.Perspective;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.ProgressPieComponent;

import javax.inject.Inject;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.geom.Line2D;
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
        // Don't render anything if the player isn't wearing the aerial fishing glove
        if (!isWearingGlove())
        {
            return null;
        }

        Map<NPC, AerialFishingSpot> spots = plugin.getActiveFishingSpots();
        Instant now = Instant.now();
        float warningThreshold = (SPOT_MIN_SPAWN_TICKS - config.getWarningThreshold()) / (float) SPOT_MAX_SPAWN_TICKS;

        for (AerialFishingSpot spot : spots.values())
        {
            LocalPoint location = spot.getNpc().getLocalLocation();
            if (location == null)
            {
                continue;
            }

            // Calculate the percentage of the time into the fishing spot's availability
            Instant spawnTime = spot.getSpawnTime();
            long maxRespawn = SPOT_MAX_SPAWN_DURATION.toMillis();
            long sinceSpawn = now.toEpochMilli() - spawnTime.toEpochMilli();
            float percentProgress = (float) sinceSpawn / maxRespawn;

            // If using tick increment calculate progress by tick instead
            if (config.getTickIncrement())
            {
                float millisPerTick = (SPOT_MAX_SPAWN_DURATION.toMillis() / (float) SPOT_MAX_SPAWN_TICKS);
                percentProgress = (float) ((Math.floor(sinceSpawn / millisPerTick)) / SPOT_MAX_SPAWN_TICKS);
            }

            // Find where to draw the indicator
            Point point = Perspective.localToCanvas(client, location, client.getPlane());
            if (point == null || percentProgress > 1.0f)
            {
                continue;
            }

            if (config.getDrawExpirationLine() && percentProgress < SPOT_RANDOM_PERCENT_THRESHOLD)
            {
                drawExpirationLine(graphics, point);
            }

            // Pick the corresponding fill color based on the progress
            Color pieFillColor = config.getAvailableColor();
            if (percentProgress >= SPOT_RANDOM_PERCENT_THRESHOLD)
            {
                pieFillColor = config.getExpiringColor();
            } else if (percentProgress >= warningThreshold)
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

    private void drawExpirationLine(Graphics2D graphics, Point point)
    {
        // Covert to radians and add a 90 degrees offset so the top of the circle is 0 degrees
        double theta = ((SPOT_RANDOM_PERCENT_THRESHOLD * 360) + 90) * Math.PI / 180;
        double xOffset = (config.getCircleSize()/2f) * Math.cos(theta);
        double yOffset = -1 * (config.getCircleSize()/2f) * Math.sin(theta);

        Stroke dashedStroke = new BasicStroke(1.5f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, new float[]{2f, 2f}, 1);
        graphics.setStroke(dashedStroke);
        graphics.setColor(config.getExpiringColor());
        graphics.draw(new Line2D.Double(point.getX(), point.getY(), point.getX() + xOffset, point.getY() + yOffset));
    }

    private boolean isWearingGlove()
    {
        ItemContainer equipment = client.getItemContainer(InventoryID.EQUIPMENT);
        if (equipment == null)
        {
            return false;
        }

        int weaponIndex = EquipmentInventorySlot.WEAPON.getSlotIdx();
        Item weapon = equipment.getItem(weaponIndex);
        if (weapon == null)
        {
            return false;
        }

        switch (weapon.getId())
        {
            case ItemID.CORMORANTS_GLOVE:
            case ItemID.CORMORANTS_GLOVE_22817:
                return true;
            default:
                return false;
        }
    }
}