package no.agentorw.norwayOutlinesPopulator.utils;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.logging.Logger;

public class ChunkDataManager {

    private final File baseFolder;
    private final Logger log;
    private final ObjectMapper mapper = new ObjectMapper();

    // regionKey → Map<String, List<int[]>>
    private final Map<String, Map<String, List<int[]>>> cache = new LinkedHashMap<>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, Map<String, List<int[]>>> eldest) {
            return size() > MAX_CACHE_SIZE;
        }
    };

    private static final int MAX_CACHE_SIZE = 64; // configurable — holds 64 region files at a time
    private final Object lock = new Object();

    public ChunkDataManager(File baseFolder, Logger log) {
        this.baseFolder = baseFolder;
        this.log = log;
    }

    /**
     * Returns the data for a specific chunk key (e.g., "123_-45").
     */
    public List<int[]> getChunkData(int chunkX, int chunkZ) {
        String regionKey = getRegionKey(chunkX, chunkZ);

        Map<String, List<int[]>> region = getRegion(regionKey);
        if (region == null) return Collections.emptyList();

        String chunkKey = chunkX + "_" + chunkZ;
        return region.getOrDefault(chunkKey, Collections.emptyList());
    }

    /**
     * Loads or retrieves a cached 32x32 chunk region.
     */
    private Map<String, List<int[]>> getRegion(String regionKey) {
        synchronized (lock) {
            if (cache.containsKey(regionKey)) {
                return cache.get(regionKey);
            }

            File file = new File(baseFolder, regionKey + ".json");
            if (!file.exists()) {
                return null;
            }

            try {
                Map<String, List<List<Integer>>> raw = mapper.readValue(file, new TypeReference<>() {});
                Map<String, List<int[]>> converted = new HashMap<>();

                for (var entry : raw.entrySet()) {
                    List<int[]> coords = new ArrayList<>();
                    for (var arr : entry.getValue()) {
                        if (arr.size() == 4) {
                            coords.add(new int[]{arr.get(0), arr.get(1), arr.get(2), arr.get(3)});
                        }
                    }
                    converted.put(entry.getKey(), coords);
                }

                cache.put(regionKey, converted);
                log.fine("Loaded region file: " + file.getName());
                return converted;
            } catch (IOException e) {
                log.warning("Failed to load region " + regionKey + ": " + e.getMessage());
                return null;
            }
        }
    }

    /**
     * Converts chunk coords to a region file name (each region covers 32x32 chunks).
     */
    private static String getRegionKey(int chunkX, int chunkZ) {
        int regionX = (int) Math.floor(chunkX / 32.0);
        int regionZ = (int) Math.floor(chunkZ / 32.0);
        return regionX + "_" + regionZ;
    }
}

