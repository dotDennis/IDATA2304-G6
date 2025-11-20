package group6.ui.helpers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import group6.entity.device.Device;
import group6.entity.device.ActuatorType;
import group6.entity.device.SensorType;
import group6.entity.device.actuator.Actuator;
import group6.entity.device.sensor.Sensor;
import group6.entity.device.sensor.SensorUpdateScheduler;
import group6.entity.device.sensor.SensorUpdateScheduler.SensorUpdateHandle;
import group6.entity.device.sensor.ThreadedSensorUpdateScheduler;
import group6.entity.node.SensorNode;
import group6.logic.factory.ActuatorFactory;
import group6.logic.factory.SensorFactory;
import group6.net.TcpServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Manages embedded {@link SensorNode} instances hosted inside the GUI process.
 * <p>
 * Provides lifecycle helpers for nodes plus utility methods for adding/removing
 * sensors and actuators. Ensures device IDs remain unique across all nodes.
 * <p>
 * Parts of code generated with help of AI.
 * And modified to fit our understanding, extra research was necessary.
 * <p>
 * The function of this class is meant to let us create sensor nodes
 * that run within the same process as the GUI application.
 * This allows for easier testing and demonstration of the system
 * without needing separate processes for each sensor node.
 * 
 * @author dotDennis
 * @since 0.2.0
 */
public class EmbeddedSensorNodeManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedSensorNodeManager.class);
  private static final Map<String, EmbeddedNode> GLOBAL_NODES = new ConcurrentHashMap<>();
  private static final Map<String, String> GLOBAL_SENSOR_IDS = new ConcurrentHashMap<>();
  private static final Map<String, String> GLOBAL_ACTUATOR_IDS = new ConcurrentHashMap<>();

  /**
   * State holder for a running embedded node and its server resources.
   */
  public static class EmbeddedNode {
    private final String nodeId;
    private final String host;
    private final int port;
    private final long refreshIntervalMs;
    private final SensorNode sensorNode;
    private final TcpServer server;
    private final Thread serverThread;

    EmbeddedNode(String nodeId,
        String host,
        int port,
        long refreshIntervalMs,
        SensorNode sensorNode,
        TcpServer server,
        Thread serverThread) {
      this.nodeId = nodeId;
      this.host = host;
      this.port = port;
      this.refreshIntervalMs = refreshIntervalMs;
      this.sensorNode = sensorNode;
      this.server = server;
      this.serverThread = serverThread;
    }

    /**
     * Gets the node ID.
     * 
     * @return the node ID
     */
    public String getNodeId() {
      return nodeId;
    }

    /**
     * Gets the host address.
     * 
     * @return the host address
     */
    public String getHost() {
      return host;
    }

    /**
     * Gets the port number.
     * 
     * @return the port number
     */
    public int getPort() {
      return port;
    }

    /**
     * Gets the refresh interval in milliseconds.
     * 
     * @return the refresh interval in milliseconds
     */
    public long getRefreshIntervalMs() {
      return refreshIntervalMs;
    }

    /**
     * Gets the underlying sensor node.
     * 
     * @return the sensor node
     */
    public SensorNode getSensorNode() {
      return sensorNode;
    }
  }

  // Instance fields
  private final Map<String, EmbeddedNode> nodes;
  private final Map<String, SensorUpdateHandle> sensorUpdateHandles;
  private final SensorUpdateScheduler scheduler;
  private final DeviceIdRegistry sensorIdRegistry;
  private final DeviceIdRegistry actuatorIdRegistry;

  /**
   * Creates a manager backed by a default threaded scheduler.
   */
  public EmbeddedSensorNodeManager() {
    this(new ThreadedSensorUpdateScheduler());
  }

  /**
   * Allows adding a custom scheduler for tests or alternative runtimes.
   * 
   * @param scheduler the scheduler to use
   */
  public EmbeddedSensorNodeManager(SensorUpdateScheduler scheduler) {
    this.scheduler = Objects.requireNonNull(scheduler, "scheduler");
    this.nodes = new ConcurrentHashMap<>();
    this.sensorUpdateHandles = new ConcurrentHashMap<>();
    this.sensorIdRegistry = new DeviceIdRegistry(GLOBAL_SENSOR_IDS, "Sensor");
    this.actuatorIdRegistry = new DeviceIdRegistry(GLOBAL_ACTUATOR_IDS, "Actuator");
  }

  /**
   * Spins up an embedded node with its server and registers globals.
   * 
   * @param nodeId            the node ID
   * @param host              the host address
   * @param port              the port number
   * @param refreshIntervalMs the sensor node refresh interval in milliseconds
   * 
   * @throws IOException              if server fails to start
   * @throws IllegalArgumentException if node ID already exists or port is in use
   * 
   * @return the created embedded node
   */
  public synchronized EmbeddedNode createNode(String nodeId,
      String host,
      int port,
      long refreshIntervalMs) throws IOException {
    if (GLOBAL_NODES.containsKey(nodeId)) {
      throw new IllegalArgumentException("Node with ID " + nodeId + " already exists");
    }
    ensurePortAvailable(port);

    SensorNode sensorNode = new SensorNode(nodeId);
    sensorNode.setSensorNodeInterval(refreshIntervalMs > 0 ? refreshIntervalMs : 5000);

    TcpServer server = new TcpServer(port, sensorNode);
    Thread serverThread = new Thread(() -> {
      try {
        server.start();
      } catch (IOException e) {
        LOGGER.error("Sensor node {} server stopped with error", nodeId, e);
      }
    }, "EmbeddedSensorServer-" + nodeId);
    serverThread.setDaemon(true);
    serverThread.start();

    EmbeddedNode embeddedNode = new EmbeddedNode(nodeId, host, port,
        sensorNode.getSensorNodeInterval(), sensorNode, server, serverThread);
    nodes.put(nodeId, embeddedNode);
    GLOBAL_NODES.put(nodeId, embeddedNode);
    LOGGER.info("Embedded sensor node {} started on port {}", nodeId, port);
    return embeddedNode;
  }

  /**
   * Stops a running node, releases devices, and tears down the server.
   * 
   * @param nodeId the node ID
   * @throws IllegalArgumentException if no node is found
   */
  public synchronized void removeNode(String nodeId) {
    EmbeddedNode node = nodes.remove(nodeId);
    if (node == null) {
      return;
    }
    GLOBAL_NODES.remove(nodeId);
    releaseDeviceIds(node);
    node.server.stop();
    try {
      node.serverThread.join(2000);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      LOGGER.warn("Interrupted while stopping node {}", nodeId, e);
    }
    LOGGER.info("Embedded sensor node {} stopped", nodeId);
  }

  /**
   * Checks whether a node with the given ID exists locally.
   * 
   * @param nodeId the node ID
   * @return true if the node exists, false otherwise
   */
  public boolean hasNode(String nodeId) {
    return nodes.containsKey(nodeId);
  }

  /**
   * Returns a snapshot of all embedded nodes.
   * 
   * @return the list of embedded nodes
   */
  public List<EmbeddedNode> listNodes() {
    return Collections.unmodifiableList(new ArrayList<>(nodes.values()));
  }

  /**
   * Returns just the node IDs for convenience.
   * 
   * @return the list of node IDs
   */
  public List<String> listNodeIds() {
    return new ArrayList<>(nodes.keySet());
  }

  /**
   * Resolves a node or throws if not present.
   * 
   * @param nodeId the node ID
   * @return the embedded node
   * @throws IllegalArgumentException if no node is found
   */
  private EmbeddedNode requireNode(String nodeId) {
    EmbeddedNode node = nodes.get(nodeId);
    if (node == null) {
      throw new IllegalArgumentException("Sensor node not found: " + nodeId);
    }
    return node;
  }

  /**
   * Adds a sensor, ensuring unique IDs and auto-update scheduling.
   * 
   * @param nodeId           the node ID
   * @param type             the sensor type
   * @param deviceId         the sensor device ID
   * @param updateIntervalMs the sensor update interval in milliseconds
   * @return the created sensor
   * @throws IllegalArgumentException if device ID is already in use
   */
  public Sensor addSensor(String nodeId, SensorType type, String deviceId, long updateIntervalMs) {
    EmbeddedNode node = requireNode(nodeId);
    String normalized = normalizeId(deviceId);
    ensureDeviceIdAvailable(node.sensorNode.getSensors(), nodeId, normalized, "Sensor");
    sensorIdRegistry.register(nodeId, normalized);
    try {
      Sensor sensor = SensorFactory.createSensor(type, deviceId);
      sensor.setUpdateInterval(updateIntervalMs);
      node.sensorNode.addSensor(sensor);
      startSensorAutoUpdate(nodeId, sensor);
      LOGGER.info("Added sensor {} ({}) to {} with interval {} ms", deviceId, type, nodeId, sensor.getUpdateInterval());
      return sensor;
    } catch (RuntimeException e) {
      sensorIdRegistry.unregister(nodeId, normalized);
      throw e;
    }
  }

  /**
   * Removes a sensor and stops any scheduled updates.
   * 
   * @param nodeId   the node ID
   * @param deviceId the sensor device ID
   * @return true if the sensor was removed, false otherwise
   * @throws IllegalArgumentException if no node is found
   */
  public boolean removeSensor(String nodeId, String deviceId) {
    EmbeddedNode node = requireNode(nodeId);
    for (Sensor sensor : new ArrayList<>(node.sensorNode.getSensors())) {
      if (sensor.getDeviceId().equalsIgnoreCase(deviceId)) {
        boolean removed = node.sensorNode.removeSensor(sensor);
        if (removed) {
          stopSensorAutoUpdate(nodeId, sensor.getDeviceId());
          String normalized = normalizeId(deviceId);
          sensorIdRegistry.unregister(nodeId, normalized);
          LOGGER.info("Removed sensor {} from {}", deviceId, nodeId);
        }
        return removed;
      }
    }
    return false;
  }

  /**
   * Returns a copy of the node's sensors to avoid modification.
   * 
   * @param nodeId the node ID
   * @return the list of sensors
   * @throws IllegalArgumentException if no node is found
   */
  public List<Sensor> listSensors(String nodeId) {
    EmbeddedNode node = requireNode(nodeId);
    return List.copyOf(node.sensorNode.getSensors());
  }

  /**
   * Adds an actuator to the node with ID duplication safety.
   * 
   * @param nodeId   the node ID
   * @param type     the actuator type
   * @param deviceId the actuator device ID
   * @return the created actuator
   * @throws IllegalArgumentException if device ID is already in use
   */
  public Actuator addActuator(String nodeId, ActuatorType type, String deviceId) {
    EmbeddedNode node = requireNode(nodeId);
    String normalized = normalizeId(deviceId);
    ensureDeviceIdAvailable(node.sensorNode.getActuators(), nodeId, normalized, "Actuator");
    actuatorIdRegistry.register(nodeId, normalized);
    try {
      Actuator actuator = ActuatorFactory.createActuator(type, deviceId);
      node.sensorNode.addActuator(actuator);
      LOGGER.info("Added actuator {} ({}) to {}", deviceId, type, nodeId);
      return actuator;
    } catch (RuntimeException e) {
      actuatorIdRegistry.unregister(nodeId, normalized);
      throw e;
    }
  }

  /**
   * Removes an actuator from the node.
   * 
   * @param nodeId   the node ID
   * @param deviceId the actuator device ID
   * @return true if the actuator was removed, false otherwise
   * @throws IllegalArgumentException if no node is found
   */
  public boolean removeActuator(String nodeId, String deviceId) {
    EmbeddedNode node = requireNode(nodeId);
    for (Actuator actuator : new ArrayList<>(node.sensorNode.getActuators())) {
      if (actuator.getDeviceId().equalsIgnoreCase(deviceId)) {
        boolean removed = node.sensorNode.removeActuator(actuator);
        if (removed) {
          String normalized = normalizeId(deviceId);
          actuatorIdRegistry.unregister(nodeId, normalized);
          LOGGER.info("Removed actuator {} from {}", deviceId, nodeId);
        }
        return removed;
      }
    }
    return false;
  }

  /**
   * Returns the actuators currently registered on the node.
   * 
   * @param nodeId the node ID
   * @return the list of actuators
   * @throws IllegalArgumentException if no node is found
   */
  public List<Actuator> listActuators(String nodeId) {
    EmbeddedNode node = requireNode(nodeId);
    return List.copyOf(node.sensorNode.getActuators());
  }

  /**
   * Shuts down all embedded nodes and their schedulers.
   */
  public synchronized void shutdown() {
    for (String nodeId : new ArrayList<>(nodes.keySet())) {
      removeNode(nodeId);
    }
    nodes.clear();
    sensorUpdateHandles.clear();
    sensorIdRegistry.clearLocalCache();
    actuatorIdRegistry.clearLocalCache();
    scheduler.shutdown();
  }

  /**
   * Validates that a candidate device ID is not already used on the node.
   * 
   * @param devices the existing devices on the node
   * @param nodeId  the node ID
   * @param key     the normalized device ID to check
   * @param label   the device label for error messages
   * @throws IllegalArgumentException if the device ID is already in use
   */
  private void ensureDeviceIdAvailable(List<? extends Device<?>> devices,
      String nodeId,
      String key,
      String label) {
    for (Device<?> device : devices) {
      if (normalizeId(device.getDeviceId()).equals(key)) {
        throw new IllegalArgumentException(label + " device id already in use on node " + nodeId);
      }
    }
  }

  /**
   * Unregisters all device IDs belonging to the node.
   * 
   * @param node the embedded node
   */
  private void releaseDeviceIds(EmbeddedNode node) {
    for (Sensor sensor : new ArrayList<>(node.sensorNode.getSensors())) {
      stopSensorAutoUpdate(node.nodeId, sensor.getDeviceId());
      String key = normalizeId(sensor.getDeviceId());
      sensorIdRegistry.unregister(node.nodeId, key);
    }
    for (Actuator actuator : new ArrayList<>(node.sensorNode.getActuators())) {
      String key = normalizeId(actuator.getDeviceId());
      actuatorIdRegistry.unregister(node.nodeId, key);
    }
  }

  /**
   * Validates that no other embedded node listens on the same port.
   * 
   * @param port the port number
   * @throws IllegalArgumentException if the port is already in use
   */
  private void ensurePortAvailable(int port) {
    validatePort(port);
    for (EmbeddedNode node : GLOBAL_NODES.values()) {
      if (node.getPort() == port) {
        throw new IllegalArgumentException("Port " + port + " is already in use by node " + node.getNodeId());
      }
    }
  }

  /**
   * Throws if the provided port is outside the allowed range.
   * 
   * @param port the port number
   * @throws IllegalArgumentException if the port is invalid
   */
  private void validatePort(int port) {
    if (port < 1024 || port > 65535) {
      throw new IllegalArgumentException("Port must be between 1024 and 65535");
    }
  }

  /**
   * Schedules autonomous updates for the given sensor.
   * 
   * @param nodeId the node ID
   * @param sensor the sensor to schedule
   * @throws IllegalArgumentException if sensor is null
   */
  private void startSensorAutoUpdate(String nodeId, Sensor sensor) {
    String key = buildHandleKey(nodeId, sensor.getDeviceId());
    stopSensorAutoUpdate(nodeId, sensor.getDeviceId());
    SensorUpdateHandle handle = scheduler.schedule(sensor);
    sensorUpdateHandles.put(key, handle);
  }

  /**
   * Cancels any scheduled auto-update for the specified sensor.
   * 
   * @param nodeId   the node ID
   * @param deviceId the sensor device ID
   * @throws IllegalArgumentException if deviceId is null
   */
  private void stopSensorAutoUpdate(String nodeId, String deviceId) {
    if (deviceId == null) {
      return;
    }
    String key = buildHandleKey(nodeId, deviceId);
    SensorUpdateHandle handle = sensorUpdateHandles.remove(key);
    if (handle != null) {
      handle.cancel();
    }
  }

  /**
   * Creates a unique key for sensor update handles.
   * 
   * @param nodeId   the node ID
   * @param deviceId the device ID
   * @return the handle key
   */
  private String buildHandleKey(String nodeId, String deviceId) {
    return nodeId + ":" + normalizeId(deviceId);
  }

  /**
   * Lowercases and trims IDs to ensure consistent comparisons.
   * 
   * @param id the device ID
   * @return the normalized device ID
   * @throws IllegalArgumentException if id is null
   */
  private static String normalizeId(String id) {
    if (id == null) {
      return "";
    }
    return id.trim().toLowerCase(Locale.ROOT);
  }

  /**
   * Registry to enforce unique device IDs across all embedded nodes.
   */
  private static final class DeviceIdRegistry {
    private final Map<String, String> globalIds;
    private final Map<String, String> localOwners = new ConcurrentHashMap<>();
    private final String label;

    /** @param globalIds shared map to enforce cross-node uniqueness */
    DeviceIdRegistry(Map<String, String> globalIds, String label) {
      this.globalIds = globalIds;
      this.label = label;
    }

    /**
     * Records a device ID, throwing if another node already owns it.
     */
    void register(String nodeId, String key) {
      String existing = globalIds.putIfAbsent(key, nodeId);
      if (existing != null && !existing.equals(nodeId)) {
        throw new IllegalArgumentException(label + " device id already in use by node " + existing);
      }
      String owner = localOwners.putIfAbsent(key, nodeId);
      if (owner != null && !owner.equals(nodeId)) {
        globalIds.remove(key, nodeId);
        throw new IllegalArgumentException(label + " device id already in use by node " + owner);
      }
    }

    /**
     * Removes the ID ownership from local/global registries.
     */
    void unregister(String nodeId, String key) {
      localOwners.remove(key);
      globalIds.remove(key, nodeId);
    }

    /**
     * Clears local ownership information; used during shutdown.
     */
    void clearLocalCache() {
      localOwners.clear();
    }
  }
}
