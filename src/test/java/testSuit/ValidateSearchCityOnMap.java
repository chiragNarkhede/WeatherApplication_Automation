package testSuit;
import baseClass.TestBase;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.openqa.selenium.Alert;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Action;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.AfterTest;
import org.testng.annotations.Test;
import pageObjects.LandingPage;
import pageObjects.WeatherReportPage;

import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import commonUtil.ScreenShotUtil;
import junit.framework.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
public class ValidateSearchCityOnMap extends TestBase {
	public WebDriver driver;
	boolean setupResult =true;
	boolean testResult =true;
	boolean tearDown =true;
	public String city = "Pune";
	LandingPage landingPage;
	WeatherReportPage reportPage;
	Logger logger =Logger.getLogger(this.getClass()); 
	
	@BeforeTest
	
	public void Setup() {
	
		logger.debug("Start Setup: ValidateSearchCityOnMap");
		try 
		{
			logger.debug("Initialize Web Driver");
			driver = DriverInitializer();
			driver.get(url);
			 driver.manage().window().maximize();
			 logger.debug("Initialising Object of Landing Page.");
			 landingPage = PageFactory.initElements(driver, LandingPage.class);
			 CaptureScreenShot(driver,true);
			 String title = driver.getTitle();
			 logger.debug("Getting title of Landing Page."+title);
			 if(!title.contains("NDTV"))
			 {
				 logger.fatal("Title is not matching."+title);
				 CaptureScreenShot(driver,false);
				 setupResult = false;
			 }
			 logger.debug("Dismiss the Alert");
			 //Alert alert = driver.switchTo().alert();
			 //Alert alert = (Alert) wait.until(ExpectedConditions.visibilityOf(landingPage.getNotifacation()));
			 //alert.accept();
			 //driver.switchTo().alert().dismiss();
			 
			 reportPage = PageFactory.initElements(driver, WeatherReportPage.class);
			 if(landingPage.getSubMenu().isDisplayed())
			 {
				 landingPage.getSubMenu().click();
			 }
			 CaptureScreenShot(driver,true);
			 landingPage.getWeatherOption().click();
			 CaptureScreenShot(driver,true);
			 if(!driver.getTitle().contains("WEATHER"))
			 {
				 setupResult = false;
				 CaptureScreenShot(driver,false);
			 }
			 CaptureScreenShot(driver,true);
		}catch(Exception e)
		{
			logger.fatal("Exception Occured"+e.getMessage()+e.getStackTrace());
			
		}
		finally
		{
			logger.debug("End Setup: ValidateSearchCityOnMap");
		}
		
	}
	
	@Test
	public void Test()
			
			{
			try 
			{
				String selectCityContainer = reportPage.getSearchContainerTitle().getText();
				CaptureScreenShot(driver,true);
				if(!selectCityContainer.contains("Pin your City"))
				{
					 CaptureScreenShot(driver,false);
					 testResult=false;
					
				}

				/*
				 * Step 
				 */
				logger.debug("Enter City into search box as"+city);
				reportPage.getSearchBox().sendKeys(city);
				CaptureScreenShot(driver,true);
				reportPage.getSearchBox().sendKeys(Keys.ENTER);;
				reportPage.getCheckBox(city).click();
				logger.debug("Search City on Map."+city);
				CaptureScreenShot(driver,true);
			
				/*
				 * Step 
				 */
				logger.debug("Verify that Selected City is displayed on Map.");
				boolean cityOnMap = reportPage.getCityOnMap(city).isDisplayed();
				CaptureScreenShot(driver,true);
				Assert.assertEquals("Selected City is not displayed on Map", testResult, cityOnMap);
				/*
				 * Step 
				*/
				
				logger.debug("Verify that Temperature is displayed on city");
				boolean tempPresent = reportPage.getDTemperature(city).isDisplayed();
				tempPresent &=reportPage.getFTemperature(city).isDisplayed();
				CaptureScreenShot(driver,true);
				Assert.assertEquals("Tempearature for city is not dispayed.", testResult, tempPresent);
				
				/*
				 * Step 
				*/
				logger.debug("Verify Temperature form city container and compare with popup temperature.");
				String degreeTemp = reportPage.getDTemperature(city).getText();
				String fahrenheitTemp = reportPage.getFTemperature(city).getText();
			
				/*
				 * Step 
				*/
				logger.debug("Verify that after clicking on city popup displayed.");
				boolean popUpDisplayed = reportPage.getLeafPopUp().isDisplayed();
				CaptureScreenShot(driver,true);
				testResult&=popUpDisplayed;
				Assert.assertEquals("After clicking on city popup is not displayed.", "true", popUpDisplayed);
				
				/*
				 * Step 
				*/
				
				logger.debug("Get temperature from the city pop up and compare with container temperature.");
				String dTempPopUp =reportPage.getPopUpData("3").getText();
				String fTempPoup = reportPage.getPopUpData("4").getText();
				boolean degreeCheck = dTempPopUp.contains(degreeTemp.substring(0, 1));
				boolean tempCheck = fTempPoup.contains(fahrenheitTemp.substring(0, 1));
				degreeCheck&=tempCheck;
				Assert.assertEquals("Temperature from the city pop up container temperature matches.", "true", degreeCheck);
				
			}catch(Exception e)
			{
				logger.fatal("Exception Occured"+e.getMessage()+e.getStackTrace());
				
			}
			finally
			{
				logger.debug("END: Test");
				
			}
		}
	
	@AfterTest
	public void CleanUp() 
	{
		
		driver.quit();
	}

}