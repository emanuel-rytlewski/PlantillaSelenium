package pageobjects.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import pageobjects.base.AbstractComponent;

public class Footer extends AbstractComponent {
    //Selectors
    private static final By footerLogoAfiliadosBy = By.id("");
    private static final By footerAfiliadosTextBy = By.id("");

    public Footer(WebElement container) {
        super(container);
    }
    @Override
    protected void isLoaded() throws Error {
        try {
            // verify that each element is displayed before continuing
            container.findElement(footerLogoAfiliadosBy);
            logger.debug("Logo is displayed");
            container.findElement(footerAfiliadosTextBy);
            logger.debug("Footer text");
        } catch (Exception e) {
            throwNotLoadedException("The footer no search component was not loaded correctly", e);
        }

        logger.debug("Header component was loaded correctly");

    }

}
