package group6.entity.device.sensor;

/**
 * Strategy interface responsible for driving periodic sensor updates.
 * Implementations decide how {@link Sensor#readValue()} should be invoked
 * over time (threads, scheduled executors, simulation hooks, etc.).
 */
public interface SensorUpdateScheduler {

  /**
   * Starts driving updates for the provided sensor.
   *
   * @param sensor the sensor to schedule
   * @return handle that can be used to cancel the updates
   */
  SensorUpdateHandle schedule(Sensor sensor);

  /**
   * Stops all ongoing schedules and releases resources.
   */
  void shutdown();

  /**
   * Handle representing a scheduled sensor update task.
   */
  interface SensorUpdateHandle {
    /**
     * Cancels updates for the associated sensor.
     */
    void cancel();
  }
}
