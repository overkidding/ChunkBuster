package me.overkidding.chunkbuster.workload.filler.impl;

import lombok.AllArgsConstructor;
import me.overkidding.chunkbuster.ChunkBuster;
import me.overkidding.chunkbuster.modules.ChunkBust;
import me.overkidding.chunkbuster.workload.WorkloadRunnable;
import me.overkidding.chunkbuster.workload.filler.ChunkFiller;
import me.overkidding.chunkbuster.workload.impl.ChunkBustBlock;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
public class DistributedFiller implements ChunkFiller {

    private final ChunkBust bust;

    @Override
    public void fill(Chunk chunk, Material material) {
        List<Block> blocks = getBlocks(chunk);
        WorkloadRunnable runnable = bust.getRunnable();
        blocks.forEach(block -> {
            ChunkBustBlock chunkBustBlock = new ChunkBustBlock(block.getLocation(), material);
            runnable.addWorkLoad(chunkBustBlock);
        });
        bust.setTotalBlocks(blocks.size());
        runnable.runTaskTimer(ChunkBuster.getInstance(), 20L * bust.getDelayBeforeStart(), 1L);
    }

    private List<Block> getBlocks(Chunk chunk) {
        List<Block> blocks = new ArrayList<>();

        final int minX = (chunk.getX() << 4) - 1;
        final int minZ = (chunk.getZ() << 4) - 1;

        final int maxX = minX + 15;
        final int maxZ = minZ + 15;

        int minY = bust.getMinHeight();
        int maxY = bust.getMaxHeight();

        World world = chunk.getWorld();
        for (int x = minX; x <= maxX; ++x) {
            for (int y = minY; y <= maxY; ++y) {
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