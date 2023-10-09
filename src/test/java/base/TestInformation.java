package base;

import com.fasterxml.jackson.annotation.JsonProperty;
import driver.DriverBase;
import environment.EnvironmentConfig;
import java.util.concurrent.TimeUnit;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Defines the test data structure sent to ElasticSearch.
 */
public final class TestInformation {

  /**
   * The logger.
   */
  private static final Logger logger = LogManager.getLogger(TestInformation.class);

  @JsonProperty("testClass")
  private String testClass =  "";

  @JsonProperty("testName")
  private String testName =  "";

  @JsonProperty("testStatus")
  private String testStatus =  "";

  @JsonProperty("startingTime")
  private String startingTime =  "";

  @JsonProperty("operativeSystem")
  private final String operativeSystem =  System.getProperty("os.name").toUpperCase();

  @JsonProperty("browser")
  private final String browser;

  @JsonProperty("browserVersion")
  private final String browserVersion;

  @JsonProperty("javaVersion")
  private final String javaVersion =  System.getProperty("java.version");

  @JsonProperty("testDuration")
  private String testDuration =  "";

  @JsonProperty("testDurationMilliseconds")
  private long testDurationMilliseconds;

  @JsonProperty("jenkinsJobName")
  private final String jenkinsJobName =  System.getenv("JOB_NAME");

  @JsonProperty("buildNumber")
  private final String buildNumber =  System.getenv("BUILD_NUMBER");

  @JsonProperty("jenkinsJobUrl")
  private final String jenkinsJobUrl =  System.getenv("BUILD_URL");

  @JsonProperty("threads")
  private final String threads =  System.getProperty("threads", "1");

  @JsonProperty("gridUrl")
  private final String gridUrl =  System.getProperty("gridURL", "");


  @JsonProperty("domain")
  private final String domain =  System.getProperty("domain", "fr_FR");

  @JsonProperty("webNextGenUrl")
  private final String webNextGenUrl;

  @JsonProperty("sessionID")
  private final String sessionID;

  @JsonProperty("exception")
  private String exception = "";

  /**
   * Constructor.
   */
  public TestInformation() {
    browser = DriverBase.getDriver().getCapabilities().getBrowserName();
    browserVersion =  DriverBase.getDriver().getCapabilities()
            .getCapability("browserVersion").toString();
    webNextGenUrl = EnvironmentConfig.getUrl();
    sessionID = DriverBase.getDriver().getSessionId().toString();
    logger.debug("Jenkins job name: {}", jenkinsJobName);
    logger.debug("Jenkins job build number: {}", buildNumber);
    logger.debug("Jenkins job URL: {}", jenkinsJobUrl);
  }

  public void setStartingTime(String startingTime) {
    this.startingTime = startingTime;
    logger.debug("Test startingTime: {}", startingTime);
  }

  public void setException(String exception) {
    this.exception = exception;
    logger.debug("Test exception: {}", exception);
  }

  public void setTestStatus(String testStatus) {
    this.testStatus = testStatus;
    logger.debug("Test final status: {}", testStatus);
  }

  public void setTestClass(String testClass) {
    this.testClass = testClass;
    logger.debug("Test testClass: {}", testClass);
  }

  public void setTestName(String testName) {
    this.testName = testName;
    logger.debug("Test testName: {}", testName);
  }

  /**
   * Set the test duration.
   * @param milliseconds duration of the test in milliseconds
   */
  public void setTestDuration(long milliseconds) {
    testDuration = String.format("%d min, %d sec, %d ms",
            TimeUnit.MILLISECONDS.toMinutes(milliseconds),
            TimeUnit.MILLISECONDS.toSeconds(milliseconds)
                    - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(milliseconds)),
            TimeUnit.MILLISECONDS.toMillis(milliseconds)
                    - TimeUnit.SECONDS.toMillis(TimeUnit.MILLISECONDS.toSeconds(milliseconds))
    );
    testDurationMilliseconds = milliseconds;
    logger.debug("Test duration: {}", testDuration);
  }
}
