package testing;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;

import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import net.sourceforge.tess4j.TesseractException;

import java.sql.Timestamp;
import java.util.ArrayList;

import java.util.Random;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/*
 * During the first execution, the bot will load cgi website, 
 * put username, password and open a new tab for you.
 * In the new tab you need to manually browse to Whatsapp Web.
 * Login to whatsapp web and click on the group/Individual that you want to send these bot updates to.
 * After you click, the bot will take you back to the cgi tab, from there you need to validate captcha.
 * After logging in to cgi, bot will start looking for slots, till the account gets frozen.
 */

public class SlotChecker {

//--------------------------------Editable Section Start-------------------------------------//	

	// Set botID (Must be unique for each instance of Slotter otherwise sessions
	// won't work)
	static int botID = 2;

	// Set Username and Password for all 3 dummies
	// It is recommended that you login sequentially to each
	static String userName1 = "saad@user.com";
	static String userName2 = "wanpisupiece5@yaho.com";
	static String userName3 = "surjodey89@gmail.com";

	static String password1 = "p12345678";
	static String password2 = "fall2022";
	static String password3 = "STUDENT2022";

	/*
	 * Bot will refresh within a random range from totalIntervalMinimum to
	 * totalIntervalMaximum If you want exact value, i.e: 1 min, set both
	 * totalIntervalMaximum, and totalIntervalMinimum to 60000
	 * 
	 * The random interval time will be equally divided between 3 dummies. (tabs)
	 */
	static double totalIntervalMinimum = 40000; // Minimum time to reload in ms
	static double totalIntervalMaximum = 90000;// Maximum time to reload in ms

//--------------------------------Editable Section End--------------------------------------//	

	private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	static WebElement userElement;
	static WebElement passElement;
	static WebElement checkBoxElement;
	static WebDriver driverMain;
	static WebDriver driverTwo;
	static WebDriver driverThree;
	static WebDriverWait waitForTelegramLogin;
	static WebElement telegramAppInput;

	static WebDriverWait waitForTelegramGroup;
	static WebElement telegramSendButton;

	static String telegramInputFieldCSSLocator = "editable-message-text";
	static String sendButtonCSS = ".Button.send.default.secondary.round";

	static ArrayList<String> tabs;

	public static void main(String ar[]) throws InterruptedException, IOException, TesseractException,
			UnsupportedAudioFileException, LineUnavailableException {

		// Session Data Retrieval
		ChromeOptions options = new ChromeOptions();
		options.addArguments("user-data-dir=C:\\Users\\" + System.getenv("USERNAME")
				+ "\\AppData\\Local\\Google\\Chrome\\User Data\\Profile " + botID);

		options.addArguments("--disable-blink-features=AutomationControlled");
		ChromeOptions options2 = new ChromeOptions();
		driverMain = new ChromeDriver(options);
		options2.addArguments("--disable-blink-features=AutomationControlled");
		driverTwo = new ChromeDriver(options2);
		driverThree = new ChromeDriver(options2);

		driverMain.get("https://cgifederal.secure.force.com");
		driverTwo.get("https://cgifederal.secure.force.com");
		driverThree.get("https://cgifederal.secure.force.com");

		TypeCredentials(userName1, password1, driverMain);

		TypeCredentials(userName2, password2, driverTwo);
		TypeCredentials(userName3, password3, driverThree);

		((JavascriptExecutor) driverMain).executeScript("window.open()");
		tabs = new ArrayList<String>(driverMain.getWindowHandles());

		driverMain.switchTo().window(tabs.get(1));
		driverMain.get("https://web.telegram.org/");
		driverMain.get("https://web.telegram.org/z/#-642121129");
		driverMain.navigate().refresh();

		/*
		 * waitForTelegramGroup = new WebDriverWait(driverMain,
		 * Duration.ofSeconds(30000));
		 * waitForTelegramGroup.until(ExpectedConditions.presenceOfAllElementsLocatedBy(
		 * By.cssSelector(telegramGroupToClick)));
		 * 
		 * telegramGroup =
		 * driverMain.findElements(By.cssSelector(telegramGroupToClick)).get(0);
		 * telegramGroup.click();
		 */

		waitForTelegramLogin = new WebDriverWait(driverMain, Duration.ofSeconds(30000));
		waitForTelegramLogin.until(ExpectedConditions.presenceOfElementLocated(By.id(telegramInputFieldCSSLocator)));
		telegramAppInput = driverMain.findElement(By.id(telegramInputFieldCSSLocator));

		// Alarm Setup
		File audioFile = new File("Audio/Alarm.wav");
		AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
		AudioFormat format = audioStream.getFormat();
		DataLine.Info info = new DataLine.Info(Clip.class, format);
		Clip audioClip = (Clip) AudioSystem.getLine(info);
		audioClip.open(audioStream);

		SendMessageToWhatsapp(1, false, audioClip, driverMain);

		SendMessageToWhatsapp(2, false, audioClip, driverTwo);
		SendMessageToWhatsapp(3, false, audioClip, driverThree);

		long perTabSleepDuration;

		for (int i = 0; i <= 10000; i++) {
			Random randomInterval = new Random();
			perTabSleepDuration = (long) (totalIntervalMinimum
					+ (totalIntervalMaximum - totalIntervalMinimum) * randomInterval.nextDouble()) / 3;

			SendMessageToWhatsapp(1, true, audioClip, driverMain);
			Thread.sleep(perTabSleepDuration);

			SendMessageToWhatsapp(2, true, audioClip, driverTwo);
			Thread.sleep(perTabSleepDuration);

			SendMessageToWhatsapp(3, true, audioClip, driverThree);
			Thread.sleep(perTabSleepDuration);

		}
	}

	static void TypeCredentials(String user, String pass, WebDriver driver) {
		WebDriverWait waitForWhatsappLogin = new WebDriverWait(driver, Duration.ofSeconds(30000));
		waitForWhatsappLogin.until(ExpectedConditions
				.presenceOfElementLocated(By.id("loginPage:SiteTemplate:siteLogin:loginComponent:loginForm:username")));
		userElement = driver.findElement(By.id("loginPage:SiteTemplate:siteLogin:loginComponent:loginForm:username"));
		passElement = driver.findElement(By.id("loginPage:SiteTemplate:siteLogin:loginComponent:loginForm:password"));
		checkBoxElement = driver
				.findElement(By.name("loginPage:SiteTemplate:siteLogin:loginComponent:loginForm:j_id167"));

		checkBoxElement.click();
		userElement.sendKeys(user);
		passElement.sendKeys(pass);
	}

	static void SendMessageToWhatsapp(int windowID, boolean refreshPage, Clip audioClip, WebDriver driver) {
		if (driver == driverMain)
			driver.switchTo().window(tabs.get(0));
		if (refreshPage)
			driver.navigate().refresh();

		WebDriverWait waitForLogin = new WebDriverWait(driver, Duration.ofSeconds(30000));
		waitForLogin.until(ExpectedConditions.presenceOfElementLocated(By.id("ctl00_nav_side1")));

		List<WebElement> slotAvailableText = driver.findElements(By.cssSelector("[class='leftPanelText']"));

		String message = "";
		if (slotAvailableText.size() == 0) {
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			message = "#" + botID + "." + windowID + ": No Slot " + sdf.format(timestamp);
			System.out.println(message);

			if (audioClip.isRunning())
				audioClip.stop();
		} else if (slotAvailableText.size() == 1) {
			String text;
			text = slotAvailableText.get(0).getText();
			Timestamp timestamp = new Timestamp(System.currentTimeMillis());
			message = "#" + botID + "." + windowID + " " + text + " Recorded On: " + sdf.format(timestamp);
			message = message.toUpperCase();
			audioClip.start();
			audioClip.loop(100);
		}

		driverMain.switchTo().window(tabs.get(1));
		waitForTelegramLogin = new WebDriverWait(driverMain, Duration.ofSeconds(30000));
		waitForTelegramLogin.until(ExpectedConditions.presenceOfElementLocated(By.id(telegramInputFieldCSSLocator)));
		telegramAppInput = driverMain.findElement(By.id(telegramInputFieldCSSLocator));
		telegramAppInput.sendKeys(message);
		
		waitForTelegramLogin = new WebDriverWait(driverMain, Duration.ofSeconds(30000));
		waitForTelegramLogin.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(sendButtonCSS)));
		telegramSendButton = driverMain.findElement(By.cssSelector(sendButtonCSS));
		telegramSendButton.click();
		
		driverMain.switchTo().window(tabs.get(0));
	}
}
