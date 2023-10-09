package driver;

import environment.EnvironmentConfig;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

public class DriverBase {

  /**
   * Logger.
   */
  protected static final Logger logger = LogManager.getLogger(DriverBase.class);

  private static final String THREAD_ID = "threadId";

  /**
   * List of safe WebDriverThreads to keep each WebDriver in a different thread.
   */
  private static final List<DriverFactory> webDriverThreadPool = Collections
          .synchronizedList(new ArrayList<>());

  /**
   * ThreadLocal variable to store individually each driver in each thread.
   */
  private static ThreadLocal<DriverFactory> driverThread;

  /**
   * Protected constructor.
   */
  protected DriverBase() {
  }

  /**
   * Instantiate the DriverFactory object at the beginning of the test suite, and store it in
   * a ThreadLocal variable to keep each DriverFactory and each WebDriver isolated in each
   * thread.
   */
  @BeforeSuite(alwaysRun = true)
  public static void startSuite() {

    ThreadContext.put(THREAD_ID, Thread.currentThread().getName());
    EnvironmentConfig.initializeEnvironment();

    // Instantiates and stores the WebDriver into the ThreadLocal variable
    driverThread = ThreadLocal.withInitial(() -> {
      /*
       The following instruction is used to tell log4j which file the log will be written to
       depending on the thread name. The thread name is used as the value threadId that is used as
       the routing key in the routing appender. Check the log4j2.xml config file.
       For more info check here:
       https://stackoverflow.com/questions/8355847/how-to-log-multiple-threads-in-different-log-files
       http://logging.apache.org/log4j/2.x/faq.html#separate_log_files
      */
      ThreadContext.put(THREAD_ID, Thread.currentThread().getName());

      logger.trace("Instantiate WebDriver");
      DriverFactory webDriverThread = new DriverFactory();
      webDriverThreadPool.add(webDriverThread);
      return webDriverThread;
    });
  }

  /**
   * Gets the WebDriver from the DriverFactory using a singleton pattern.
   *
   * @return the instantiated WebDriver
   */
  public static RemoteWebDriver getDriver() {
    logger.trace("Get the WebDriver");

    // Gets the WebDriver stored in the ThreadLocal variable
    return driverThread.get().getDriver();
  }

  /**
   * Gets the DriverTYpe from DriverFactory.
   *
   * @return BrowserType
   */
  public static BrowserType getBrowserType() {
    logger.trace("Get the BrowserType");
    return driverThread.get().getBrowserType();
  }

  /**
   * Quit the browser between tests.
   */
  @AfterMethod(alwaysRun = true)
  public static void quitWebDriver() {
    try {
      logger.trace("Quit WebDriver");
      driverThread.get().quitDriver();
    } catch (Exception ex) {
      logger.error("Unable to quit WebDriver", ex);
    }
  }

  /**
   * Safely quits all the WebDrivers in the ThreadPool.
   */
  @AfterSuite(alwaysRun = true)
  public static void finishSuite() {
    /*
     The following instruction is used to tell log4j which file the log will be written to depending
     on the thread name. The thread name is used as the value threadId that is used as the routing
     key in the routing appender. Check the log4j2.xml config file.
     For more info check here:
     https://stackoverflow.com/questions/8355847/how-to-log-multiple-threads-in-different-log-files
     http://logging.apache.org/log4j/2.x/faq.html#separate_log_files
    */
    ThreadContext.put(THREAD_ID, Thread.currentThread().getName());

    int index = 0;
    for (DriverFactory webDriverThread : webDriverThreadPool) {
      logger.trace("Quit WebDriver {}", index);
      index++;
      webDriverThread.quitDriver();
    }
    logger.trace("Remove WebDriver from ThreadLocal");
    driverThread.remove();
  }
}
