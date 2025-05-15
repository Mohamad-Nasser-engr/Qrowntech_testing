import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.nio.file.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

public class Dashboard_Test {
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static final Path statePath = Paths.get("state.json");

    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        try {
            if (Files.exists(statePath)) {
                System.out.println("✅ Using saved session...");
                context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
                page = context.newPage();
                page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");

                if (page.url().contains("/login")) {
                    System.out.println("❌ Session expired, please log in again.");
                    LoginHelper.loginAndSaveState(playwright, browser);
                    context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
                    page = context.newPage();
                }
            } else {
                System.out.println("🔁 No saved session found, please log in...");
                LoginHelper.loginAndSaveState(playwright, browser);
                context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
                page = context.newPage();
            }

            page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testDashboardInteractions() {
        // Main setup
        page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("documenttype Document Type")).click();
        page.locator("#floatingdoctypedec").click();
        page.getByText("QR GenerateDocument Archiving").click();
        assertThat(page.getByText("Field Required")).isVisible();
        page.locator("#floatingdoctypedec").fill("pdf");
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Document Archiving")).check();
        assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Metadata"))).isVisible();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("QR Generate")).uncheck();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("QR Generate")).check();

        // QR Setup
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("QR setup")).getByRole(AriaRole.LINK).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Authentication Validity*")).locator("span").click();
        page.locator(".cdk-overlay-backdrop").click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Authentication Validity*")).locator("span").click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("12 Months")).click();
        page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("QR Design QROWNTECH Design")).click();
        page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Standard Design")).click();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Pin Code")).check();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Show Authenticator Info")).check();

        // Color selections
        page.locator(".circle").first().click();
        page.locator("#ngx-colors-overlay").getByRole(AriaRole.IMG).click();
        page.locator("#ngx-colors-overlay path").nth(1).click();
        page.locator("div:nth-child(13) > .circle").click();
        page.locator("div:nth-child(7) > div > .color-picker > .app-color-picker > .preview > .preview-background > .circle").click();
        page.locator("div:nth-child(7) > .circle").click();
        page.locator("div:nth-child(10) > .circle").click();

        page.locator("#mat-select-value-13").click();
        page.getByText("All pages").click();

        // File uploads
        page.locator("label:has-text('Common Files') input[type='file']")
             .setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));
        page.locator("label:has-text('Upload Logo Inside QR') input[type='file']")
             .setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));

        // Communication Channels
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Communication Channels")).getByRole(AriaRole.LINK).click();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Email")).check();
        page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Email")).uncheck();

        // Document Metadata
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Metadata")).getByRole(AriaRole.LINK).click();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Row")).click();
        page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Key value")).fill("Metadata 1");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Row")).click();
        page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("Delete").setExact(true))
            .getByPlaceholder("Key value").fill("Metadata 2");
        page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("Metadata 1 Delete"))
            .getByRole(AriaRole.BUTTON).click();

        // Document Template
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Template")).getByRole(AriaRole.LINK).click();
        page.locator("label.qwd-viewer-doc-label input[type='file']")
             .setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));

        // Optional wait to observe
        page.waitForTimeout(5000);
    }

    @AfterAll
    static void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
