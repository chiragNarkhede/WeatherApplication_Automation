package baseClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;

/* This class is used for WebDriver
 * initialization
 */
public class TestBase {

	public WebDriver driver;
	public Properties prop;
	public String url;
		
	
	/* Initialize the web Driver.
	 * @return webDriver
	 */	
		public WebDriver DriverInitializer() throws IOException
		{
			
			 prop= new Properties();
			 FileInputStream fileInput =new FileInputStream(System.getProperty("user.dir")+"\\InputFiles\\data.properties");
			 prop.load(fileInput);
			 String browserName = prop.getProperty("browser");
			 url = prop.getProperty("applicationUrl");
				 
			 if(browserName.equals("chrome"))
			 {
				String chromePath = prop.getProperty("chromDriverPath"); 
				System.setProperty("webdriver.chrome.driver",System.getProperty("user.dir")+chromePath);
				
				 driver= new ChromeDriver();
			 }
			 else if (browserName.equals("firefox"))	
			 {
				String firefoxPath = prop.getProperty("firefoxDriverPath"); 
				System.setProperty("webdriver.gecko.driver", System.getProperty("user.dir")+firefoxPath);
				driver= new FirefoxDriver();
			 }
		
			driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
			
			return driver;
	}
}

