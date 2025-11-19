package group6.entity.device;

/**
 * Observer interface for receiving callbacks when a device updates its state
 * or measurement.
 */
public interface DeviceUpdateListener {

  /**
   * Invoked whenever the observed device produces a new value or changes
   * internal state.
   *
   * @param device the device that triggered the update
   */
  void onDeviceUpdated(Device<?> device);
}
