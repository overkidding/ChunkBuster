package me.overkidding.chunkbuster.workload.filler.impl;

import lombok.AllArgsConstructor;
import me.overkidding.chunkbuster.ChunkBuster;
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

    private final WorkloadRunnable runnable;

    @Override
    public void fill(Chunk chunk, Material material) {
        getBlocks(chunk).forEach(block ->{
            ChunkBustBlock chunkBustBlock = new ChunkBustBlock(block.getLocation(), material);
            this.runnable.addWorkLoad(chunkBustBlock);
        });
    }

    private List<Block> getBlocks(Chunk chunk) {
        List<Block> blocks = new ArrayList<>();

        int bx = chunk.getX()<<4;
        int bz = chunk.getZ()<<4;

        int minY = ChunkBuster.getInstance().getConfig().getInt("SETTINGS.MIN_HEIGHT");
        int maxY = ChunkBuster.getInstance().getConfig().getInt("SETTINGS.MAX_HEIGHT");

        World world = chunk.getWorld();

        for(int xx = bx; xx < bx+16; xx++) {
            for(int zz = bz; zz < bz+16; zz++) {
                for(int yy = minY; yy <= maxY; yy++) {
                    Block block = world.getBlockAt(xx,yy,zz);
                    if(block.getType() != Material.AIR) {
                        blocks.add(block);
                    }
                }
            }
        }

        return blocks;
    }
}
