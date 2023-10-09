package pageobjects.pages;

import environment.EnvironmentConfig;
import java.net.MalformedURLException;
import java.net.URL;

import org.openqa.selenium.By;
import pageobjects.components.Header;
import pageobjects.base.AbstractPage;
import pageobjects.components.Header;
import utils.TestReporter;

public class HomePage extends AbstractPage {

    // Selectors
    private URL homePageUrl;
    private static final By headerBy = By.cssSelector("[id='__next']>div>div");
    private static final By footerBy = By.cssSelector("[id='__next']>div>div+div+div");
    private boolean comingFromAnotherPage;

    private static final long TIME_OUT = 15 ;
    //
    private Header header;

    /**
     * Constructor of the HomePage class.
     */
    public HomePage() {
        super();
        logger.debug("Initializing Home Page");
        try {
            homePageUrl = new URL(EnvironmentConfig.getUrl());
        } catch (MalformedURLException e) {
            logger.error("The URL format '{}' is not correct.", EnvironmentConfig.getUrl(), e);
            throwNotLoadedException("The home page could not be opened", e);
        }
        logger.debug("Set home page url to: '{}'", homePageUrl);
    }







    /**
     * Constructor of the HomePage class when passing the home page url as argument.
     */
    public HomePage(String url) {
        super();
        logger.debug("Initializing Home Page");
        try {
            homePageUrl = new URL(url);
        } catch (MalformedURLException e) {
            logger.error("The URL format '{}' is not correct.", url, e);
            throwNotLoadedException("The home page could not be opened", e);
        }
        logger.debug("Set home page url to: '{}'", homePageUrl);
    }

    /**
     * Constructor of the HomePage when coming from another page.
     *
     * @param comingFromAnotherPage Boolean
     */
    public HomePage(boolean comingFromAnotherPage) {
        super();
        logger.debug("Initializing HomePage when coming from another page");
        this.comingFromAnotherPage = comingFromAnotherPage;

    }

    /**
     * load() is called when HomePage.get() is called. Opens the home page defined by the
     * configuration selected (environment, domain) or passed as -DwngURL argument.
     */
    @Override
    protected void load() {
        if (!comingFromAnotherPage) {
            TestReporter.addInfoToReport("Opening Home Page: " + homePageUrl);
            driver.get(homePageUrl.toString());
            driver.navigate().refresh();
        } else {
            TestReporter.addInfoToReport("Opening Home Page when coming from another page");
        }

    }

    /**
     * isLoaded() is called when HomePage.get() is called. Defines when the page has finished
     * loading. It must verify that the components of this page have also finished loading before
     * continuing.
     */
    @Override
    protected void isLoaded() throws Error {
        try {
            // verify that each element is displayed before continuing
            driver.findElement(headerBy);
            logger.debug("Header container is displayed");
            driver.findElement(footerBy);
            logger.debug("Footer container is displayed");
        } catch (Exception e) {
            throwNotLoadedException("The Home page was not loaded correctly", e);
        }
    }

}
