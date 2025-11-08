package no.agentorw.norwayOutlinesPopulator.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import no.agentorw.norwayOutlinesPopulator.NorwayOutlinesPopulator;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public class LoadJson {

    static Logger log = JavaPlugin.getPlugin(NorwayOutlinesPopulator.class).getLogger();

    public static Map<String, List<int[]>> loadJson(File file) {
        final Map<String, List<int[]>> blocksPerChunk = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            Map<String, List<List<Integer>>> raw = mapper.readValue(file, new TypeReference<>() {});
            for (Map.Entry<String, List<List<Integer>>> entry : raw.entrySet()) {
                List<int[]> coords = new ArrayList<>();
                for (List<Integer> quad : entry.getValue()) {
                    if (quad.size() == 4) {
                        coords.add(new int[]{ quad.get(0), quad.get(1), quad.get(2), quad.get(3) });
                    } else {
                        log.warning("Skipping invalid coord with size " + quad.size());
                    }
                }
                blocksPerChunk.put(entry.getKey(), coords);
            }
            log.info("Loaded block data for " + blocksPerChunk.size() + " chunks.");
        } catch (IOException e) {
            log.severe("Failed to load block data for " + file.getName() + " chunks." + " " + e.getMessage());
        }
        return Collections.unmodifiableMap(blocksPerChunk);
    }
}
