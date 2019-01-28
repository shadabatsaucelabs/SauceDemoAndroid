package com.swaglabs.Pages;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import io.appium.java_client.MobileDriver;
import io.appium.java_client.TouchAction;

public class LoginPage {

    
    
   // @FindBy(xpath = "//input[@placeholder='Username']")
    //private WebElement usernameTextBox;
    
    @FindBy(id = "test-Username")
    private WebElement usernameTextBox;
    
    @FindBy(id = "test-Password")
    private WebElement passwordTextBox;

    //@FindBy(xpath = "//input[@placeholder='Password']")
    //private WebElement passwordTextBox;
    
    @FindBy(id = "Heading1_1")
    private WebElement h1Text;

    @FindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-LOGIN\"]/android.widget.TextView")
    private WebElement loginButton; 
    
    @FindBy(xpath = "//android.view.ViewGroup[@content-desc=\"test-Error message\"]/android.widget.TextView[2]")
    private WebElement lockedOutMessage; 
    
    @FindBy(xpath = "//pre[@id='login_credentials']")
    private WebElement loginCredentials; 
    
  
  

    public WebDriver driver;
    //public static String url = "https://www.saucedemo.com/";

    /*public static LoginPage visitPage(WebDriver driver) {
    	LoginPage page = new LoginPage(driver);
        page.visitPage();
        return page;
    }*/

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        PageFactory.initElements(driver, this);
    }

    /*public void visitPage() {
        this.driver.get(url);
    }*/

    
    
    public InventoryPage enterCredentials(String username, String password) throws InterruptedException {
    	//Thread.sleep(1000);
    	usernameTextBox.sendKeys(username);
    	//Thread.sleep(1000);
    	passwordTextBox.sendKeys(password);
    	//Thread.sleep(4000);
    	//hideKeyboard();
    	loginButton.click();
        return PageFactory.initElements(driver, InventoryPage.class);
    }
    
    
    public boolean verifyLoginPage() {
    	WebDriverWait wait = new WebDriverWait(driver, 30);
    	wait.until(ExpectedConditions.visibilityOf(loginButton));
    	//System.out.println("Value is: "+loginButton.getText());
        return true;
    }
    
    public String verifyLockedOutMessage() {
    	WebDriverWait wait = new WebDriverWait(driver, 30);
    	wait.until(ExpectedConditions.visibilityOf(lockedOutMessage));
    	//System.out.println(lockedOutMessage.getText());
        return lockedOutMessage.getText();
    }
    
    /**
     * This method only work for this page and assumes the app supports keyboard hide on click-away.
     */
    public void hideKeyboard() {
        this.h1Text.click();
        //TouchAction touchAction=new TouchAction(driver);
        //touchAction.tap(797, 387).perform();
    }

}
