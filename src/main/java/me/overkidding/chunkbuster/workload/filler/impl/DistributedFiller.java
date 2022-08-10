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
            ChunkBustBlock chunkBustBlock = new ChunkBustBlock(block.getLocation(), material, bust.isPhysics());
            runnable.addWorkLoad(chunkBustBlock);
        });

        int totalBlocks = blocks.size();
        bust.setTotalBlocks(totalBlocks);

        runnable.runTaskTimer(ChunkBuster.getInstance(), 20L * bust.getDelayBeforeStart(), 1L);
    }

    private List<Block> getBlocks(Chunk chunk) {
        List<Block> blocks = new ArrayList<>();

        World world = chunk.getWorld();

        int cx = bust.getChunkX(), cz = bust.getChunkZ();

        int minX = cx * 16;
        int minZ = cz * 16;

        int maxX = cx < 0 ? validate(cx, (minX + 16) - 1) : minX + 16;
        int maxZ = cz < 0 ? validate(cz, (minZ + 16) - 1) : minZ + 16;

        int minY = bust.getMinHeight();
        int maxY = bust.getMaxHeight();

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

    private int validate(int shouldBe, int current){
        if((current >> 4) != shouldBe){
            int prev = current - 1, next = current + 1;
            if((next >> 4) == shouldBe){
                return next;
            }else{
                return prev;
            }
        }else {
            return current;
        }
    }
}