import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.microsoft.playwright.*;
import com.microsoft.playwright.options.AriaRole;
import org.junit.jupiter.api.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;



public class Dashboard_Test {
    static Playwright playwright;
    static Browser browser;
    static BrowserContext context;
    static Page page;
    static final Path statePath = Paths.get("state.json");
    
    //QR detector
    public static String detectQRCodeInImage(Path imagePath) {
        try {
            BufferedImage image = ImageIO.read(imagePath.toFile());
            LuminanceSource source = new BufferedImageLuminanceSource(image);
            BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));

            // Add decode hints
            Map<DecodeHintType, Object> hints = new HashMap<>();
            hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
            hints.put(DecodeHintType.POSSIBLE_FORMATS, Collections.singletonList(BarcodeFormat.QR_CODE));
            hints.put(DecodeHintType.CHARACTER_SET, "UTF-8");

            Result result = new MultiFormatReader().decode(bitmap, hints);
            return result.getText();
        } catch (NotFoundException e) {
            System.err.println("QR code not found: " + e.getMessage());
            return null;
        } catch (IOException e) {
            System.err.println("Image read error: " + e.getMessage());
            return null;
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            return null;
        }
    }



    @BeforeAll
    static void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false).setArgs(Arrays.asList("--window-size=1920,1080","--disable-gpu")));

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
        // QR detection
     // Zoom out the entire page
        page.evaluate("document.body.style.zoom = '60%'");

        // Small wait so the zoom effect applies properly
        page.waitForTimeout(500);
        Locator canvas = page.locator("#PdfjsAnnotationExtension_painter_wrapper_page_1 canvas").first();
        Path screenshotPath = Paths.get("QR_code_image.png");

        canvas.screenshot(new Locator.ScreenshotOptions().setPath(screenshotPath));
        
        String qrText = detectQRCodeInImage(screenshotPath);
        Assertions.assertNotNull(qrText, "QR Code was not detected in canvas screenshot");
        System.out.println("QR Code detected: " + qrText);
        
        // Optional wait to observe//
        page.waitForTimeout(5000);
    }

    @AfterAll
    static void tearDown() {
        context.close();
        browser.close();
        playwright.close();
    }
}
