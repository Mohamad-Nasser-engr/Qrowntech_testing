import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LoginHelper {
    public static void loginAndSaveState(Playwright playwright, Browser browser) {
        BrowserContext context = browser.newContext();
        Page page = context.newPage();

        System.out.println("🌐 Navigating to login page...");
        page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/login");

        // Fill login form
        System.out.println("📝 Filling login credentials...");
        page.locator("#username").click();
        page.locator("#username").fill("mohamadnasser13@hotmail.com");
        page.locator("#userpwd").click();
        page.locator("#userpwd").fill("Mn1321/Mn132");
        page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Login")).click();

        // Give time to manually solve CAPTCHA
        System.out.println("⏳ Waiting 60 seconds for CAPTCHA (if any)...");
        page.waitForTimeout(20000); // wait 60 seconds

        // Wait for navigation to dashboard or home
        System.out.println("➡️ Waiting for dashboard page navigation...");
        page.waitForURL("**/dashboard");

        // Save login state
        Path statePath = Paths.get("state.json").toAbsolutePath();
        context.storageState(new BrowserContext.StorageStateOptions().setPath(statePath));
        System.out.println("💾 Login state saved to: " + statePath);

        context.close();
    }
}
