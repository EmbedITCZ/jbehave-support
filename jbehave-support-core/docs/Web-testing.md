[Contents](../README.md)

## WEB testing

WEB steps allow interaction with a GUI of a web application. They are built upon the Selenium project.

### Configuration

For each web application used in the test a bean of type WebSetting needs to be added to the Spring context.
It's necessary to provide three parameters for WebSetting:
- homepage URL - this URL will be used to open the application in a browser
- rendering framework - some steps' behaviour depends on the way the page is rendered.
At the moment we support Wicket and Simple rendering.
HTML tables rendered by Wicket are more complex than simple HTML tables and the renderer feature tries to abstract some of that complexity away.
- location of elements' mapping files

```
@Bean
@Qualifier("MYAPP")
public WebSetting webSetting() {
    return WebSetting.builder()
        .homePageUrl(myappWebUrl)
        .htmlRenderer(WebTableSteps.HtmlRenderer.SIMPLE)
        .elementLocatorsSource("/ui-mapping/*.yaml")
        .build();
}
```

#### Optional configuration properties

Default behavior can be influenced by several properties:

- `web.timeout` - Sets the timeout for Web Driver operation, defaults to `10` seconds if not set
    - timeout is used for webdriver implicitlyWait, setScriptTimeout, pageLoadTimeout
- `web.browser` - Sets the browser which will be used, default supported value is only `chrome` (custom values can be optionally used as well, for more info see [custom browser support](#custom-browser-support)).
Defaults to `chrome` if not set. 
- `web.browser.driver.location` - Sets the absolute path to the webdriver on disk, if not set then Web Driver is downloaded from the Internet.
- `web.browser.driver.version` - Sets the Web Driver version of the browser used, defaults to the latest available version (not used if `web.browser.driver.location` is set, or `firefox47` is used as a browser)
- `web.browser.driver.port` - Sets the port on which the Web Driver runs, defaults to any free port. (supported only for chrome)
- `web.browser.driver.startup.arguments` - Sets browser startup arguments.

#### Custom browser support

If the supplied browser options mentioned [above](#optional-configuration-properties) are not sufficient (or there is a need to use a different browser than chrome),
custom `RemoteWebDriver` implementation can be used by registering a bean implementing the `WebDriverFactory` interface.
After that you need to set the property `web.browser` to the name you specified in your implementation of `WebDriverFactory`.
Note: If you use your custom `WebDriverFactory` then the properties [above](#optional-configuration-properties) (with the exception of `web.browser`) will not influence anything.

TODO: screenshot info

### Mapping files

JBehave-support uses web element mapping files to provide comprehensible names for css or xpath mapping.
Element has to be defined under a page in a [configuration yaml file](#mapping-files).

Here is an example of a mapping yaml file:
```
home:
  contractNumber.input.css:      "input[data-uid='contract-number']"
  cuid.input.css:                "input[data-uid='cuid']"
  contractNumber.label.xpath:    "//*[@id='content']/div/div/form/div[2]/div/div/ul/li/label"
  cuid.label.xpath:              "//*[@id='content']/div/div/form/div[2]/div/div/ul/li[4]/label"
  search.button.css:             "input[data-uid='button-panel:search']"
  reset.button.css:              "input[data-uid='button-panel:reset']"
  error.0.label.css:             "span[data-uid='errors:feedbackul:0:nav:message']"
  error.1.label.css:             "span[data-uid='errors:feedbackul:1:nav:message']"
```

The top-most element "home" is used as a page name in the UI steps while the middle level elements such "contractNumber.input" are used as comprehensible element names.
The lowest level elements (css or xpath) are used to determine which type of selector is used. The value after the colon (:) then is the selector itself.
The top element has to be unique per tested application. The middle level element names have to be unique per "page".

Special elements (can't be defined) are `@url` and `@title`. Those elements don't support performing actions on them and have only text and value properties.

#### Implicit mapping by ID

For simple CSS mapping by element ID an explicit mapping does not need to be defined in the mapping file, and it can be used directly in the story.
Such mapping needs to start with a `#` sign, e.g.
```
When on [home] page these actions are performed:
| element    | action | data  |
| #search-id | FILL   | 85    |
```

In the example above `#search-id` references HTML element with property `id="search-id"`.

#### Custom mapping types

The mapping files by default support only CSS and XPATH selectors, but it is possible to use a custom selectors by registering a bean implementing the `ByFactory` interface.
E.g. a custom bean for searching by id might look something like this:

```
@Component
public class IdByFactory implements ByFactory {
    @Override
    public By getBy(String value) { return By.id(value); }

    @Override
    public String getType() { return "id"; }
}  
```

And mapping that uses the above factory in the yaml file could then look like:
```
search.button.id: "search-button"
```

### WEB steps

#### Opening page in a browser

One of the following two steps should be the first one used when testing web application.

```
Given [MYAPP] homepage is open
```

```
When [MYAPP] url is open with path [/admin] and query string parameters:
| name | data           |
| user | {CP:USER_NAME} |
```

```
When [MYAPP]/[?token=5kF1vKJD%2BELhBSByQr0fR2Ai0N65780MUMdMbaLD0%2Bk=] url is open
```

```
When [https://www.google.com] url is open
```

The steps use the homePageUrl parameter of the WebSetting instance from the Spring context to open a browser on that URL.
In case of the second step the homePageUrl is appended with the path in brackets and query string parameters.
The third step would open the URL of the MYAPP system with a specific path - second parameter in brackets.
The fourth step opens a specified URL with no reference to any application in the configuration.

#### Browser navigation

To navigate backwards/forwards in the browser the following steps can be used:
```
When navigated back
Then navigate back
```
```
When navigated forward
Then navigate forward
```

To focus iframes and return to main frame.
```
Given on page [home] frame [iframe] is focused
Then on page [home] frame [iframe] is focused
```
```
Given main frame is focused
Then main frame is focused
```

To open a new tab. (works only in browsers with javascript enabled)
```
Given open and focus new tab
Then open and focus new tab
```

To focus any opened tab using part of its URL or title
```
Given tab with [url] containing [google] is focused
Then tab with [title] containing [google] is focused
```

To close current tab or whole browser
```
When current tab is closed
```
```
Given browser is closed
```

To switch to another browser (midstory, this will close the current browser automatically)
```
When browser is changed to [$browserName]
```
```
Given browser is changed to [$browserName]
```
#### Performing an action on HTML elements

To perform an action on a page use the following step.

```
When on [home] page these actions are performed:
| element       | action | data           |
| search.input  | FILL   | {CP:USER_NAME} |
| search.button | CLICK  |                |
```

This step will find the mapping for the `search.input` element and perform the `FILL` action with the `USER_NAME` value stored in test context on it.
Similarly it will handle the `search.button` element and the `CLICK` action.

There are the following actions available at the moment:
- ACCEPT - for accepting alert dialog
- DISMISS - for dismissing alert dialog
- CLICK
- DOUBLE_CLICK
- FORCE_CLICK - for clicking on enabled but invisible elements (works only in browsers with javascript enabled)
- FILL - for inserting text value
- CLEAR - for clearing text value of input/textarea
- PRESS - for pressing special keys from the org.openqa.selenium.Keys enum
- SELECT - for selecting a value in an HTML select and checkbox input
- SCROLL_ON - for scrolling on element

For building examples table with actions programmatically it is possible to use `WebActionBuilder`.

##### Creating a custom WebAction

We support using a custom implemented actions. To create your own custom action simply register a new bean implementing the interface `org.jbehavesupport.core.web.WebAction`.  

String returned by the method `String name()` is the name of the action used in stories (e.g. `TRIPPLE_CLICK`) 
and the method `void perform(WebActionContext ctx)` should contain your custom action logic itself. 

#### Verifying HTML elements' properties

To verify element properties on a page use the following step:

```
Then on [home] page these conditions are verified:
| element     | property | data     |
| client.id   | TEXT     | 123      |
| client.name | TEXT     | John Doe |
```

This step will find the mapping for the home.client.id element and verify that the TEXT property of that element is equal to "123".
There are the following element properties available:
- ENABLED
- SELECTED
- DISPLAYED
- DISPLAYED_ON_SCREEN (visible inside of bounds of the screen, works only with javascript browsers)
- TEXT
- VALUE
- ROW_COUNT
- CLASS
- SELECTED_TEXT
- EDITABLE

I'll leave it to the imagination of the inquiring to figure out what these properties mean.

The step can be used with the optional 'verifier' column where it's possible to specify any of the verifiers from [General info - Verification - Comparison verifiers](General.md).

```
Then on [home] page these conditions are verified:
| element        | property | data    | verifier |
| client.name    | TEXT     | John    | CONTAINS |
```

#### Saving element values to test context

Sometimes there's a need to save element values (or other properties) into the test context for further usage.
The following step does just that.

```
Then on [home] page these values are saved:
| element    | property | contextAlias   |
| client.id  | VALUE    | ID             |
| client.ssn | VALUE    | SSN            |
```

#### Waiting until a condition is met
If you want to wait on a certain page, until a specified condition is met, use the `Then on [page] page wait until [element] [condition]` step.

Supported conditions are:
* `is present`
* `is clickable`
* `is visible`
* `is not visible`
* `has [attribute] [attributeValue]` (i.e. has text someText | has class issue | has id)
* `missing [attribute] [attributeValue]` (i.e. missing text java | missing customAttributeName working | missing class)

Example:
```
Given [TEST]/[https://www.google.com] url is open
Then on [home] page wait until [@title] has text Google
```
For more examples see [WebWaitCondition.story](../src/test/groovy/org/jbehavesupport/test/sample/WebWaitCondition.story)

#### Taking screenshot

To take screenshot 
```
Then screenshot is taken
When screenshot is taken
```

If you want to take screenshot from your custom web steps inject `ApplicationEventPublisher` and publish `WebScreenshotEvent`
```
import org.jbehavesupport.core.web.WebScreenshotType;

@Autowired
ApplicationEventPublisher applicationEventPublisher;

public void myMethod() {
    ...
    applicationEventPublisher.publishEvent(new WebScreenshotEvent(this, WebScreenshotType.MANUAL));
    ...
}
```

If you want to see screenshots in report register `ScreenshotReporterExtension`
* WebScreenshotType has to be `MANUAL` or correspond with chosen mode (see property `web.screenshot.reporting.mode` in [Reporting](Reporting.md)), otherwise screenshot won't be taken 
TODO: add explanation of table steps
