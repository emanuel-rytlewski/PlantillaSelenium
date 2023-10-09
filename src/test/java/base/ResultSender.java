package base;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class ResultSender {

  /**
   * The logger.
   */
  private static final Logger logger = LogManager.getLogger("es.logger");

  private static final ObjectMapper OM = new ObjectMapper();

  /**
   * The private constructor.
   */
  private ResultSender() {
  }

  /**
   * Send the test status to ElasticSearch.
   * @param testInformation Contains the test status to be sent to ElasticSearch
   */
  public static void send(final TestInformation testInformation) {

    try {
      logger.info(OM.writeValueAsString(testInformation));
    } catch (Exception e) {
      logger.error(e);
    }
  }
}
