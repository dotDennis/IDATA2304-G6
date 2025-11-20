package group6.logic.events;

import group6.entity.node.SensorNode;

/**
 * Observer for high-level sensor node updates.
 */
public interface SensorNodeUpdateListener {

  void onSensorsUpdated(SensorNode node);

  void onActuatorsUpdated(SensorNode node);
}
