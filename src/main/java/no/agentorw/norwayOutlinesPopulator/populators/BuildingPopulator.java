package no.agentorw.norwayOutlinesPopulator.populators;

import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;

import java.io.File;
import java.util.*;

import no.agentorw.norwayOutlinesPopulator.utils.loadJson;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

public class BuildingPopulator extends BlockPopulator {
    private static final Map<String, List<int[]>> blocksPerChunk = loadJson.loadJson(new File("plugins/NorwayOutlinesData/buildings.json"));

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {
        // Block ID to Material
        Map<Integer, Material> blc = new HashMap<>();
        blc.put(1, Material.WHITE_WOOL);         // Takkant / Taksprang / Takoverbyggkant
        blc.put(2, Material.LIGHT_GRAY_WOOL);    // FiktivBygningsavgrensning
        blc.put(3, Material.CYAN_WOOL);          // Arkade / Portrom
        blc.put(4, Material.ORANGE_WOOL);        // Veranda
        blc.put(5, Material.YELLOW_WOOL);        // Låvebru / TrappBygg
        blc.put(6, Material.BLACK_WOOL);         // VeggFrittstående
        blc.put(7, Material.RED_WOOL);           // Bygningsdelelinje / TaksprangBunn
        blc.put(8, Material.PINK_WOOL);          // Bygningslinje
        blc.put(9, Material.GREEN_WOOL);         // Mønelinje
        blc.put(10, Material.LIGHT_BLUE_WOOL);   // TakplatåTopp
        blc.put(11, Material.BLUE_WOOL);         // Takplatå
        blc.put(12, Material.BROWN_WOOL);        // BygningBru

        // Reverse map to check existing blocks' id's
        Map<Material, Integer> materialPriority = new HashMap<>();
        for (Map.Entry<Integer, Material> entry : blc.entrySet()) {
            materialPriority.put(entry.getValue(), entry.getKey());
        }

        String key = chunkX + "_" + chunkZ;
        List<int[]> blocks = blocksPerChunk.getOrDefault(key, Collections.emptyList());

        for (int[] coord : blocks) {
            if (coord.length < 4) {
                System.out.println("Skipping invalid coord: " + Arrays.toString(coord));
                continue;
            }

            int x = coord[0];
            int y = coord[1];
            int z = coord[2];
            int block_id = coord[3];

            if (!limitedRegion.isInRegion(x, y, z)) {
                continue;
            }

            Material newMaterial = blc.get(block_id);

            if (newMaterial == null) {
                continue;
            }

            Material existingMaterial = limitedRegion.getType(x, y, z);
            int existingPriority = materialPriority.getOrDefault(existingMaterial, 0); // Air or unknown = priority 0

            // Place only if new block has higher priority
            if (block_id > existingPriority) {
                limitedRegion.setType(x, y, z, newMaterial);
            }
        }
    }
}
