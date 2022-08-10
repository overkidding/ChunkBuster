package me.overkidding.chunkbuster.workload;

import lombok.RequiredArgsConstructor;
import me.overkidding.chunkbuster.modules.ChunkBust;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayDeque;
import java.util.Deque;

@RequiredArgsConstructor
public class WorkloadRunnable extends BukkitRunnable {

    private static final double MAX_MILLIS_PER_TICK = 0.25;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

    private final ChunkBust chunkBust;

    private final Deque<Workload> workLoadDeque = new ArrayDeque<>();

    public void addWorkLoad(Workload workload){
        workLoadDeque.add(workload);
    }

    @Override
    public void run() {
        long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

        Workload nextLoad;


        while(System.nanoTime() <= stopTime && (nextLoad = this.workLoadDeque.poll()) != null){
            nextLoad.compute();
        }

        if(workLoadDeque.size() == 0)
            chunkBust.end();
    }
}
