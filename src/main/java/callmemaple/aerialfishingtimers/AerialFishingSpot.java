package callmemaple.aerialfishingtimers;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.runelite.api.NPC;

import java.time.Instant;

@AllArgsConstructor
@Getter
public class AerialFishingSpot
{
    private NPC npc;
    private Instant spawnTime;
}
