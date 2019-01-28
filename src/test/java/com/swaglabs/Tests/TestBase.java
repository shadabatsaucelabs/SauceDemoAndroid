package com.swaglabs.Tests;

import org.testng.annotations.AfterMethod;
import io.appium.java_client.android.AndroidDriver;
import org.apache.xpath.operations.Bool;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.UnexpectedException;

import com.saucelabs.saucerest.SauceREST;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.util.Collections;

import java.util.HashMap;
import java.util.Map;

/**
 * Simple TestNG test which demonstrates being instantiated via a DataProvider in order to supply multiple browser combinations.
 *
 * @author Neil Manvar
 */
public class TestBase {

    private String sauceSeleniumURI = "@ondemand.saucelabs.com:443";
    private String buildTag = System.getenv("BUILD_TAG");
    private String username = System.getenv("SAUCE_USERNAME");
    private String accesskey = System.getenv("SAUCE_ACCESS_KEY");
    //private String testobjectAccessKey = System.getenv("TO_ACCESS_KEY");
    private String testobjectAccessKey = "01601147FC394E60A3778B6F2D41D044";
    private SauceREST sauceRESTClient = new SauceREST(username, accesskey);

    /**
     * ThreadLocal variable which contains the  {@link AndroidDriver} instance which is used to perform browser interactions with.
     */
    private ThreadLocal<AndroidDriver> androidDriver = new ThreadLocal<AndroidDriver>();

    /**
     * ThreadLocal variable which contains the Sauce Job Id.
     */
    private ThreadLocal<String> sessionId = new ThreadLocal<String>();

    /**
     * DataProvider that explicitly sets the browser combinations to be used.
     *
     * @param testMethod
     * @return Two dimensional array of objects with browser, version, and platform information
     */
    @DataProvider(name = "hardCodedBrowsers", parallel = true)
    public static Object[][] sauceBrowserDataProvider(Method testMethod) {
        return new Object[][]{
                //new Object[]{"Android", "Android Emulator", "5.0", "1.6.3", "portrait"},
                new Object[]{"Android", "Android Emulator", "", "1.8.0", "portrait"},
                //new Object[]{"Android", "Samsung Galaxy S4 Emulator", "4.4", "1.6.3", "portrait"},
                new Object[]{"Android", "Samsung Galaxy S9 WQHD GoogleAPI Emulator", "", "1.8.0", "portrait"},
                new Object[]{"TestObject", "Samsung Galaxy S7", "", "1.8.0", ""},
                new Object[]{"TestObject", "Samsung_Galaxy_S5_real", "", "1.8.0", ""}
        };
    }

    /**
     * @return the {@link AndroidDriver} for the current thread
     */
    public AndroidDriver getAndroidDriver() {
        return androidDriver.get();
    }

    /**
     * @return the session id for the current thread
     */
    public String getSessionId() {
        return sessionId.get();
    }

    /**
     * Constructs a new {@link AndroidDriver} instance which is configured to use the capabilities defined by the browser,
     * version and os parameters, and which is configured to run against ondemand.saucelabs.com, using
     * the username and access key defined above.
     *
     * @param platformName      name of the platformName. (Android, iOS, etc.)
     * @param deviceName        name of the device
     * @param platformVersion   Os version of the device
     * @param appiumVersion     appium version
     * @param deviceOrientation device orientation
     * @return
     * @throws MalformedURLException if an error occurs parsing the url
     */
    protected void createDriver(
            String platformName,
            String deviceName,
            String platformVersion,
            String appiumVersion,
            String deviceOrientation,
            String methodName)
            throws MalformedURLException, UnexpectedException {
        DesiredCapabilities capabilities = new DesiredCapabilities();
        String gridEndpoint;

        if (platformName.equals("TestObject")) {
            capabilities.setCapability("testobject_device", deviceName);
            capabilities.setCapability("testobject_api_key", testobjectAccessKey);
            capabilities.setCapability("testobject_appium_version", appiumVersion);
            capabilities.setCapability("testobject_test_name", methodName);
            gridEndpoint = "https://us1.appium.testobject.com/wd/hub";
            //gridEndpoint = "https://appium.testobject.com:443/wd/hub";
        } else { // running on Sauce
            capabilities.setCapability("platformName", platformName);
            capabilities.setCapability("platformVersion", platformVersion);
            capabilities.setCapability("deviceName", deviceName);
            capabilities.setCapability("deviceOrientation", deviceOrientation);
            capabilities.setCapability("appiumVersion", appiumVersion);
            capabilities.setCapability("name", methodName);

            if (buildTag != null) {
                capabilities.setCapability("build", buildTag);
            }
            String app = "https://github.com/shadabatsaucelabs/SwagLabsAndroidDemoApp/blob/master/app-debug.apk?raw=true";
            //String app = "https://github.com/saucelabs-sample-test-frameworks/GuineaPig-Sample-App/blob/master/android/GuineaPigApp-debug.apk?raw=true";
            capabilities.setCapability("app", app);

            gridEndpoint = "https://" + username + ":" + accesskey + sauceSeleniumURI + "/wd/hub";
        }

        AndroidDriver driver =  new AndroidDriver(
                new URL(gridEndpoint),
                capabilities);

        // Launch remote browser and set it as the current thread
        androidDriver.set(driver);
    }

    /**
     * Method that gets invoked after test.
     * Dumps browser log and
     * Closes the driver
     */
    @AfterMethod
	
    public void tearDown(ITestResult result) throws Exception {
        String id = ((RemoteWebDriver) getAndroidDriver()).getSessionId().toString();
        Boolean status = result.isSuccess();
        Boolean isTOTest = getAndroidDriver().getCapabilities().getCapability("testobject_device") != null;

        if (isTOTest) {
            // TestObject REST API
            Client client = ClientBuilder.newClient();
            WebTarget resource = client.target("https://app.testobject.com/api/rest/appium/v1/");
            resource.path("session")
                    .path(id)
                    .path("test")
                    .request(new MediaType[]{MediaType.APPLICATION_JSON_TYPE}).
                    put(Entity.json(Collections.singletonMap("passed", status)));
        } else { // test was run on Sauce
            // Sauce REST API (updateJob)
            Map<String, Object> updates = new HashMap<String, Object>();
            updates.put("passed", status);
            sauceRESTClient.updateJobInfo(id, updates);
        }

        // driver.quit(). TO must issue REST API before quit, before or after on Sauce.
        getAndroidDriver().quit();
    }
    
    protected void annotate(String text) {
    	
        ((JavascriptExecutor) androidDriver.get()).executeScript("sauce:context=" + text);
    }
}
