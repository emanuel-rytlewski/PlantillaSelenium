package pageobjects.base;
import java.time.Clock;
import org.openqa.selenium.WebElement;

public abstract class AbstractComponent extends AbstractPageObject {
  protected static final int TIMEOUT_TO_LOAD_COMPONENT = 10;

  /**
   * Container of the component.
   */
  protected WebElement container;

  /**
   * Initializes the component with the container of the component.
   * @param container the WebElement that contains the whole component in the web page.
   */
  protected AbstractComponent(WebElement container) {
      super(Clock.systemDefaultZone(), TIMEOUT_TO_LOAD_COMPONENT);
      this.container = container;
  }


  /**
   * Gets the container of the component.
   * @return the WebElement container of the component
   */
  public WebElement getContainer() {
      return container;
  }
}
