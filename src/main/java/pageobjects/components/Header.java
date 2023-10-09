package pageobjects.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pageobjects.base.AbstractComponent;

public class Header extends AbstractComponent {
    //Selectors
    private static final By HeaderLogoAfiliadosBy = By.id("");
    private static final By secureLogoAfiliadosBy = By.id("");

    public Header(WebElement container) {
        super(container);
    }
    @Override
    protected void isLoaded() throws Error {
        try {
            // verify that each element is displayed before continuing
            container.findElement(HeaderLogoAfiliadosBy);
            logger.debug("Logo is displayed");
            container.findElement(secureLogoAfiliadosBy);
            logger.debug("Logo web secure");
        } catch (Exception e) {
            throwNotLoadedException("The header no search component was not loaded correctly", e);
        }

        logger.debug("Header component was loaded correctly");

    }
}

