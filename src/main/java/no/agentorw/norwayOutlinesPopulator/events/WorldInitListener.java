package no.agentorw.norwayOutlinesPopulator.events;

import no.agentorw.norwayOutlinesPopulator.populators.BuildingPopulator;
import no.agentorw.norwayOutlinesPopulator.populators.RoadsPopulator;
import no.agentorw.norwayOutlinesPopulator.utils.ChunkDataManager;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;

import java.util.logging.Logger;

public class WorldInitListener implements Listener {
    private final ChunkDataManager buildingManager;
    private final ChunkDataManager roadsManager;
    private final Logger log;

    public WorldInitListener(ChunkDataManager buildingManager, ChunkDataManager roadsManager, Logger log) {
        this.buildingManager = buildingManager;
        this.roadsManager = roadsManager;
        this.log = log;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        world.getPopulators().add(new RoadsPopulator(roadsManager, log));
        world.getPopulators().add(new BuildingPopulator(buildingManager, log));
        log.info("Added populators to world: " + world.getName());
    }
}
