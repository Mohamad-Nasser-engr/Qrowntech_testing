import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;
import java.nio.file.*;
import com.microsoft.playwright.options.*;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;

public class Dashboard_Test {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;
    private Path statePath;
//
    @BeforeEach
    public void setUp() throws Exception {
        // Initialize Playwright and browser
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        // Define the path for the saved session
        statePath = Paths.get("state.json");

        // Load or create a new browser context based on session state
        if (Files.exists(statePath)) {
            System.out.println("✅ Using saved session...");
            // Load saved session from state.json
            context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
        } else {
            System.out.println("🔁 No saved session found, please log in...");
            // If no saved session, handle login and save state (mocking login in this example)
            LoginHelper.loginAndSaveState(playwright, browser);
            context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
        }

        // Create a new page in the browser context
        page = context.newPage();
    }

    @Test
    public void testSavedDashboard() {
        // Navigate to the dashboard page
        page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");

        // Example of checking page URL to ensure we're logged in
        if (page.url().contains("/login")) {
            System.out.println("❌ Session expired, please log in again.");
            // If session expired, handle login again (optional)
            LoginHelper.loginAndSaveState(playwright, browser);
            context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
            page = context.newPage();
            page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");
        }

        // Interact with the page and assert conditions
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("documenttype Document Type")).click();
        page.locator("#floatingdoctypedec").click();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Document Archiving")).check();
        assertTrue(page.locator("app-collapse").textContent().contains("Document Metadata"));
        assertTrue(page.getByText("Field Required").isVisible());

        page.locator("#floatingdoctypedec").click();
        page.locator("#floatingdoctypedec").fill("pdf");

        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("QR setup")).getByRole(AriaRole.LINK).click();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Pin Code")).check();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Show Authenticator Info")).check();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Authentication Validity*")).locator("span").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("6 Months")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("QR Design QROWNTECH Design")).click();
        //page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Standard Design")).click();

        page.locator("ngx-colors").first().click();
        page.locator("div:nth-child(13) > .circle").click();
        page.locator("ngx-colors").nth(1).click();
        page.locator("div:nth-child(19) > .circle").click();
        page.locator("div:nth-child(10) > .circle").click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("QR Stamped On Document* First")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("All pages")).click();

        // Handle file input for uploading files
        page.locator("label:has-text('Common Files') input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));
        page.locator("label:has-text('Upload Logo Inside QR') input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));

        // Document Template interaction
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Template")).getByRole(AriaRole.LINK).click();
        page.locator("label.qwd-viewer-doc-label input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));

        // You can add further assertions if needed
        assertNotNull(page.locator("label:has-text('Common Files') input[type='file']"));
    }

    @AfterEach
    public void tearDown() {
        // Wait before closing
        page.waitForTimeout(5000);

        // Close context and browser when done
        context.close();
        browser.close();
        playwright.close();
    }
}
