package org.jbehavesupport.core.web;

import static org.apache.commons.lang3.StringUtils.prependIfMissing;
import static org.apache.commons.lang3.StringUtils.removeEnd;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.beans.factory.annotation.BeanFactoryAnnotationUtils.qualifiedBeanOfType;
import static java.util.Arrays.asList;
import static java.lang.String.join;

import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import org.assertj.core.api.Assertions;
import org.jbehavesupport.core.TestContext;
import org.jbehavesupport.core.expression.ExpressionEvaluatingParameter;
import org.jbehavesupport.core.internal.verification.VerifierNames;
import org.jbehavesupport.core.internal.web.GivenStoryHelper;
import org.jbehavesupport.core.internal.web.WebScreenshotCreator;
import org.jbehavesupport.core.verification.Verifier;
import org.jbehavesupport.core.verification.VerifierResolver;

import lombok.RequiredArgsConstructor;
import org.apache.http.client.utils.URIBuilder;
import org.jbehave.core.annotations.AfterScenario;
import org.jbehave.core.annotations.BeforeScenario;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.ScenarioType;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.annotations.When;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Parameters;
import org.jbehave.core.steps.Row;
import org.jbehavesupport.core.internal.ExampleTableConstraints;
import org.jbehavesupport.core.internal.MetadataUtil;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public final class WebSteps {

    private static final String ACTION = "action";
    private static final String ELEMENT = "element";
    private static final String PROPERTY = "property";

    private static final ThreadLocal<WebSetting> CURRENT_SETTING = new ThreadLocal<>();

    private final ConfigurableListableBeanFactory beanFactory;
    private final TestContext testContext;
    private final WebDriver driver;
    private final WebActionResolver actionResolver;
    private final WebPropertyResolver propertyResolver;
    private final WebWaitConditionResolver waitConditionResolver;
    private final WebScreenshotCreator screenshotCreator;
    private final VerifierResolver verifierResolver;
    private final GivenStoryHelper givenStoryHelper;

    public static WebSetting getCurrentSetting() {
        return CURRENT_SETTING.get();
    }

    @Autowired
    private WebElementRegistry elementRegistry;

    @BeforeScenario
    public void beforeScenario() {
        if (!givenStoryHelper.isInGivenStory()) {
            driver.quit();
        }
    }

    @AfterScenario(uponType = ScenarioType.ANY, uponOutcome = AfterScenario.Outcome.SUCCESS)
    public void afterSuccessScenario() {
        if (!givenStoryHelper.isInGivenStory()) {
            driver.quit();
        }
    }

    @AfterScenario(uponType = ScenarioType.ANY, uponOutcome = AfterScenario.Outcome.FAILURE)
    public void afterFailedScenario() {
        screenshotCreator.createScreenshot();
        if (!givenStoryHelper.isInGivenStory()) {
            driver.quit();
        }
    }

    @Given("[$url] url is open")
    @When("[$url] url is open")
    public void openUrl(ExpressionEvaluatingParameter<String> url) {
        driver.get(url.getValue());
    }

    @Given("[$application] homepage is open")
    @When("[$application] homepage is open")
    public void openHomePage(String application) {
        driver.get(resolveHomePageUrl(application));
    }

    @Given(value = "[$application]/[$path] url is open", priority = 100)
    @When(value = "[$application]/[$path] url is open", priority = 100)
    public void openUrl(String application, ExpressionEvaluatingParameter<String> pathExpression) {
        String path = prependIfMissing(pathExpression.getValue().replace("//(?!:)", "/"), "/");
        String url = resolveHomePageUrl(application) + path;
        driver.get(url);
    }

    @Given("[$application]/[$path] url is open with query parameters:$queryParameters")
    @When("[$application]/[$path] url is open with query parameters:$queryParameters")
    public void openUrl(String application, String path, ExamplesTable queryParameters) throws URISyntaxException {
        URIBuilder uriBuilder = new URIBuilder(resolveHomePageUrl(application) + prependIfMissing(path, "/"));
        for (Parameters queryParameter : queryParameters.getRowsAsParameters()) {
            String queryParameterName = queryParameter.valueAs(ExampleTableConstraints.NAME, String.class);
            String queryParameterData = queryParameter.valueAs(ExampleTableConstraints.DATA, String.class);
            uriBuilder.addParameter(queryParameterName, queryParameterData);
        }
        driver.get(uriBuilder.build().toString());
    }

    @When("on [$page] page these actions are performed:$actionTable")
    public void performActions(String page, ExamplesTable actionTable) {
        for (Row actionRow : actionTable.getRowsAsParameters()) {
            Map<String, String> actionValues = actionRow.values();

            WebAction action = actionResolver.resolveAction(actionValues.get(ACTION));

            WebActionContext actionContext = WebActionContext.builder()
                .page(page)
                .element(actionValues.get(ELEMENT))
                .data(actionValues.get(ExampleTableConstraints.DATA))
                .alias(actionValues.getOrDefault(ExampleTableConstraints.ALIAS, null))
                .build();

            action.perform(actionContext);
        }
    }

    @Given("on [$page] page these values are saved:$table")
    @Then("on [$page] page these values are saved:$table")
    public void storePropertiesInContext(String page, ExamplesTable table) {
        for (Row row : table.getRowsAsParameters()) {
            Map<String, String> values = row.values();
            Object value = resolvePropertyValue(page, values);
            testContext.put(values.get(ExampleTableConstraints.ALIAS), value, MetadataUtil.userDefined());
        }
    }

    @Then("on [$page] page these conditions are verified:$table")
    public void verifyProperties(String page, ExamplesTable table) {
        for (Row row : table.getRowsAsParameters()) {
            Map<String, String> values = row.values();

            Object expected = values.get(ExampleTableConstraints.DATA);
            Object actual = resolvePropertyValue(page, values);
            String verifierName = resolveVerifierName(values);

            Verifier verifier = verifierResolver.getVerifierByName(verifierName);
            verifier.verify(actual, expected);
        }
    }

    /**
     * Waits on given page, until requested condition is met, or timeout runs out.
     *
     * @param page      page defined in yml file
     * @param element   element specified under page in same yml file or reserved keyword: @url | @title
     * @param condition parsed for following expressions: <ul>
     *                  <li>is present</li>
     *                  <li>is clickable</li>
     *                  <li>is visible</li>
     *                  <li>is not visible</li>
     *                  <li>has $attr $attrVal (i.e. has text someText | has class issue | has id)</li>
     *                  <li>missing $attr $attrVal (i.e. missing text java | missing customAttributeName working | missing class)</li>
     *
     *                  </ul>
     */
    @Then("on [$page] page wait until [$element] $condition")
    public void waitUntilCondition(String page, String element, ExpressionEvaluatingParameter<String> condition) {
        WebWaitConditionContext waitConditionCtx = WebWaitConditionContext.builder()
            .page(page)
            .element(element)
            .condition(condition.getValue())
            .value(parseConditionValue(condition.getValue()))
            .build();

        WebWaitCondition waitCondition = waitConditionResolver.resolveWaitCondition(waitConditionCtx);

        waitCondition.evaluate(waitConditionCtx);
    }

    /**
     * @deprecated because it's not opening new tab, only focus tab opened by another action
     *             getLastOpenedWindowHandler() may not work correctly on all browsers (https://developer.mozilla.org/en-US/docs/Web/WebDriver/Commands/GetWindowHandles)
     *             findTabWithUrlOrTitle() should be used instead
     */
    @Deprecated
    @Then("new tab is opened and focused")
    public void switchToNewTab() {
        assertThat(driver.getWindowHandles().size())
            .as("last tab remains, new was not opened")
            .isGreaterThan(0);
        driver.switchTo().window(getLastOpenedWindowHandler());
    }

    @Given("open and focus new tab")
    @Then("open and focus new tab")
    public void openAndFocusNewTab() {
        Set<String> handlesBefore = driver.getWindowHandles();
        if (driver instanceof JavascriptExecutor) {
            ((JavascriptExecutor) driver).executeScript("window.open()");
            Set<String> handlesAfter = driver.getWindowHandles();
            if (handlesAfter.size() == handlesBefore.size() + 1) {
                handlesAfter.removeAll(handlesBefore);
                driver.switchTo().window(handlesAfter.iterator().next());
                return;
            }
        }
        throw new AssertionError("Opening new tab failed");
    }

    @Given("tab with [$urlTitle] containing [$text] is focused")
    @Then("tab with [$urlTitle] containing [$text] is focused")
    public void findTabWithUrlOrTitle(String urlTitle, ExpressionEvaluatingParameter<String> text) {
        Assertions.assertThat(urlTitle).matches("url|title").as("Must be url or title");
        driver.getWindowHandles().stream()
            .filter(handle -> {
                try {
                    driver.switchTo().window(handle);
                } catch (Exception e) {
                    return urlTitlecontainsText(urlTitle, text.getValue());
                }
                return urlTitlecontainsText(urlTitle, text.getValue());
            })
            .findAny()
            .orElseThrow(IllegalStateException::new);
    }

    private boolean urlTitlecontainsText(String urlTitle, String text) {
        return urlTitle.contains("url") ? driver.getCurrentUrl().contains(text) : driver.getTitle().contains(text);
    }

    @Given("on page [$page] frame [$frame] is focused")
    @Then("on page [$page] frame [$frame] is focused")
    public void focusNamedFrame(ExpressionEvaluatingParameter<String> page, ExpressionEvaluatingParameter<String> frame) {
        WebElement iFrame = driver.findElement(elementRegistry.getLocator(page.getValue(), frame.getValue()));
        driver.switchTo().frame(iFrame);
    }

    @Given("main frame is focused")
    @Then("main frame is focused")
    public void focusMainFrame() {
        driver.switchTo().defaultContent();
    }

    @When("current tab is closed")
    public void closeTab() {
        driver.close();
        driver.switchTo().window(getLastOpenedWindowHandler());
    }

    @Given("browser is closed")
    public void closeBrowser() {
        driver.quit();
    }

    @When("navigated back")
    @Then("navigate back")
    public void navigateBack() {
        driver.navigate().back();
    }

    @When("navigated forward")
    @Then("navigate forward")
    public void navigateForward() {
        driver.navigate().forward();
    }

    private String parseConditionValue(String condition) {
        String[] conditionParts = condition.split(" ");
        return conditionParts.length >= 3 ? join(" ", asList(conditionParts).subList(2,conditionParts.length)) : null;
    }

    private Object resolvePropertyValue(String page, Map<String, String> row) {
        WebProperty property = propertyResolver.resolveProperty(row.get(PROPERTY));

        WebPropertyContext ctx = WebPropertyContext.builder()
            .page(page)
            .element(row.get(ELEMENT))
            .build();

        return property.value(ctx);
    }

    private String resolveVerifierName(Map<String, String> row) {
        String operator = VerifierNames.EQ;
        if (row.containsKey(ExampleTableConstraints.OPERATOR) && !row.get(ExampleTableConstraints.OPERATOR).isEmpty()) {
            operator = row.get(ExampleTableConstraints.OPERATOR);
        } else if (row.containsKey(ExampleTableConstraints.VERIFIER) && !row.get(ExampleTableConstraints.VERIFIER).isEmpty()) {
            operator = row.get(ExampleTableConstraints.VERIFIER);
        }
        return operator;
    }

    private String resolveHomePageUrl(String application) {
        WebSetting webSetting = qualifiedBeanOfType(beanFactory, WebSetting.class, application);
        CURRENT_SETTING.set(webSetting);
        return removeEnd(webSetting.getHomePageUrl(), "/");
    }

    private String getLastOpenedWindowHandler() {
        Set<String> handles = driver.getWindowHandles();
        assertThat(handles).as("no opened windows").isNotEmpty();
        return handles.stream().skip(handles.size() - 1).findFirst().get();
    }

}
