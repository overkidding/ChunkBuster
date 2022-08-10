package me.overkidding.chunkbuster.workload.filler.impl;

import lombok.AllArgsConstructor;
import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.workload.WorkloadRunnable;
import me.overkidding.chunkbuster.workload.filler.ChunkFiller;
import me.overkidding.chunkbuster.workload.impl.ChunkBustBlock;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DistributedFiller implements ChunkFiller {

    private final WorkloadRunnable runnable;
    private final FileConfiguration configuration;

    @Override
    public void fill(Chunk chunk, Material material) {
        getBlocks(chunk).forEach(block -> {
            ChunkBustBlock chunkBustBlock = new ChunkBustBlock(block.getLocation(), material);
            this.runnable.addWorkLoad(chunkBustBlock);
        });
        runnable.runTaskTimer(ChunkBuster.getInstance(), 20L * configuration.getInt("SETTINGS.DELAY_BEFORE_START"), 1L);
    }

    private List<Block> getBlocks(Chunk chunk) {
        List<Block> blocks = new ArrayList<>();

        final int minX = (chunk.getX() << 4) - 1;
        final int minZ = (chunk.getZ() << 4) - 1;

        final int maxX = minX + 15;
        final int maxZ = minZ + 15;

        Bukkit.broadcastMessage(chunk.getX() + " " + chunk.getZ() + " " + maxX + " " + maxZ + " " + minX + " " + minZ);

        int minY = ChunkBuster.getInstance().getConfig().getInt("SETTINGS.MIN_HEIGHT");
        int maxY = ChunkBuster.getInstance().getConfig().getInt("SETTINGS.MAX_HEIGHT");

        World world = chunk.getWorld();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; ++z) {
                    Block block = world.getBlockAt(x, y, z);
                    if (block.getType() != Material.AIR && block.getType() != Material.BEDROCK) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }
}