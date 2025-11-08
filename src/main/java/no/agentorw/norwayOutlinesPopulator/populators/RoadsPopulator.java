package no.agentorw.norwayOutlinesPopulator.populators;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class RoadsPopulator extends BlockPopulator {
    private final Map<String, List<int[]>> blocksPerChunk;
    private final Logger log;
    private static final Map<Integer, Material> BLOCK_MAP = Map.ofEntries(
        Map.entry(18, Material.WHITE_CONCRETE),             // Kjørebanekant
        Map.entry(17, Material.BROWN_TERRACOTTA),           // Kantstein
        Map.entry(16, Material.CYAN_CONCRETE),              // Skiltportal
        Map.entry(15, Material.RED_CONCRETE),               // Vegbom
        Map.entry(14, Material.YELLOW_GLAZED_TERRACOTTA),   // OverkjørbartArealAvgrensning
        Map.entry(13, Material.ORANGE_GLAZED_TERRACOTTA) ,  // AnnetVegarealAvgrensning (MultiLineString)
        Map.entry(12, Material.LIGHT_GRAY_TERRACOTTA),      // Vegrekkverk

// Areal
        Map.entry(11, Material.ORANGE_TERRACOTTA),          // AnnetVegarealAvgrensning (MultiPolygon)
        Map.entry(10, Material.WHITE_TERRACOTTA),           // FeristAvgrensning (linje->polygon)
        Map.entry(9, Material.WHITE_GLAZED_TERRACOTTA),     // GangfeltAvgrensning (linje->polygon)
        Map.entry(8, Material.RED_GLAZED_TERRACOTTA),       // FartsdemperAvgrensing (linje->polygon)
        Map.entry(7, Material.LIGHT_GRAY_CONCRETE),         // Trafikkøy
        Map.entry(6, Material.BLUE_GLAZED_TERRACOTTA),      // Kjørebanekant, Vegdekkekant, Vegskulderkant
        Map.entry(5, Material.YELLOW_CONCRETE),             // ParkeringsOmråde
        Map.entry(4, Material.ORANGE_CONCRETE),             // VegGåendeOgSyklende
        Map.entry(3, Material.BLACK_CONCRETE),              // VegKjørende

// Linjer (plasser før areal)
        Map.entry(2, Material.BROWN_GLAZED_TERRACOTTA),     // Vegskulderkant (MultiLineString)
        Map.entry(1, Material.LIGHT_BLUE_GLAZED_TERRACOTTA) // Vegdekkekant (MultiLineString)
    );

    private static final Map<Material, Integer> PRIORITY_MAP = invertMap();

    public RoadsPopulator(Map<String, List<int[]>> roadData, Logger PluginLog) {
        this.blocksPerChunk = roadData;
        this.log = PluginLog;
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        String key = chunkX + "_" + chunkZ;
        List<int[]> blocks = blocksPerChunk.getOrDefault(key, Collections.emptyList());

        for (int[] coord : blocks) {
            if (coord.length < 4) {
                this.log.warning("Skipping invalid coord: " + Arrays.toString(coord));
                continue;
            }

            int x = coord[0];
            int y = coord[1];
            int z = coord[2];
            int block_id = coord[3];

            if (!limitedRegion.isInRegion(x, y, z)) {
                continue;
            }

            Material newMaterial = BLOCK_MAP.get(block_id);

            if (newMaterial == null) {
                this.log.warning("Skipping invalid block: " + Arrays.toString(coord) + " " + block_id);
                continue;
            }

            int high_y = limitedRegion.getHighestBlockYAt(x, z) - 1;

            // Ignore clamping road fencing into the ground since it's not in the ground.
            if (Math.abs(high_y - y) <= 3 && block_id != 12) {
                y = high_y;
            }

            Material existingMaterial = limitedRegion.getType(x, y, z);
            int existingPriority = PRIORITY_MAP.getOrDefault(existingMaterial, 0); // Air or unknown = priority 0

            // Place only if new block has higher priority
            if (block_id > existingPriority) {
                limitedRegion.setType(x, y, z, newMaterial);
            }
        }
    }

    private static Map<Material, Integer> invertMap() {
        Map<Material, Integer> inverse = new HashMap<>();
        RoadsPopulator.BLOCK_MAP.forEach((key, value) -> inverse.put(value, key));
        return inverse;
    }

}
