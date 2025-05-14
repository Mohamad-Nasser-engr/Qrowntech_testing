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
            page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("documenttype Document Type")).click();
            page.locator("#floatingdoctypedec").click();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Document Archiving")).check();
            assertThat(page.locator("app-collapse")).containsText("Document Metadata");
            assertThat(page.getByText("Field Required")).isVisible();
            page.locator("#floatingdoctypedec").click();
            page.locator("#floatingdoctypedec").fill("pdf");
            page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("QR setup")).getByRole(AriaRole.LINK).click();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Pin Code")).check();
            page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Show Authenticator Info")).check();
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Authentication Validity*")).locator("span").click();
            page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("6 Months")).click();
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("QR Design QROWNTECH Design")).click();
            page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("Standard Design")).click();
            page.locator("ngx-colors").first().click();
            page.locator("div:nth-child(13) > .circle").click();
            page.locator("ngx-colors").nth(1).click();
            page.locator("div:nth-child(19) > .circle").click();
            page.locator("div:nth-child(10) > .circle").click();
            page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("QR Stamped On Document* First")).click();
            page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("All pages")).click();
            //page.locator("label").filter(new Locator.FilterOptions().setHasText("Common Files")).click();
            
            // check html to see labels for file input 
            page.locator("label:has-text('Common Files') input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));
            //page.locator("label:has-text('Upload Logo Inside QR') input[type='file']").waitFor();
            page.locator("label:has-text('Upload Logo Inside QR') input[type='file']").setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));

            
            
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
