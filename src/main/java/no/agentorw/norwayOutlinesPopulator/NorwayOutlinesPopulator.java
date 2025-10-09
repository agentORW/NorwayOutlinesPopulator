package no.agentorw.norwayOutlinesPopulator;

import no.agentorw.norwayOutlinesPopulator.populators.BuildingPopulator;
import no.agentorw.norwayOutlinesPopulator.populators.RoadsPopulator;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldInitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class NorwayOutlinesPopulator extends JavaPlugin implements Listener {

    public Logger log;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        log = getLogger();
        log.info("NorwayOutlinesPopulator enabled");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    @EventHandler
    public void onWorldInit(WorldInitEvent event) {
        World world = event.getWorld();
        world.getPopulators().add(new RoadsPopulator());
        world.getPopulators().add(new BuildingPopulator());
    }


}
