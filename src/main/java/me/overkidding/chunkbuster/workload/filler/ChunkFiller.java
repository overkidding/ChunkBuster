package me.overkidding.chunkbuster.workload.filler;

import org.bukkit.Chunk;
import org.bukkit.Material;

/**
 * We are defining out problem with an interface.
 * Our task is it to fill a volume defined by two corners with blocks.
 */
public interface ChunkFiller {

    void fill(Chunk chunk, Material material);

}