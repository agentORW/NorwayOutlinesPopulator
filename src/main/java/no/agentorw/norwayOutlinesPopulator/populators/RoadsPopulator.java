package no.agentorw.norwayOutlinesPopulator.populators;

import no.agentorw.norwayOutlinesPopulator.utils.loadJson;
import org.bukkit.Material;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.LimitedRegion;
import org.bukkit.generator.WorldInfo;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class RoadsPopulator extends BlockPopulator {
    private static final Map<String, List<int[]>> blocksPerChunk = loadJson.loadJson(new File("plugins/NorwayOutlinesData/roads.json"));

    @Override
    public void populate(@NotNull WorldInfo worldInfo, @NotNull Random random, int chunkX, int chunkZ, @NotNull LimitedRegion limitedRegion) {

        // Block ID to Material (used in your current logic)
        Map<Integer, Material> blc = new HashMap<>();
        blc.put(18, Material.WHITE_CONCRETE);             // Kjørebanekant
        blc.put(17, Material.BROWN_TERRACOTTA);           // Kantstein
        blc.put(16, Material.CYAN_CONCRETE);              // Skiltportal
        blc.put(15, Material.RED_CONCRETE);               // Vegbom
        blc.put(14, Material.YELLOW_GLAZED_TERRACOTTA);   // OverkjørbartArealAvgrensning
        blc.put(13, Material.ORANGE_GLAZED_TERRACOTTA);   // AnnetVegarealAvgrensning (MultiLineString)
        blc.put(12, Material.LIGHT_GRAY_TERRACOTTA);      // Vegrekkverk

// Areal
        blc.put(11, Material.ORANGE_TERRACOTTA);          // AnnetVegarealAvgrensning (MultiPolygon)
        blc.put(10, Material.WHITE_TERRACOTTA);           // FeristAvgrensning (linje->polygon)
        blc.put(9, Material.WHITE_GLAZED_TERRACOTTA);     // GangfeltAvgrensning (linje->polygon)
        blc.put(8, Material.RED_GLAZED_TERRACOTTA);       // FartsdemperAvgrensing (linje->polygon)
        blc.put(7, Material.LIGHT_GRAY_CONCRETE);         // Trafikkøy
        blc.put(6, Material.BLUE_GLAZED_TERRACOTTA);      // Kjørebanekant, Vegdekkekant, Vegskulderkant
        blc.put(5, Material.YELLOW_CONCRETE);             // ParkeringsOmråde
        blc.put(4, Material.ORANGE_CONCRETE);             // VegGåendeOgSyklende
        blc.put(3, Material.BLACK_CONCRETE);              // VegKjørende

// Linjer (plasser før areal)
        blc.put(2, Material.BROWN_GLAZED_TERRACOTTA);     // Vegskulderkant (MultiLineString)
        blc.put(1, Material.LIGHT_BLUE_GLAZED_TERRACOTTA);// Vegdekkekant (MultiLineString)

        // Reverse map: Material -> Priority (1 = lowest, 12 = highest)
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
            int blckid = coord[3];
            Material newMaterial = blc.get(blckid);

            if (newMaterial == null || !limitedRegion.isInRegion(x, y, z)) {
                continue;
            }

            int high_y = limitedRegion.getHighestBlockYAt(x, z) - 1;

            // Ignore clamping road fencing into the ground since its not in the ground.
            if (Math.abs(high_y - y) <= 3 && blckid != 12) {
                y = high_y;
            }

            Material existingMaterial = limitedRegion.getType(x, y, z);
            int existingPriority = materialPriority.getOrDefault(existingMaterial, 0); // Air or unknown = priority 0
            int newPriority = blckid;

            // Place only if new block has higher priority
            if (newPriority > existingPriority) {
                limitedRegion.setType(x, y, z, newMaterial);
            }
        }
    }
}
