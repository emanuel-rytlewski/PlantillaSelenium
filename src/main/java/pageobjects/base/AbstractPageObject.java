package pageobjects.base;

import java.time.Clock;

import customerros.PageObjectLoadingError;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.openqa.selenium.support.ui.SlowLoadableComponent;


public abstract class AbstractPageObject extends SlowLoadableComponent<AbstractPageObject> {

  /**
   * Logger.
   */
  protected static final Logger logger = LogManager.getLogger(AbstractPageObject.class);

  /**
   * Constructor of the AbstractPageObject class.
   *
   * @param clock Clock
   * @param timeOutInSeconds Integer
   */
  protected AbstractPageObject(Clock clock, int timeOutInSeconds) {
      super(clock, timeOutInSeconds);
  }

  /**
   * Logs and throws the exception caught when loading the component.
   * @param customMessage The custom message to write in the log.
   * @param e Exception caught when trying to load the component.
   */
  protected void throwNotLoadedException(String customMessage, @NotNull Exception e) {
      throw new PageObjectLoadingError(customMessage + "\n" + e.getMessage(), e.getCause());
  }

  /**
   * Logs and throws the exception caught when loading the component.
   * @param customMessage The custom message to write in the log.
   */
  protected void throwNotLoadedException(String customMessage) {
      throw new PageObjectLoadingError(customMessage);
  }



  @Override
  protected void load() {
  }

  @Override
  protected void isLoaded() throws Error {
  }
}
