package group6.ui.helpers;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * A utility class for retrieving the version of the application from the
 * pom.xml file.
 *
 * <p>
 * This class provides a method to read the version number from the pom.xml
 * file located in the root directory of the project.
 *
 * <p>
 * Created with assistance from ChatGPT.
 *
 * @author Svein Antonsen
 * @since 2.0-SNAPSHOT
 */
public final class VersionUtil {

  private static final Logger LOGGER = LoggerFactory.getLogger(VersionUtil.class);

  private VersionUtil() {
    // Private constructor to prevent instantiation
  }

  /**
   * Retrieves the version of the application from the pom.xml file.
   *
   * @return The version number as a String, or "UNKNOWN" if an error occurs.
   */
  public static String getVersion() {
    try {
      File pomFile = new File("pom.xml");
      DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
      factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
      factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
      factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
      factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
      factory.setXIncludeAware(false);
      factory.setExpandEntityReferences(false);

      DocumentBuilder builder = factory.newDocumentBuilder();
      Document doc = builder.parse(pomFile);

      doc.getDocumentElement().normalize();
      return doc.getElementsByTagName("version").item(0).getTextContent();
    } catch (IOException | ParserConfigurationException | DOMException | SAXException e) {
      LOGGER.error("Error reading version from pom.xml", e);
      return "UNKNOWN";
    }
  }
}
