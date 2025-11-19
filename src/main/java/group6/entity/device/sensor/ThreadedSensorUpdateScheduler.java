package group6.entity.device.sensor;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorUpdateScheduler backed by a shared ScheduledExecutorService.
 * Provides consistent error handling and lifecycle management.
 */
public class ThreadedSensorUpdateScheduler implements SensorUpdateScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadedSensorUpdateScheduler.class);

  private final ScheduledExecutorService executor;
  private final ConcurrentMap<String, ScheduledHandle> handles = new ConcurrentHashMap<>();

  // Using atomic boolean due to errors without, see:
  // https://stackoverflow.com/questions/16918237/multiple-thread-writing-to-the-same-boolean
  private final AtomicBoolean shutdown = new AtomicBoolean(false);

  public ThreadedSensorUpdateScheduler() {
    this(createExecutor());
  }

  private ThreadedSensorUpdateScheduler(ScheduledExecutorService executor) {
    this.executor = executor;
  }

  private static ScheduledExecutorService createExecutor() {
    int threads = Math.max(2, Runtime.getRuntime().availableProcessors());
    ThreadFactory factory = runnable -> {
      Thread thread = new Thread(runnable, "SensorUpdateScheduler");
      thread.setDaemon(true);
      return thread;
    };
    return Executors.newScheduledThreadPool(threads, factory);
  }

  @Override
  public SensorUpdateHandle schedule(Sensor sensor) {
    Objects.requireNonNull(sensor, "sensor");
    if (shutdown.get()) {
      throw new IllegalStateException("Scheduler has been shut down");
    }
    ScheduledHandle handle = new ScheduledHandle(sensor, executor);
    ScheduledHandle previous = handles.put(sensor.getDeviceId(), handle);
    if (previous != null) {
      previous.cancel();
    }
    handle.start();
    return handle;
  }

  @Override
  public void shutdown() {
    if (shutdown.compareAndSet(false, true)) {
      handles.values().forEach(SensorUpdateHandle::cancel);
      handles.clear();
      executor.shutdownNow();
    }
  }

  private static final class ScheduledHandle implements SensorUpdateHandle, Runnable {
    private final Sensor sensor;
    private final ScheduledExecutorService executor;
    private final AtomicBoolean running = new AtomicBoolean(true);
    private ScheduledFuture<?> future;

    private ScheduledHandle(Sensor sensor, ScheduledExecutorService executor) {
      this.sensor = sensor;
      this.executor = executor;
    }

    private void start() {
      future = executor.scheduleAtFixedRate(this, 0, sensor.getUpdateInterval(), TimeUnit.MILLISECONDS);
    }

    @Override
    public void cancel() {
      if (running.compareAndSet(true, false) && future != null) {
        future.cancel(true);
      }
    }

    @Override
    public void run() {
      if (!running.get()) {
        return;
      }
      try {
        sensor.readValue();
      } catch (Exception e) {
        LOGGER.warn("Auto-update failed for sensor {}: {}", sensor.getDeviceId(), e.getMessage());
      }
    }
  }
}
