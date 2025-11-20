package group6.logic.events;

import group6.entity.node.SensorNode;

/**
 * Observer for high-level sensor node updates.
 */
public interface SensorNodeUpdateListener {

  /**
   * Called when sensors on the given node have been updated.
   * 
   * @param node the sensor node with updated sensors
   */
  void onSensorsUpdated(SensorNode node);

  /**
   * Called when actuators on the given node have been updated.
   * 
   * @param node the sensor node with updated actuators
   */
  void onActuatorsUpdated(SensorNode node);
}
