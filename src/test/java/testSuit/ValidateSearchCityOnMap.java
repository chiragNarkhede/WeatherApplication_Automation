package testSuit;

import org.apache.log4j.Logger;

import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;

import base.TestBase;
import pageObjects.LandingPage;
import pageObjects.WeatherReportPage;

import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import commonUtil.ApiOps;
import commonUtil.PropertyReader;
import commonUtil.ScreenShotUtil;
import junit.framework.Assert;
import model.WeatherReportResponse;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/* Verify City on Map.
 */
public class ValidateSearchCityOnMap extends TestBase{

	public WebDriver driver;
	boolean setupResult = true;
	boolean testResult = true;
	boolean tearDown = true;
	public String city ;
	LandingPage landingPage;
	WeatherReportPage reportPage;
	WebDriverWait wait;
	Logger logger = Logger.getLogger(this.getClass());

	@BeforeTest
	public void Setup() {

		logger.debug("Start Setup: ValidateSearchCityOnMap");
		try {
			
			PropertyReader.ReadFile();
			ApiOps.GetResponse();
			city = PropertyReader.getCity();
			logger.debug("Initialize Web Driver");
			driver = DriverInitializer();
			driver.get(PropertyReader.getUrl());
			driver.manage().window().maximize();
			wait = new WebDriverWait(driver, 5000);
			
			logger.debug("Initialising Object of Landing Page.");
			landingPage = PageFactory.initElements(driver, LandingPage.class);
			ScreenShotUtil.CaptureScreenShot(driver, true);
			String title = driver.getTitle();
			logger.debug("Getting title of Landing Page." + title);
			if (!title.contains("NDTV")) {
				logger.fatal("Title is not matching." + title);
				ScreenShotUtil.CaptureScreenShot(driver, false);
				setupResult = false;
			}
			logger.debug("Dismiss the Alert");
			System.out.println("Error");
			Thread.sleep(15000);
			//driver.switchTo().alert().accept();
			Alert alert = wait.until(ExpectedConditions.alertIsPresent());
			alert.dismiss();
			
			reportPage = PageFactory.initElements(driver, WeatherReportPage.class);
			if (landingPage.getSubMenu().isDisplayed())
			{
				landingPage.getSubMenu().click();
			}
			ScreenShotUtil.CaptureScreenShot(driver, true);
			landingPage.getWeatherOption().click();
			ScreenShotUtil.CaptureScreenShot(driver, true);
			if (!driver.getTitle().contains("WEATHER")) {
				setupResult = false;
				ScreenShotUtil.CaptureScreenShot(driver, false);
			}
			ScreenShotUtil.CaptureScreenShot(driver, true);
		} catch (Exception e) 
		{
			logger.fatal("Exception Occured" + e.getMessage() + e.getStackTrace());

		} finally 
		{
			logger.debug("End Setup: ValidateSearchCityOnMap");
		}

	}

	@Test()
	public void Test() {

		try {
			/*
			 * Step 1
			 */
			String selectCityContainer = reportPage.getSearchContainerTitle().getText();
			ScreenShotUtil.CaptureScreenShot(driver, true);
			Assert.assertEquals("Container is not present", testResult, selectCityContainer.contains("Pin your City"));

			/*
			 * Step 2
			 */
			logger.debug("Enter City into search box as" + city);
			reportPage.getSearchBox().sendKeys(city);
			ScreenShotUtil.CaptureScreenShot(driver, true);
			reportPage.getSearchBox().sendKeys(Keys.ENTER);
			;
			reportPage.getCheckBox(city).click();
			ScreenShotUtil.CaptureScreenShot(driver, true);
			Assert.assertEquals("Container is not present", testResult, selectCityContainer.contains("Pin your City"));

			/*
			 * Step 3
			 */
			logger.debug("Verify that Selected City is displayed on Map.");
			boolean cityOnMap = reportPage.getCityOnMap(city).isDisplayed();
			ScreenShotUtil.CaptureScreenShot(driver, true);
			Assert.assertEquals("Selected City is not displayed on Map", testResult, cityOnMap);

			/*
			 * Step 4
			 */

			logger.debug("Verify that Temperature is displayed on city");
			boolean tempPresent = reportPage.getDTemperature(city).isDisplayed();
			tempPresent &= reportPage.getFTemperature(city).isDisplayed();
			ScreenShotUtil.CaptureScreenShot(driver, true);
			Assert.assertEquals("Tempearature for city is not dispayed.", testResult, tempPresent);

			/*
			 * Step 5 ,6,7
			 */
			logger.debug("Verify Temperature form city container with popup temperature.");
			String degreeTemp = reportPage.getDTemperature(city).getText();
			String fahrenheitTemp = reportPage.getFTemperature(city).getText();

			logger.debug("Verify that after clicking on city popup displayed.");
			boolean popUpDisplayed = reportPage.getLeafPopUp().isDisplayed();
			ScreenShotUtil.CaptureScreenShot(driver, true);
			testResult &= popUpDisplayed;
			Assert.assertEquals("After clicking on city popup is not displayed.", "true", popUpDisplayed);

			logger.debug("Get temperature from the city pop up and compare with container temperature.");
			String dTempPopUp = reportPage.getPopUpData("3").getText();
			String fTempPoup = reportPage.getPopUpData("4").getText();
			boolean degreeCheck = dTempPopUp.contains(degreeTemp.substring(0, 1));
			boolean tempCheck = fTempPoup.contains(fahrenheitTemp.substring(0, 1));
			degreeCheck &= tempCheck;
			Assert.assertEquals("Temperature from the city pop up container temperature matches.", "true", degreeCheck);

		} catch (Exception e) {
			logger.fatal("Exception Occured" + e.getMessage() + e.getStackTrace());

		} finally {
			logger.debug("END: Test");

		}
	}

	@AfterTest
	public void CleanUp() 
	{
		driver.quit();
	}

}