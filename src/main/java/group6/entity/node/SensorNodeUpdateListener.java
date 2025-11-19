package group6.entity.node;

/**
 * Observer for sensor node level updates.
 */
public interface SensorNodeUpdateListener {

  void onSensorsUpdated(SensorNode node);

  void onActuatorsUpdated(SensorNode node);
}
