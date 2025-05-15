import com.microsoft.playwright.*;
import java.nio.file.*;
import com.microsoft.playwright.options.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import java.util.*;

public class saved_dashboard_Test {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            // Launch the browser in non-headless mode to see the actions
            Browser browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));

            Path statePath = Paths.get("state.json");
            BrowserContext context;

            // Check if saved session exists
            if (Files.exists(statePath)) {
                System.out.println("✅ Using saved session...");
                // Load saved session from state.json
                context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
                Page page = context.newPage();

                // Navigate to a page that requires authentication (adjust URL as needed)
                page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard"); // Replace with your URL

                // Wait for the page to be fully loaded
                //page.waitForLoadState(Page.LoadState.DOMCONTENTLOADED);

                // Add any further checks or interactions here if needed
                if (page.url().contains("/login")) {
                    System.out.println("❌ Session expired, please log in again.");
                    // If session expired, handle login again (optional)
                    LoginHelper.loginAndSaveState(playwright, browser);
                    context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
                }
            } else {
                System.out.println("🔁 No saved session found, please log in...");
                // If no saved session, handle login and save state
                LoginHelper.loginAndSaveState(playwright, browser);
                context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath));
            }

            // Continue with your recorded interactions here
            // You can paste your recorded actions below

            Page page = context.newPage();
            page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard"); // Navigate to the desired page

            // Wait for the page to be fully loaded
            //page.waitForLoadState(Page.LoadState.DOMCONTENTLOADED);

            // Example of recording actions:
            
            //main setup
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("documenttype Document Type")).click();
            page.locator("#floatingdoctypedec").click();
            page.getByText("QR GenerateDocument Archiving").click();
            assertThat(page.getByText("Field Required")).isVisible();
            page.locator("#floatingdoctypedec").click();
            page.locator("#floatingdoctypedec").fill("pdf");
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Document Archiving")).check();
            assertThat(page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Metadata"))).isVisible();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("QR Generate")).uncheck();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("QR Generate")).check();
            
            // QR setup
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("QR setup")).getByRole(AriaRole.LINK).click();
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Authentication Validity*")).locator("span").click();
            page.locator(".cdk-overlay-backdrop").click();
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Authentication Validity*")).locator("span").click();
            page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("12 Months")).click();
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("QR Design QROWNTECH Design")).click();
            page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Standard Design")).click();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Pin Code")).check();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Show Authenticator Info")).check();
            page.locator(".circle").first().click();
            page.locator("#ngx-colors-overlay").getByRole(AriaRole.IMG).click();
            page.locator("#ngx-colors-overlay path").nth(1).click();
            page.locator("div:nth-child(13) > .circle").click();
            page.locator("div:nth-child(7) > div > .color-picker > .app-color-picker > .preview > .preview-background > .circle").click();
            page.locator("div:nth-child(7) > .circle").click();
            page.locator("div:nth-child(10) > .circle").click();
            page.locator("#mat-select-value-13").click();
            page.getByText("All pages").click();
            page.locator("label:has-text('Common Files') input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));
            page.locator("label:has-text('Upload Logo Inside QR') input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));
            
            //Communication Channels
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Communication Channels")).getByRole(AriaRole.LINK).click();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Email")).check();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Email")).uncheck();
            
            // Document Metabase
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Metadata")).getByRole(AriaRole.LINK).click();
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Row")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Key value")).click();
            page.getByRole(AriaRole.TEXTBOX, new Page.GetByRoleOptions().setName("Key value")).fill("Metadata 1");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Add Row")).click();
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("Delete").setExact(true)).getByPlaceholder("Key value").click();
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("Delete").setExact(true)).getByPlaceholder("Key value").fill("Metadata 2");
            page.getByRole(AriaRole.ROW, new Page.GetByRoleOptions().setName("Metadata 1 Delete")).getByRole(AriaRole.BUTTON).click();
            
            // Document Template
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Template")).getByRole(AriaRole.LINK).click();
            // Locate the input[type='file'] element and set the file to upload
            page.locator("label.qwd-viewer-doc-label input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));

            // End of your recorded actions

            // Add a delay or some condition to keep the page open for inspection if needed
            // For example, wait for 5 seconds before closing the context
            page.waitForTimeout(5000);

            // Close context and browser when done
            context.close();
            browser.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
