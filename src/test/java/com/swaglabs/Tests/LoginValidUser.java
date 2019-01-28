package com.swaglabs.Tests;

import org.openqa.selenium.InvalidElementStateException;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.AssertJUnit;
import org.testng.annotations.Test;

import com.swaglabs.Pages.InventoryPage;
import com.swaglabs.Pages.LoginPage;

import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.rmi.UnexpectedException;

/**
 * Created by Shadab Siddiqui on 01/27/19.
 */

public class LoginValidUser extends TestBase {

    /**
     * Runs a simple test verifying link can be followed.
     *
     * @throws InvalidElementStateException
     * @throws InterruptedException 
     */
    @Test(dataProvider = "hardCodedBrowsers")
    public void LoginValidUserTest(String platformName,
                               String deviceName,
                               String platformVersion,
                               String appiumVersion,
                               String deviceOrientation,
                               Method method)
            throws MalformedURLException, InvalidElementStateException, UnexpectedException, InterruptedException {

        //create webdriver session
        this.createDriver(platformName, deviceName, platformVersion, appiumVersion, deviceOrientation, method.getName());
        WebDriver driver = this.getAndroidDriver();

        LoginPage page = new LoginPage(driver);

       //this.annotate("Visiting Swag Labs Login page...");
       AssertJUnit.assertTrue(page.verifyLoginPage());
        
       //this.annotate("Greet Sign In To Swag Labs Page...");
       InventoryPage inventory = page.enterCredentials("standard_user", "secret_sauce");
        
       //this.annotate("View Product Inventory...");
       AssertJUnit.assertTrue(inventory.viewInventory().contains("Products"));
        
        
        
           
    }

}