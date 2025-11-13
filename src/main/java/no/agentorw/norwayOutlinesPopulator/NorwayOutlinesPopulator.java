package no.agentorw.norwayOutlinesPopulator;

import no.agentorw.norwayOutlinesPopulator.events.WorldInitListener;
import no.agentorw.norwayOutlinesPopulator.utils.ChunkDataManager;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.logging.Logger;

public final class NorwayOutlinesPopulator extends JavaPlugin implements Listener {
    private Logger log;

    @Override
    public void onEnable() {
        log = getLogger();

        File buildingFolder = new File(getDataFolder(), "buildings");
        File roadsFolder = new File(getDataFolder(), "roads");
        buildingFolder.mkdirs();
        roadsFolder.mkdirs();

        ChunkDataManager buildingManager = new ChunkDataManager(buildingFolder, log);
        ChunkDataManager roadsManager = new ChunkDataManager(roadsFolder, log);

        getServer().getPluginManager().registerEvents(new WorldInitListener(buildingManager, roadsManager, log), this);

        log.info("NorwayOutlinesPopulator initialized with chunk-based JSON loading.");
    }

    @Override
    public void onDisable() {
        log.info("=== Disabling NorwayOutlinesPopulator ===");
    }
}
