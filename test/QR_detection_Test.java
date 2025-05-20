import com.microsoft.playwright.junit.UsePlaywright;
import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.*;

import org.junit.jupiter.api.*;
import static com.microsoft.playwright.assertions.PlaywrightAssertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;


public class QR_detection_Test {
	
	static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static final Path statePath = Paths.get("state.json");
	
	@BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setArgs(Arrays.asList("--window-size=1920,1080","--disable-gpu","--no-sandbox", "--use-gl=egl")));

        try {
            if (Files.exists(statePath)) {
                System.out.println("✅ Using saved session...");
                context = browser.newContext(new Browser.NewContextOptions().setStorageStatePath(statePath).setViewportSize(1920, 1080));
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
  void QR_detection_test() {
    page.navigate("https://ambitious-smoke-0480d8303.5.azurestaticapps.net/#/dashboard");
    page.getByRole(AriaRole.LINK, new Page.GetByRoleOptions().setName("documenttype Document Type")).click();
    page.locator("#floatingdoctypedec").click();
    page.locator("#floatingdoctypedec").fill("pdf");
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("QR setup")).getByRole(AriaRole.LINK).click();
    page.getByRole(AriaRole.COMBOBOX, new Page.GetByRoleOptions().setName("Authentication Validity*")).locator("span").click();
    page.getByRole(AriaRole.OPTION, new Page.GetByRoleOptions().setName("3 Months")).click();
    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Show Authenticator Info")).check();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Template")).getByRole(AriaRole.LINK).click();
    page.locator("label.qwd-viewer-doc-label input[type='file']")
    .setInputFiles(Paths.get("C:/Users/user/Desktop/WORK DOCUMENTS/MohamadNasser_Resume.pdf"));
    assertThat(page.getByText("QR Size: 2 cm")).isVisible();
    
    // NO QR
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Main Setup")).getByRole(AriaRole.LINK).click();
    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("Document Archiving")).check();
    page.getByRole(AriaRole.CHECKBOX, new Page.GetByRoleOptions().setName("QR Generate")).uncheck();
    page.getByRole(AriaRole.BUTTON, new Page.GetByRoleOptions().setName("Document Template")).getByRole(AriaRole.LINK).click();
    assertThat(page.getByText("QR Size: 2 cm")).not().isVisible();
  }
  
  @AfterAll
  static void tearDown() {
      context.close();
      browser.close();
      playwright.close();
  }
}