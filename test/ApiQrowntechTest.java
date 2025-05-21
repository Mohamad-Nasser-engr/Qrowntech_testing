import com.microsoft.playwright.*;
import com.microsoft.playwright.options.RequestOptions;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.*;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class ApiQrowntechTest {
    static Playwright playwright;
    static APIRequestContext apiRequest;
    static String token;

    // POJOs for JSON deserialization (only what's needed)
    static class Cookie {
        String name;
        String value;
        String domain;
        String path;
        // Other fields ignored for simplicity
    }

    static class LocalStorageItem {
        String name;
        String value;
    }

    static class Origin {
        String origin;
        List<LocalStorageItem> localStorage;
    }

    static class State {
        List<Cookie> cookies;
        List<Origin> origins;
    }

    @BeforeAll
    static void setup() throws Exception {
        // Initialize Playwright
        playwright = Playwright.create();

        // Parse your saved state.json file
        Gson gson = new Gson();
        FileReader reader = new FileReader("state.json");
        State state = gson.fromJson(reader, State.class);
        reader.close();

        // Extract cookies relevant to your domain
        List<Cookie> cookiesForDomain = state.cookies.stream()
            .filter(c -> c.domain.contains("ambitious-smoke-0480d8303.5.azurestaticapps.net"))
            .collect(Collectors.toList());

        // Extract token from localStorage under your origin
        Optional<Origin> origin = state.origins.stream()
            .filter(o -> o.origin.equals("https://ambitious-smoke-0480d8303.5.azurestaticapps.net"))
            .findFirst();

        if (origin.isPresent()) {
            Optional<LocalStorageItem> tokenItem = origin.get().localStorage.stream()
                .filter(item -> item.name.equals("token"))
                .findFirst();
            token = tokenItem.map(item -> item.value).orElse(null);
        }

        // Build cookie header string for API requests
        String cookieHeader = cookiesForDomain.stream()
            .map(c -> c.name + "=" + c.value)
            .collect(Collectors.joining("; "));

        // Setup APIRequestContext with baseURL and cookie header
        apiRequest = playwright.request().newContext(new APIRequest.NewContextOptions()
            .setBaseURL("https://ambitious-smoke-0480d8303.5.azurestaticapps.net")
            .setExtraHTTPHeaders(Map.of(
                "Cookie", cookieHeader,
                "Authorization", "Bearer " + token  // adjust if your API uses another header
            ))
        );
    }

    @AfterAll
    static void teardown() {
        apiRequest.dispose();
        playwright.close();
    }

    @Test
    void testGetApiConfig() {
        APIResponse response = apiRequest.get("/assets/json/api-config.json");
        assertEquals(200, response.status());

        String body = response.text();
        System.out.println("API CONFIG RESPONSE");
        System.out.println("Response body:\n" + body);
        System.out.println();
    }
    
    @Test
    void testGetReturnDocumentTypes() {
        String url = "https://qrdafd.qrowntech.io/dev/doctmgmt/returnDocumentTypes?page_number=1&page_size=5";
        APIResponse response = apiRequest.get(url);
        assertEquals(200, response.status());

        String body = response.text();
        System.out.println("RETURN DOCUMENT TYPES RESPONSE");
        System.out.println("Response from returnDocumentTypes:\n" + body);
        System.out.println();
    }
    
    @Test
    void testGetReturnPages() {
        String url = "https://qrdafd.qrowntech.io/dev/doctmgmt/returnPages";
        APIResponse response = apiRequest.get(url);
        assertEquals(200, response.status());

        String body = response.text();
        System.out.println("RETURN PAGES RESPONSE");
        System.out.println("Response from returnPages:\n" + body);
        System.out.println();
    }
    
    @Test
    void testGetReturnConfigurations() {
        String url = "https://qrdafd.qrowntech.io/dev/doctmgmt/returnConfiguration";
        APIResponse response = apiRequest.get(url);
        assertEquals(200, response.status());

        String body = response.text();
        System.out.println("RETURN CONFIGURATION RESPONSE");
        System.out.println("Response from returnPages:\n" + body);
        System.out.println();
    }
    
    @Test
    void testGetReturnQrDesign() {
        String url = "https://qrdafd.qrowntech.io/dev/doctmgmt/returnConfiguration";
        APIResponse response = apiRequest.get(url);
        assertEquals(200, response.status());

        String body = response.text();
        System.out.println("RETURN QR DESIGN RESPONSE");
        System.out.println("Response from returnPages:\n" + body);
        System.out.println();
    }
    
    @Test
    void testGetReturnAltCommChannel() {
        String url = "https://qrdafd.qrowntech.io/dev/doctmgmt/returnAlternativeCommChannel";
        APIResponse response = apiRequest.get(url);
        assertEquals(200, response.status());

        String body = response.text();
        System.out.println("RETURN ALT COMM CHANNEL RESPONSE");
        System.out.println("Response from returnPages:\n" + body);
        System.out.println();
    }
    
    @Test
    void testGetReturnDocumentTypeValidity() {
        String url = "https://qrdafd.qrowntech.io/dev/doctmgmt/returnDocumentTypeValidity";
        APIResponse response = apiRequest.get(url);
        assertEquals(200, response.status());

        String body = response.text();
        System.out.println("RETURN DOCUMENT TYPE VALIDITY RESPONSE");
        System.out.println("Response from returnPages:\n" + body);
        System.out.println();
    }
    
    @Test
    void testGenerateQrPreview() {
        String jsonBody = """
            {
                "id_in_qr": false,
                "corners": false,
                "authenticator": null,
                "domain": false,
                "group_by_customer": null,
                "background_color": "#E0E0E000",
                "foreground_color": "#000000",
                "logo_content": null,
                "qr_design": "1"
            }
            """;

        APIResponse response = apiRequest.post("https://qrdafd.qrowntech.io/dev/docmgmt/generateQrPreview",
            RequestOptions.create().setHeader("Content-Type", "application/json").setData(jsonBody)
        );

        assertEquals(200, response.status());
        String body = response.text();
        System.out.println("GENERATE QR PREVIEW RESPONSE");
        System.out.println("QR Preview Response:\n" + body);
        System.out.println();
    }

}
