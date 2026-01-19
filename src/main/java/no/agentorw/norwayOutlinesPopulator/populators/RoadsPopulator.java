package no.agentorw.norwayOutlinesPopulator.populators;

import no.agentorw.norwayOutlinesPopulator.utils.ChunkDataManager;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

public class RoadsPopulator extends BlockPopulator {
    private final ChunkDataManager dataManager;
    private final Logger log;
    private static final Map<Integer, Material> BLOCK_MAP = Map.ofEntries(
        Map.entry(18, Material.WHITE_CONCRETE),
        Map.entry(17, Material.BROWN_TERRACOTTA),
        Map.entry(16, Material.CYAN_CONCRETE),
        Map.entry(15, Material.RED_CONCRETE),
        Map.entry(14, Material.YELLOW_GLAZED_TERRACOTTA),   
        Map.entry(13, Material.ORANGE_GLAZED_TERRACOTTA) ,
        Map.entry(12, Material.LIGHT_GRAY_TERRACOTTA),

// Areal
        Map.entry(11, Material.ORANGE_TERRACOTTA),
        Map.entry(10, Material.WHITE_TERRACOTTA),
        Map.entry(9, Material.WHITE_GLAZED_TERRACOTTA),
        Map.entry(8, Material.RED_GLAZED_TERRACOTTA),
        Map.entry(7, Material.LIGHT_GRAY_CONCRETE),
        Map.entry(6, Material.BLUE_GLAZED_TERRACOTTA),
        Map.entry(5, Material.YELLOW_CONCRETE),
        Map.entry(4, Material.ORANGE_CONCRETE),
        Map.entry(3, Material.BLACK_CONCRETE),

// Linjer (plasser før areal)
        Map.entry(2, Material.BROWN_GLAZED_TERRACOTTA),
        Map.entry(1, Material.LIGHT_BLUE_GLAZED_TERRACOTTA)
    );

    private static final Map<Material, Integer> PRIORITY_MAP = invertMap();

    public RoadsPopulator(ChunkDataManager chunkDataManager, Logger PluginLog) {
        this.dataManager = chunkDataManager;
        this.log = PluginLog;
    }

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        List<int[]> blocks = dataManager.getChunkData(chunkX, chunkZ);
        if (blocks.isEmpty()) return;

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
