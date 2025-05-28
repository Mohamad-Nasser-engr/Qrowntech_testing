# 📘 Playwright with Java & JUnit – Research Findings

## 🧩 Overview

This document summarizes my research on using [Microsoft Playwright](https://playwright.dev/java/) for end-to-end testing with **Java** and **JUnit**. It focuses on how Playwright can facilitate rapid test creation via its **codegen recorder**, and how it fits into modern DevOps pipelines.

---

## Tool Evaluation and Selection Justification
As part of my research, I evaluated several popular test automation tools: TestRigor, Mabl, Katalon Studio, Selenium, and Playwright. Each was assessed based on its features, usability, pricing, and integration capabilities.

### Summary of Comparison:

| Tool             | Strengths                                                                 | Limitations                                                                 |
|------------------|---------------------------------------------------------------------------|------------------------------------------------------------------------------|
| **TestRigor**     | Natural language scripting, AI-powered suggestions, API testing, CI/CD ready | Recorder is **very inaccurate**, expensive (~$900/month), limited control for complex scenarios |
| **Mabl**          | Low-code scripting, visual testing, parallel execution, cloud-based       | No free tier beyond 14-day trial, must contact sales for pricing             |
| **Katalon Studio**| Supports UI/code test creation, good recorder, API & mobile testing       | **CI/CD integration requires paid tier**, many features behind paywall       |
| **Selenium**      | Free and open-source, supports many languages, flexible for dev teams     | **Jenkins plugin is unstable**, lacks built-in visual/mobile/API testing, steep learning curve |
| **Playwright**    | Free, modern, fast, supports Chromium/Firefox/WebKit, great dev experience| Limited mobile support (emulated only), no built-in AI, requires coding      |

### Why I Chose Playwright
After analyzing these tools, I chose Playwright for the following reasons:
- Comprehensive cross-browser testing — includes Chromium, Firefox, and WebKit support out of the box.
- Fast and modern — excellent for dynamic web apps with faster test execution than Selenium.
- Code-first flexibility — full control for customizing complex test flows.
- Developer-focused tools — powerful selector engine, smooth debugging, and intelligent error messages.
- Cost-effective — fully open-source with no licensing costs.
- CI/CD ready — seamless integration into modern pipelines with parallel execution support.
- Native Support for Multiple Languages — Playwright supports JavaScript, Python, Java, and C#, providing flexibility to use your preferred language.
- Headless & Headed Execution — It supports both headless and headed browser executions.
- Auto-Wait and Smart Assertions — Playwright automatically waits for elements to be ready before interacting with them, ensuring more reliable tests.
- Recorder Tool — Playwright’s recorder tool automatically generates tests, simplifying test creation and boosting productivity.

---

## 🔧 Prerequisites
Before getting started, ensure you have the following tools and accounts set up:
- Java 
- Node.js installed (for Playwright CLI tools)
- Maven installed
- Jenkins server set up
- JIRA Cloud account with admin access

---

## 🧪 Technology Stack

- **Playwright** v1.51.0 
- **Java** 
- **JUnit 5**
- **Maven** (for project and dependency management)

---

## ⚙️ Installation & Setup
This section guides you through setting up a Java project using Maven, configuring Playwright for browser automation, and using the recorder to generate UI test code.

### 1. Create a Maven Project
Maven is a build automation tool used primarily for Java projects. It uses a **pom.xml** file to manage dependencies and build configurations. If you're using an IDE like IntelliJ IDEA or Eclipse, you can create a Maven project from the "New Project" wizard.

File -> New Project -> Maven Project

This creates a basic Maven structure with a pom.xml file.

### 2. Add Playwright and Junit 5 to your Maven `pom.xml`:
In the pom.xml file specify both your source directory and your test source directory:
```xml
<build>
<sourceDirectory>src</sourceDirectory>
<testSourceDirectory>test</testSourceDirectory>
<plugins>
<!-- added any needed plugins here -->
</plugins>
</build>
```
Edit the pom.xml file to include the following dependencies. These will enable Java to work with Playwright and run JUnit 5 tests:
```xml
<dependencies>
<dependency>
  <groupId>com.microsoft.playwright</groupId>
  <artifactId>playwright</artifactId>
  <version>1.51.0</version>
</dependency>
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter-api</artifactId>
  <version>5.8.1</version>
  <scope>test</scope>
</dependency>
<dependency>
  <groupId>org.junit.jupiter</groupId>
  <artifactId>junit-jupiter-engine</artifactId>
  <version>5.8.1</version>
  <scope>test</scope>
</dependency>
</dependencies>
```

To properly run JUnit tests using Maven, you need to configure the maven-surefire-plugin in your pom.xml file. This plugin ensures that JUnit 5 tests are executed during the Maven build process.

Add the following configuration for the maven-surefire-plugin:
```xml
    <plugins>
        <plugin>
          <artifactId>maven-compiler-plugin</artifactId>
          <version>3.8.1</version>
          <configuration>
            <release>17</release>
          </configuration>
        </plugin>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-surefire-plugin</artifactId>
            <version>3.0.0-M5</version>
            <configuration>
                <!-- Optional configurations -->
                <includes>
                    <include> <!--update this for different naming convention--> </include>
                </includes>
            </configuration>
        </plugin>
    </plugins>
```
Ensure your test classes follow the naming convention ***Test.java** so Surefire can detect them. If not, update includes in the plugin config.
### 3. Install Playwright recorder
This step installs Playwright using the Node.js version, which is required to record and generate Java test code.
```bash
npx playwright install 
```
This will download browser binaries and set up Playwright's command-line tools.
### 4. Use Playwright's recorder 
Now you're ready to generate Java test code using Playwright's recorder:
```bash
npx playwright codegen https://example.com
```

This command opens a browser window where you can interact with the site. As you click, type, or navigate, Playwright automatically generates Java test code in real time.

Important: In the top-right corner of the recorder, change the target language to Java JUnit to ensure it generates compatible test code.

The screenshot below shows an example of what the recorder looks like:

![image](https://github.com/user-attachments/assets/d5d5f4e5-00c3-4903-a669-7a1f53e7c5f5)

---

## API Testing Using Playwright
Playwright is widely known for browser automation, but it also provides robust support for API testing through its APIRequestContext. This allows developers to validate backend services directly—without needing a browser UI—by sending HTTP requests and asserting responses. Below is a summary of how Playwright was used to test various backend API endpoints. 

### Setup:
We initialize a Playwright instance and create an APIRequestContext object, which allows us to perform HTTP requests.

```
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)   // Enables ordered execution of test methods
public class ApiQrowntechTest {
    static Playwright playwright;
    static APIRequestContext apiRequest;

    // Variables to be used in future tests
    static boolean corners;
    static boolean domain;
    static int qrDesign;
    static String backgroundColor;
    static String foregroundColor;

@BeforeAll
    static void setup() throws Exception {
        // Initialize Playwright
        playwright = Playwright.create();
        // Setup APIRequestContext with baseURL and cookie header
        apiRequest = playwright.request().newContext(new APIRequest.NewContextOptions()
                     .setBaseURL("https://qrdafd.qrowntech.io")  // optional
);
}

@AfterAll
    static void teardown() {
        apiRequest.dispose();
        playwright.close();
    }

// Add tests Here
...
}
```

### Test Structure:
We used JUnit 5 annotations to structure our tests. Each test corresponds to a specific API endpoint on the Qrowntech platform. Tests are ordered to ensure proper setup before dependent calls are made.
### Example: GET Request – Fetching Dynamic Configuration
```
@Test
    @Order(4) // This indicates that this will be the 4th test executed 
    void testGetReturnConfigurations() {
        String url = "https://qrdafd.qrowntech.io/dev/doctmgmt/returnConfiguration";
        APIResponse response = apiRequest.get(url);
        assertEquals(200, response.status());

        String body = response.text();       
        
     // Parse the JSON array and store responses in variables to be used later
        JsonArray configArray = JsonParser.parseString(body).getAsJsonArray();
        if (!configArray.isEmpty()) {
            JsonObject config = configArray.get(0).getAsJsonObject(); // Use the first object
            corners = config.get("corners").getAsBoolean();
            domain = config.get("domain").getAsBoolean();
            qrDesign = config.get("qr_design").getAsInt();
            backgroundColor = config.get("background_color").getAsString();
            foregroundColor = config.get("foreground_color").getAsString();  
        }
    }
```
### Example: POST Request with Dynamic JSON Body:
This test sends a POST request to generate a QR preview using parameters captured from previous GET requests.
```
@Test
@Order(8)
void testGenerateQrPreview() {
    String jsonBody = String.format("""
        {
            "id_in_qr": false,
            "corners": %b,
            "authenticator": null,
            "domain": %b,
            "group_by_customer": null,
            "background_color": "%s",
            "foreground_color": "%s",
            "logo_content": null,
            "qr_design": "%d"
        }
    """, corners, domain, backgroundColor, foregroundColor, qrDesign);  // Variables (e.g., corners, domain) are populated in testGetReturnConfigurations()

    APIResponse response = apiRequest.post("https://.../generateQrPreview",
        RequestOptions.create().setHeader("Content-Type", "application/json").setData(jsonBody)
    );

    assertEquals(200, response.status());
    String body = response.text();
}
```

### Reusability and Maintainability:
- Dynamic JSON Construction:
Parameters like corners, domain, and qrDesign are populated in earlier tests and reused for more complex request bodies.
- Assertion & Debugging:
Each response is asserted for status code and optionally printed for manual inspection.

---

## Capturing Browser Console Logs with Playwright
Monitoring the browser console for JavaScript errors is crucial in maintaining front-end reliability. Playwright provides a simple way to listen for console messages, including errors, warnings, and logs.

In this test case, we attached a listener to capture all console messages. If any console.error is triggered during the execution, the test will fail and print the errors in the test output.

### Console Listener Setup
```
page.onConsoleMessage(msg -> {
    if ("error".equals(msg.type())) {
        System.err.println("🚨 Console Error: " + msg.text());
        browserErrorLogs.add("🚨 Console Error: " + msg.text());
    } else {
        System.out.println("📋 Console [" + msg.type() + "]: " + msg.text());
    }
});
```
Note that 'browserErrorLogs' is a 'List<String>' declared at the top of the test class to store all captured error messages.

### Why This Matters:
- Detects uncaught front-end issues (e.g., missing resources, null errors, etc.)
- Validates that the application is not silently failing
- Makes integration into CI (e.g., Jenkins) more robust

## Final Assertion Example:
At the end of the test, we check for collected errors:
```
if (!browserErrorLogs.isEmpty()) {
    Assertions.fail("Browser console errors detected:\n" + String.join("\n", browserErrorLogs));
}
```
If any browser errors were logged, the test will immediately fail, ensuring only error-free builds proceed further.

![image](https://github.com/user-attachments/assets/c1f1f720-24ad-49d4-94e4-804ce938115b)

---

## Integration with Jenkins 

This section shows how to integrate your Playwright Java tests into a CI pipeline using Jenkins. The goal is to automatically run your tests every time you push code to GitHub.

### 1. Setup a GitHub Repository

You’ll need your Maven project hosted on GitHub so Jenkins can pull and build it.

**Steps**:
- Create a GitHub repository
- Go to your Maven project and initialize it:
  ```bash
  git init
  git remote add origin https://github.com/your-username/your-repo-name.git
  ```
- Add, Commit and push changes for the first time:
  ```bash
  git add .
  git commit -m "Initial Playwright test setup"
  git push --set-upstream origin master
  ```
- After that each time you want to push changes:
  ```bash
  git add .
  git commit -m "Commit Message"
  git push origin
  ```
### 2. Install Jenkins on Windows:
- Go to https://www.jenkins.io/download/
- Download the .msi installer for windows
- Run the installer and follow the setup wizard
- Jenkins will install as a Windows service (default port: 8080 or custom one like 8443)
Now you can access Jenkins by going to the following url:
```
http://localhost:<port-number>
```
- The first time you access Jenkins, it will ask for a password which can be found in this file:
  ```
  <Jenkins-installation-path>/secrets/initialAdminPassword
  ```
- paste the password in the web UI
- Finalize Jenkins Setup
  
### 3. Install Required Plugins
Ensure the following plugins are installed (Manage Jenkins → Manage Plugins):
- Git Plugin – For Git integration
- GitHub Plugin – For GitHub webhooks and integration
- JUnit – To publish test results
- Maven Integration – To build Maven projects

### 4. Create and configure a new Maven Project:
- In Jenkins, click New Item, name your job, and select Maven Project.
- Under Source Code Management, select Git and enter your GitHub repository URL.
- In the Build Trigger section, make sure to check “GitHub hook trigger for GITScm polling”. This allows Jenkins to automatically trigger a build when changes are pushed to the GitHub repository.
- In the Build section:

  Root POM:
  ```
  pom.xml
  ```
  Goals and Options:
  ```
  clean install
  ```
This setup will clone your code and run your tests automatically using the pom.xml file.

### 5. Expose Jenkins to GitHub using ngrok (for Webhooks):
If Jenkins is hosted locally (e.g., on http://localhost:8443), GitHub won’t be able to trigger webhooks unless it's exposed to the internet.

To solve this we use ngrok to create a public URL that links to Jenkins:
- Install ngrok
- Start ngrok on the Jenkins port:
```bash
ngrok http 8443
```
This will generate a public URL that links to Jenkins

![image](https://github.com/user-attachments/assets/5fa0b1a1-5b2e-4de3-83d8-a901a5157f4d)

- Now that we created the URL we will add it as a WebHook in GitHub:
   - Go to your repository → Settings → Webhooks → Add webhook
   - Payload URL: <generated-ngrok-url>
   - Content type: application/json
   - Events: Choose “Just the push event”
 
![image](https://github.com/user-attachments/assets/1f81ec7a-b59e-45d7-8533-5d8592f6b0c0)

- In Jenkins:
   - Make sure your job is configured to trigger builds on GitHub push events
- Push a change to test the setup
  
⚠️ Note:
- The ngrok URL is only valid as long as the terminal window running ngrok is active. If you close the window or your system restarts, the URL will stop working.
- Each time you restart ngrok, a new URL is generated. Don’t forget to update the GitHub webhook with the new URL.

---

## Jenkins Integration with JIRA
Integrating Jenkins with JIRA allows automatic linking of build results, commits, and issues, making it easier to track development progress directly from JIRA.
### Steps to integrate Jenkins with JIRA
### 1. Install the Jenkins JIRA Plugin
- Go to Manage Jenkins → Manage Plugins
- Under the Available tab, search for and install JIRA Plugin
- Restart Jenkins if prompted
### 2. Create a JIRA API Token (for Atlassian Cloud users)
If you're using Atlassian Cloud, you need an API token to authenticate Jenkins.
To generate one:
- Visit https://id.atlassian.com/manage/api-tokens
- Click Create API token
- Enter a label like "Jenkins Integration", then click Create
- Copy the generated token (you won’t be able to view it again)

  ![image](https://github.com/user-attachments/assets/32bd2b04-afb5-49f3-8cec-29b5c7e0602e)

### 3. Add JIRA credentials in Jenkins:
- Go to Manage Jenkins → Credentials
- Click **Add Credentials**
  - **Kind**: Username with password
  - **Username**: Your Atlassian account email
  - **Password**: Paste the API token
  - **ID/Description**: Optional (e.g., jira-api-token)

  ![image](https://github.com/user-attachments/assets/705dc3e8-80ca-4028-a2bb-994e05789edd)

### 4. Configure JIRA site in Jenkins
- Go to Manage Jenkins → Configure System
- Scroll to the JIRA section
- Click Add Jira Site
    - Enter the JIRA server URL (e.g., https://yourcompany.atlassian.net)
    - Add the previously created credentials (JIRA API token)
    - Test the connection
### 5. Link JIRA site in a Jenkins Job
- Open your Jenkins Job → Configure
- Scroll to the JIRA site section
- Select the JIRA site you previously configured
- This step enables Jenkins to associate builds with JIRA issues, making issue references clickable in build logs and views

*Note: This integration does not automatically update JIRA issues (e.g., adding comments or changing statuses). For that, you must use additional plugins, scripted logic, or smart commits via your version control system*

---

## Xray Integration for Test Management:
Xray is a comprehensive test management tool for Jira that supports both manual and automated testing, including unit, integration, and end-to-end tests. Integrating Jenkins with Xray allows teams to automatically import test execution results, maintaining a tight link between code, test results, and Jira issues.

This integration provides end-to-end traceability, helping QA, Dev, and PM teams monitor the health of releases from within Jira itself — including test coverage, results, and defect links.

*Note: Xray for Jira Cloud is a paid add-on, starting at $10/month for up to 10 users. Larger teams pay more, based on tiered Atlassian pricing*

### Why I chose Xray:
Initially I used QAlity Plus for test management due to its ease of integration with Jira and minimal setup. However, several limitations became apparent during CI/CD pipeline integration:
- QAlity Plus did not support automatic test issue creation from Jenkins builds.
- While it allowed changing issue statuses (e.g., from In Progress to Done), it lacked support for:
  - Automatically identifying failed tests.
  - Showing detailed error messages or stack traces for failed tests.
  - Capturing test failures required writing custom scripts to extract logs from Jenkins and manually push them to Jira.
    
Because of these limitations, we switched to **Xray for Jira**, which provided the following improvements:  
- Automatically creates test issues in Jira from test results in Jenkins.
- Automatically marks tests as Passed or Failed in Jira.
- Displays detailed error messages for each failed test without custom scripting.

### 1. Install and configure the Xray App in JIRA:
To begin, install Xray in your Jira Cloud instance:
- In Jira, go to Apps → Explore more apps
- Search for **Xray Test Management** for Jira
- Click Install
- Once installed, you'll see Xray in the Jira top navigation or under Apps → Xray

### 2. Configure Xray for all needed JIRA projects:
- Go to Apps -> Xray -> Configure Project
- Choose your JIRA project
- Add all Xray Issue Types

![image](https://github.com/user-attachments/assets/a95d7e25-07ff-4e2e-b187-9a7de42ba105)

Now you can create and use Xray issue types

### 3. Install the Xray Plugin in Jenkins
To enable integration between Jenkins and Xray:
- Navigate to Jenkins Dashboard → Manage Jenkins → Plugins.
- Search for Xray and install the plugin.
- Restart Jenkins if required.
### 4. Create API Credentials in Jira:
Xray uses Jira API tokens for secure integration. To generate one:
- In Jira, go to Apps → Manage Apps → API Keys.
- Click Create API Key.
- Give it a name, and copy the generated key for later use.
### 5. Configure Xray in Jenkins:
Now, connect Jenkins to Xray:
- Go to Manage Jenkins → System → Configure System.
- Scroll to the Xray Configuration section.
- Fill in the following:
  - Configuration Alias: Choose JIRA Cloud.
  - Hosting Type: Select Cloud.
  - Credentials: Add the API Key from Step 3 as a new credential.
- Click Test Connection to ensure everything works correctly.

![image](https://github.com/user-attachments/assets/ea04c789-5b75-41c3-8fb4-555f60e32c73)

### 6. Add Xray Reporting in a Jenkins Job:
Once the plugin is configured, add test result publishing to your build job:
- Open your Jenkins job and click Configure.
- Under Post-build Actions, click Add post-build action → Select Xray: Results Import Task.
- Set the following options:
  - Instance: Choose JIRA Cloud.
  - Format: Choose JUnit XML.
  - Execution Report File: Enter your test report path (e.g., target/surefire-reports/*.xml).
  - JIRA Project Key: Enter the Jira project key (e.g., ABC).
  - Test Execution Key: Enter the corresponding Test Execution issue key (e.g., ABC-123).
    - Note: The Test Execution issue must already exist in Jira. If not, you'll need to create it manually before the integration works.

  ![image](https://github.com/user-attachments/assets/686720d0-1839-4868-ae56-16fd36ba725a)

Now, after the JUnit tests are executed during the Jenkins build, all the test results will be automatically imported and displayed in the specified Test Execution issue in Jira:

 ![image](https://github.com/user-attachments/assets/bb1a53f5-9097-4c99-ae32-fa75ca840b76)

**This provides immediate visibility into test outcomes directly within Jira, enabling better collaboration between development and QA teams.**

Additionally, you can view detailed information about failed test cases by clicking the "Test Details" button located at the right of each test:

![image](https://github.com/user-attachments/assets/deb6e964-1b47-41ce-8aff-7031e02aed3f)

--- 

## Manually Trigerring Jenkins Jobs From JIRA:
By integrating Jira with Jenkins, you can manually trigger Jenkins jobs directly from Jira issues. This is particularly useful for on-demand testing or executing specific builds without leaving the Jira interface.
### Prerequisites:
- Jenkins is accessible over the internet (e.g., via an ngrok URL).
- A Jenkins user with an API token.
- The Jenkins job is configured to allow remote triggering.
  
*Note: This section assumes you have already set up your Jenkins API token and exposed Jenkins via an ngrok URL as described in the earlier sections.*

### Enable remote trigerring in Jenkins:
  - Go to your Jenkins job
  - Click on Configure
  - Under Build Triggers, check Trigger builds remotely
  - Set your Authentication Token (e.g., my-trigger-token)

![image](https://github.com/user-attachments/assets/a8046af8-3209-41a6-b427-77faf009eb5e)

### Create a manual trigger rule in JIRA
- In Jira, navigate to your project.
- Go to Project Settings → Automation.
- Click Create Rule.
- Select Manual trigger as the trigger.
- Click New Action and choose Send web request.

### Configure the Web request:
- Web Request URL:
    ```
    https://<your-ngrok-url>/job/<jenkins-job-name>/build?token=<my-trigger-token>
    ```
- HTTP Method: POST
- Headers
  - Key 1: Authorization:
    ```
    Basic <base64_encoded_credentials>
    ```
    Replace with *base64-encoded string* of jenkins-username:jenkins-api-token (Use the following link to generate the encoded version: https://www.base64encode.org).

    ![image](https://github.com/user-attachments/assets/37860daf-52a0-4245-b7e9-f0d99698be48)

### Test and Publish the Rule:
- Click Validate to test the web request configuration.
- If successful, click Publish to activate the rule.

### Trigger the Jenkins Job from a Jira Issue:
- Open a Jira issue within the project.
- Click on the Automation button (or Actions → Automation).
- Select the manual trigger rule you created.
- The Jenkins job should start executing.

![image](https://github.com/user-attachments/assets/299c73f2-1a90-4811-a41a-4f69a5558509)

