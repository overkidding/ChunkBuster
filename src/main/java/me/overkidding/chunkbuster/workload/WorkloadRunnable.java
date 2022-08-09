package me.overkidding.chunkbuster.workload;

import java.util.ArrayDeque;
import java.util.Deque;

public class WorkloadRunnable implements Runnable{

    private static final double MAX_MILLIS_PER_TICK = 0.75;
    private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

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
    }
}
