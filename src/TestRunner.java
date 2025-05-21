import com.microsoft.playwright.*;
import java.nio.file.*;

public class TestRunner {
    public static void main(String[] args) {
        try (Playwright playwright = Playwright.create()) {
            Browser browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false)
            );
            BrowserContext context = null;

            Path statePath = Paths.get("state.json").toAbsolutePath();
            boolean sessionValid = false;

            System.out.println("📁 Looking for state file at: " + statePath);

            // Try using existing session
            if (Files.exists(statePath)) {
                System.out.println("📦 Found saved state. Trying to use it...");

                context = browser.newContext(
                    new Browser.NewContextOptions().setStorageStatePath(statePath)
                );
                Page page = context.newPage();
                page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");
                page.waitForLoadState();
                page.waitForTimeout(5000);

                // Check if we're still logged in
                if (page.url().contains("/login")) {
                    System.out.println("⚠️ Session invalid. Redirected to login.");
                    sessionValid = false;
                    context.close();
                    context = null;
                } else {
                    sessionValid = true;
                    System.out.println("✅ Using saved session.");
                }
            }

            // If no valid session, login and save new one
            if (!sessionValid) {
                System.out.println("🔁 Logging in again and saving new session...");
                LoginHelper.loginAndSaveState(playwright, browser);
                context = browser.newContext(
                    new Browser.NewContextOptions().setStorageStatePath(statePath)
                );
            }

            // Now run your test
            if (context != null) {
                Page page = context.newPage();
                page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");
                System.out.println("🚀 Arrived at dashboard. Pausing to visualize...");
                page.waitForTimeout(3000);  // Pause to visualize

                context.close();
            } else {
                System.err.println("❌ Failed to create a browser context.");
            }
        }
    }
}
