package utils;

import static driver.DriverBase.getDriver;


import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.service.ExtentTestManager;
import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Utility class to log info and screenshots in the test report.
 */
public final class TestReporter {

  /**
   * The logger.
   */
  private static final Logger logger = LogManager.getLogger(TestReporter.class);

  private static final int MAX_LENGTH_FILENAME = 15;
  private static final RandomStringGenerator filenameGenerator =
          new RandomStringGenerator(MAX_LENGTH_FILENAME, new SecureRandom(),
                  RandomStringGenerator.LOWERCASE);
  private static final int MAX_RETRY_COUNT = 2;

  /**
   * Keeps the number of the last message displayed on the test report log of each test. Useful
   * for tracking metrics on where in the scenario the test failed.
   */
  private static final ThreadLocal<Integer> threadStepNumber = ThreadLocal.withInitial(() -> 1);

  private static int retryCount;

  /**
   * Private constructor to hide the implicit one.
   */
  private TestReporter() {

  }

  /**
   * Resets the step counter to 1.
   */
  public static void resetStepCounter() {
    logger.debug("The step counter was reset to 1");
    threadStepNumber.set(1);
  }

  /**
   * Gets the number of the current step to be logged.
   */
  public static int getCurrentStepNumber() {
    return threadStepNumber.get();
  }

  /**
   * Sets the current retry counter.
   *
   * @param count integer
   */
  public static void setRetryCount(int count) {
    logger.debug("The retry count value is {}", count);
    retryCount = count;
  }

  /**
   * Takes a screenshot and adds it to ExtentReport.
   */
  public static void addScreenshotToReport(String message) {
    logger.debug(message);
    WebDriver webDriver = getDriver();

    if (retryCount == MAX_RETRY_COUNT) {
      logger.debug("Taking screenshot");
      File scrFile = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.FILE);
      String projectPath = System.getProperty("user.dir");
      logger.debug("Project path: {}", projectPath);
      String extentReportPath = projectPath + "/build/extent/HtmlReport/";
      String fileName = filenameGenerator.nextString() + ".jpeg";
      String fullFilenamePath = extentReportPath + fileName;
      logger.debug("Full path screenshot file name: {}", fullFilenamePath);

      try {
        FileUtils.copyFile(scrFile, new File(fullFilenamePath));
      } catch (IOException e) {
        logger.error("Exception creating the screenshot file", e);
        return;
      }

      int currentStep = threadStepNumber.get();
      String messageWithStepNumber = Integer.toString(currentStep) + " - " + message;
      currentStep++;
      threadStepNumber.set(currentStep);
      ExtentTestManager.getTest().log(Status.INFO, messageWithStepNumber
                      + " ------ Screenshot: ",
              MediaEntityBuilder.createScreenCaptureFromPath(fileName, fileName).build());
    } else {
      logger.info("Adding info to report without screenshot");
      addInfoToReport(message);
    }
  }

  /**
   * Logs info to the Extent test report and to Log4j2.
   */
  public static void addInfoToReport(String message) {
    String messageWithStepNumber = addMessageToReport(message);
    ExtentTestManager.getTest().log(Status.INFO, messageWithStepNumber);
  }

  /**
   * Logs error to the Extent test report and to Log4j2.
   */
  public static void addErrorToReport(String message) {
    String messageWithStepNumber = addMessageToReport(message);
    ExtentTestManager.getTest().log(Status.FAIL, messageWithStepNumber);
  }

  @NotNull
  private static String addMessageToReport(String message) {
    logger.debug(message);
    int currentStep = threadStepNumber.get();
    String messageWithStepNumber = currentStep + " - " + message;
    currentStep++;
    threadStepNumber.set(currentStep);
    return messageWithStepNumber;
  }
}
