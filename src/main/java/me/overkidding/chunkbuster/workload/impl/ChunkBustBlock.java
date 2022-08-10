package me.overkidding.chunkbuster.workload.impl;

import lombok.RequiredArgsConstructor;
import me.overkidding.chunkbuster.workload.Workload;
import org.bukkit.Location;
import org.bukkit.Material;

@RequiredArgsConstructor
public class ChunkBustBlock implements Workload {

    private final Location location;
    private final Material material;
    private final boolean applyPhysics;

    @Override
    public void compute() {
        location.getBlock().setType(material, applyPhysics);
    }
}
