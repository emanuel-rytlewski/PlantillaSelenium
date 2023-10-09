package environment;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import static com.google.common.io.Files.asByteSource;

public final class EnvironmentConfig {

  /**
   * Logger.
   */
  private static final Logger logger = LogManager.getLogger(EnvironmentConfig.class);


  private static final String CONFIG = "config";

  private static Map<String, Object> configMap;


  /**
   * Private constructor.
   */
  private EnvironmentConfig() {

  }

  /**
   * Initializes the environment selected for testing.
   */
  public static void initializeEnvironment() {
    String environmentConfig = CONFIG + ".yaml";
    configMap = loadConfigFile(environmentConfig);
  }

  /**
   * Load the settings of the config file defined in /config.
   * @param filename the name of the config file to be opened
   * @return The settings of the config file in a Key/Value Map
   */
  private static Map<String, Object> loadConfigFile(String filename) {
    String path = "config/" + filename;
    logger.debug("Config file: {}", path);

    File initialFile = new File(path);
    try (InputStream targetStream = asByteSource(initialFile).openStream()) {
      Yaml yaml = new Yaml();
      Map<String, Object> map = yaml.load(targetStream);
      logger.debug("Content of config file: {}", map);
      return map;
    } catch (IOException e) {
      logger.error("Problem when opening the config file", e);
      throw new IllegalArgumentException();
    }
  }

  /**
   * Gets tiny url money page as string.
   * @return String
   */
  public static String getUrl() {
    return configMap.get("baseURL").toString();
  }

}
