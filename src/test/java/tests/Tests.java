package tests;

import base.TestBase;
import org.testng.Assert;
import org.testng.annotations.Test;
import pageobjects.pages.HomePage;

@Test(groups = {"full-regression"})
public class Tests extends TestBase {

 /**
  * Open home page.
  */
 @Test(
   groups = {"HomePgae"},
   description = "Go to home page",
   enabled = true
   //retryAnalyzer = TestBase.RetryAnalyzer.class
 )
 public void OpenHomePage() {
  //Act
  HomePage homePage = new HomePage();
  homePage.get();
 }
}
