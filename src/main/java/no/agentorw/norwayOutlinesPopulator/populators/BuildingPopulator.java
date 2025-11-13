package no.agentorw.norwayOutlinesPopulator.populators;

import no.agentorw.norwayOutlinesPopulator.utils.ChunkDataManager;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;

import java.util.*;
import java.util.logging.Logger;

import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class BuildingPopulator extends BlockPopulator {
    private final ChunkDataManager dataManager;
    private final Logger log;
    private static final Map<Integer, Material> BLOCK_MAP = Map.ofEntries(
            Map.entry(1, Material.WHITE_WOOL),         // Takkant / Taksprang / Takoverbyggkant
        Map.entry(2, Material.LIGHT_GRAY_WOOL),    // FiktivBygningsavgrensning
        Map.entry(3, Material.CYAN_WOOL),          // Arkade / Portrom
        Map.entry(4, Material.ORANGE_WOOL),        // Veranda
        Map.entry(5, Material.YELLOW_WOOL),        // Låvebru / TrappBygg
        Map.entry(6, Material.BLACK_WOOL),         // VeggFrittstående
        Map.entry(7, Material.RED_WOOL),           // Bygningsdelelinje / TaksprangBunn
        Map.entry(8, Material.PINK_WOOL),          // Bygningslinje
        Map.entry(9, Material.GREEN_WOOL),         // Mønelinje
        Map.entry(10, Material.LIGHT_BLUE_WOOL),   // TakplatåTopp
        Map.entry(11, Material.BLUE_WOOL),         // Takplatå
        Map.entry(12, Material.BROWN_WOOL)        // BygningBru
    );

    private static final Map<Material, Integer> PRIORITY_MAP = invertMap();

    public BuildingPopulator(ChunkDataManager chunkDataManager, Logger PluginLog) {
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
        BuildingPopulator.BLOCK_MAP.forEach((key, value) -> inverse.put(value, key));
        return inverse;
    }
}
