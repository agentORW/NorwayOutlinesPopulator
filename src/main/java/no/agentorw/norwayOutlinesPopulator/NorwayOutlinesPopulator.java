package no.agentorw.norwayOutlinesPopulator;

import no.agentorw.norwayOutlinesPopulator.populators.BuildingPopulator;
import no.agentorw.norwayOutlinesPopulator.populators.RoadsPopulator;
import no.agentorw.norwayOutlinesPopulator.utils.LoadJson;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public final class NorwayOutlinesPopulator extends JavaPlugin implements Listener {

    private Logger log;
    private Map<String, List<int[]>> buildingData;
    private Map<String, List<int[]>> roadData;

    @Override
    public void onEnable() {
        this.log = getLogger();
        log.info("Enabling NorwayOutlinesPopulator");

        // Ensure plugin data folder exists
        if (!getDataFolder().exists() && !getDataFolder().mkdirs()) {
            log.warning("Failed to create plugin data folder!");
        }

        // Load JSON data once, store for reuse
        File buildingsFile = new File(getDataFolder(), "buildings.json");
        File roadsFile = new File(getDataFolder(), "roads.json");

        if (!buildingsFile.exists()) {
            log.warning("Missing buildings.json file in data folder.");
        } else {
            buildingData = LoadJson.loadJson(buildingsFile);
            log.info("Loaded building data for " + buildingData.size() + " chunks.");
        }

        if (!roadsFile.exists()) {
            log.warning("Missing roads.json file in data folder.");
        } else {
            roadData = LoadJson.loadJson(roadsFile);
            log.info("Loaded road data for " + roadData.size() + " chunks.");
        }

        // Register world initialization listener
        getServer().getPluginManager().registerEvents(this, this);

        log.info("NorwayOutlinesPopulator enabled successfully.");
    }

    @Override
    public void onDisable() {
        log.info("=== Disabling NorwayOutlinesPopulator ===");
        buildingData = null;
        roadData = null;
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();

        if (roadData != null) {
            world.getPopulators().add(new RoadsPopulator(roadData, log));
            log.fine("Added RoadsPopulator to world: " + world.getName());
        } else {
            log.warning("Skipping RoadsPopulator – no data loaded.");
        }

        if (buildingData != null) {
            world.getPopulators().add(new BuildingPopulator(buildingData, log));
            log.fine("Added BuildingPopulator to world: " + world.getName());
        } else {
            log.warning("Skipping BuildingPopulator – no data loaded.");
        }
    }
}
