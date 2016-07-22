package org.wso2.ds.ui.integration.test.dashboard;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.testng.annotations.*;
import org.wso2.carbon.automation.engine.context.TestUserMode;
import org.wso2.carbon.integration.common.utils.exceptions.AutomationUtilException;
import org.wso2.ds.ui.integration.util.DSUIIntegrationTest;
import org.wso2.ds.ui.integration.util.DSWebDriver;

import javax.xml.xpath.XPathExpressionException;
import java.awt.*;
import java.io.IOException;
import java.net.MalformedURLException;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * To test the allowing primitive edit operations in view mode
 */
public class EditInViewModeTest extends DSUIIntegrationTest{
    private static final String DASHBOARD_TITLE = "editinviewdashboard";
    private static final String USERNAME_VIEWER = "vieweredit";
    private static final String PASSWORD_VIEWER = "vieweredit";

    /**
     * Initializes the class.
     *
     * @param userMode user mode
     */
    @Factory(dataProvider = "userMode")
    public EditInViewModeTest(TestUserMode userMode) {
        super(userMode);
    }

    /**
     * Provides user modes.
     *
     * @return user modes
     */
    @DataProvider(name = "userMode")
    public static Object[][] userModeProvider() {
        return new Object[][] { { TestUserMode.SUPER_TENANT_ADMIN } };
    }

    /**
     * Setup the testing environment.
     *
     * @throws XPathExpressionException
     * @throws IOException
     * @throws AutomationUtilException
     */
    @BeforeClass(alwaysRun = true)
    public void setUp() throws AutomationUtilException, XPathExpressionException, IOException {
        login(getCurrentUsername(), getCurrentPassword());
        addDashBoard(DASHBOARD_TITLE, "This is a test dashboard");
        loginToAdminConsole(getCurrentUsername(), getCurrentPassword());
        addUser(USERNAME_VIEWER, PASSWORD_VIEWER, PASSWORD_VIEWER);
        assignInternalRoleToUser(DASHBOARD_TITLE + "-viewer", new String[] { USERNAME_VIEWER});
        addLoginRole(USERNAME_VIEWER);
    }

    /**
     * Clean up after running tests.
     *
     * @throws XPathExpressionException
     * @throws MalformedURLException
     */
    @AfterClass(alwaysRun = true)
    public void tearDown() throws XPathExpressionException, MalformedURLException {
        logout();
        getDriver().quit();
    }

    /**
     * Checks for anon view
     *
     * @throws MalformedURLException
     * @throws XPathExpressionException
     */
    @Test(groups = "wso2.ds.dashboard", priority =0, description = "Checking the creation of anon view")
    public void testDeleteGadgetInViewMode() throws MalformedURLException, XPathExpressionException,
            InterruptedException {
        // Create a dashboard and add some gadgets and make the dashboard as personalizable one
        redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-edit")).click();
        String[][] gadgetMappings = { { "usa-map", "b" }, { "publisher", "c" } };
        String script = generateAddGadgetScript(gadgetMappings);
        getDriver().navigate().refresh();
        selectPane("gadgets");
        Thread.sleep(2000);
        getDriver().executeScript(script);
        Thread.sleep(2000);
        allowPersonalizeDashboard();
        Thread.sleep(2000);
        logout();
        login(USERNAME_VIEWER, PASSWORD_VIEWER);
        getDriver().findElement(By.id(DASHBOARD_TITLE)).findElement(By.cssSelector(".ues-view")).click();
        pushWindow();
        assertTrue(getDriver().isElementPresent(By.id("usa-map-0")),
                "usa-map gadget is not displayed in the " + "view mode");
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")),
                "publisher gadget is not displayed in the " + "view mode");
        getDriver().findElement(By.cssSelector("a.dropdown")).click();
        getDriver().findElement(By.id("edit-view-toggler")).click();
        assertTrue(getDriver().isElementPresent(By.cssSelector("#usa-map-0 .ues-trash-handle")),
                "Gadgets cannot be" + "deleted as delete button is not visible");
        assertTrue(getDriver().isElementPresent(By.cssSelector("#publisher-0 .ues-trash-handle")),
                "Gadgets cannot be" + "deleted as delete button is not visible");
        getDriver().findElement(By.cssSelector("#usa-map-0 .ues-trash-handle")).click();
        getDriver().findElement(By.id("btn-delete")).click();
        Thread.sleep(2000);
        assertFalse(getDriver().isElementPresent(By.id("usa-map-0")), "Gadget is not deleted in view mode");
        //getDriver().close();
        //popWindow();
    }

    @Test(groups = "wso2.ds.dashboard", priority = 2, description = "test dashboard restore option ")
    public void testRestoreGadget()throws MalformedURLException, XPathExpressionException,
            InterruptedException {
       // redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
       // getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
       // pushWindow();
        getDriver().findElement(By.cssSelector("a.dropdown")).click();
        getDriver().findElement(By.id("edit-view-toggler")).click();
        getDriver().findElement(By.xpath("//span[@id='landing']/i")).click();
        assertTrue(getDriver().isElementPresent(By.id("usa-map-0")), "Gadget is not restored in view mode");
    }

    @Test(groups = "wso2.ds.dashboard", priority = 1, description = "test dashboard draggable option")
    public void testResizeGadgetInViewMode()throws MalformedURLException, XPathExpressionException,
            InterruptedException {
        //redirectToLocation(DS_HOME_CONTEXT, DS_DASHBOARDS_CONTEXT);
        //getDriver().findElement(By.cssSelector("#" + DASHBOARD_TITLE + " a.ues-view")).click();
        //pushWindow();
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")),
                "publisher gadget is not displayed in the " + "view mode");
        getDriver().findElement(By.cssSelector("a.dropdown")).click();
        getDriver().findElement(By.id("edit-view-toggler")).click();
        WebElement element = getDriver().findElement(By.cssSelector(".gadget-heading"));
        WebElement target = getDriver().findElement(By.xpath("//div[@data-id='a']"));

        (new Actions(getDriver())).dragAndDrop(element, target).perform();
        assertTrue(getDriver().isElementPresent(By.id("publisher-0")),
                "publisher gadget is not displayed in the " + "view mode");
        String datax = getDriver().findElement(By.cssSelector(".grid-stack-item")).getAttribute("data-gs-x");
        assertEquals("0",datax,"Gadget is not draggable");
    }


}
