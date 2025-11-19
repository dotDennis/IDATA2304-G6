package group6.entity.device.sensor;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * SensorUpdateScheduler that starts a dedicated thread
 * per sensor. Threads terminate when cancel() or
 * shutdown() is called.
 */
public class ThreadedSensorUpdateScheduler implements SensorUpdateScheduler {

  private static final Logger LOGGER = LoggerFactory.getLogger(ThreadedSensorUpdateScheduler.class);

  private final ConcurrentMap<String, SensorUpdateHandle> handles = new ConcurrentHashMap<>();

  // Using atomic boolean due to errors without, see:
  // https://stackoverflow.com/questions/16918237/multiple-thread-writing-to-the-same-boolean
  private final AtomicBoolean shutdown = new AtomicBoolean(false);

  @Override
  public SensorUpdateHandle schedule(Sensor sensor) {
    Objects.requireNonNull(sensor, "sensor");
    if (shutdown.get()) {
      throw new IllegalStateException("Scheduler has been shut down");
    }
    ThreadedHandle handle = new ThreadedHandle(sensor);
    SensorUpdateHandle previous = handles.put(handle.key(), handle);
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
    }
  }

  private static final class ThreadedHandle implements SensorUpdateHandle, Runnable {
    private final Sensor sensor;
    private final Thread thread;
    private final AtomicBoolean running = new AtomicBoolean(true);

    private ThreadedHandle(Sensor sensor) {
      this.sensor = sensor;
      this.thread = new Thread(this, "SensorUpdate-" + sensor.getDeviceId());
      this.thread.setDaemon(true);
    }

    private String key() {
      return sensor.getDeviceId();
    }

    private void start() {
      thread.start();
    }

    @Override
    public void cancel() {
      if (running.compareAndSet(true, false)) {
        thread.interrupt();
      }
    }

    @Override
    public void run() {
      while (running.get()) {
        try {
          sensor.readValue();
          Thread.sleep(sensor.getUpdateInterval());
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          break;
        } catch (Exception e) {
          LOGGER.warn("Auto-update failed for sensor {}: {}", sensor.getDeviceId(), e.getMessage());
        }
      }
    }
  }
}
