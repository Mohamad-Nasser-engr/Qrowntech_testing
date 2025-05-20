import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import com.microsoft.playwright.options.ScreenshotType;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;

public class BrowserConsoleTest {

    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static final Path statePath = Paths.get("state.json");
    
    static List<String> browserErrorLogs = new ArrayList<>();


    @BeforeAll
    static void setupAll() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

        try {
            if (Files.exists(statePath)) {
                System.out.println("✅ Using saved session...");
                context = browser.newContext(new Browser.NewContextOptions()
                        .setStorageStatePath(statePath)
                        .setViewportSize(1920, 1080));
            } else {
                System.out.println("🔁 No saved session found, please log in...");
                LoginHelper.loginAndSaveState(playwright, browser);
                context = browser.newContext(new Browser.NewContextOptions()
                        .setStorageStatePath(statePath)
                        .setViewportSize(1920, 1080));
            }

            page = context.newPage();

            // Setup console listener once on main page
            page.onConsoleMessage(msg -> {
                if ("error".equals(msg.type())) {
                    System.err.println("🚨 Console Error: " + msg.text());
                    browserErrorLogs.add("🚨 Console Error: " + msg.text());

                } else {
                    System.out.println("📋 Console [" + msg.type() + "]: " + msg.text());
                }
            });

            page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");

            if (page.url().contains("/login")) {
                System.out.println("❌ Session expired, please log in again.");
                LoginHelper.loginAndSaveState(playwright, browser);
                context = browser.newContext(new Browser.NewContextOptions()
                        .setStorageStatePath(statePath)
                        .setViewportSize(1920, 1080));
                page = context.newPage();

                // Re-attach listener to the new page
                page.onConsoleMessage(msg -> {
                    if ("error".equals(msg.type())) {
                        System.err.println("🚨 Console Error: " + msg.text());
                    } else {
                        System.out.println("📋 Console [" + msg.type() + "]: " + msg.text());
                    }
                });

                page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void testPrintConsoleMessages() {
        // Already navigated in @BeforeAll
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
		 
		  // background
			/*
			 * page.locator(".circle").first().click();
			 * page.locator("#ngx-colors-overlay").getByRole(AriaRole.IMG).click();
			 * page.locator("#ngx-colors-overlay path").nth(1).click();
			 * page.locator("div:nth-child(8) > .circle").click();
			 */
		  // foreground
			
			  page.locator("div:nth-child(7) > div > .color-picker > .app-color-picker > .preview > .preview-background > .circle").click(); 
			  page.locator("div:nth-child(4) > .circle").click();
			  page.locator("div:nth-child(9) > .circle").click();
			  
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
        .setInputFiles(Paths.get("C:\\Users\\user\\Desktop\\HAMOUDI LAU\\Spring 2023\\Strength of Material\\Chapter 6- problems.pdf"));
        assertThat(page.getByText("Exceeded File Size Limit:")).isVisible();
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("OK")).click();
        
        //page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("Delete")).click();
        
        page.locator("label.qwd-viewer-doc-label input[type='file']")
        .setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));
        page.waitForTimeout(2000);
        
        // Optional wait to observe//
        page.waitForTimeout(3000); // Give it time to log console messages
    }

    @AfterAll
    static void teardownAll() {
        try {
            // Check for browser errors before shutting down
            if (!browserErrorLogs.isEmpty()) {
                String errorMessage = String.join("\n", browserErrorLogs);
                Assertions.fail("Browser console errors detected:\n" + errorMessage);
            }
        } finally {
            context.close();
            browser.close();
            playwright.close();
        }
    }

}
