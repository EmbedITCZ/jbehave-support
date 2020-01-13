package org.jbehavesupport.core.web;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Given;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @deprecated(since = "1.0.0", forRemoval = true) support for general usable table steps may be added in a future version and it is not guaranteed that it will be backwards compatible
 */
@Slf4j
@Component
@RequiredArgsConstructor
@Deprecated
public class WebTableSteps {

    private static final String TEXT_CONTENT = "textContent";
    private static final int MAXIMUM_LOCATE_ATTEMPTS = 2;

    public enum HtmlRenderer {
        SIMPLE,
        WICKET
    }

    private final WebDriver driver;
    private final WebElementRegistry elementRegistry;

    @Autowired(required = false)
    private List<WebSetting> webSettings;

    @Given("on [$page] page in table [$table] row $rowNumber, column $columnNumber is clicked")
    public void clickCell(String page, String tableName, int rowNumber, int columnNumber) {
        WebElement table = findElementByPageAndName(page, tableName);
        List<WebElement> tableRows =
            table.findElement(By.className("imxt-body")).findElements(By.className("imxt-grid-row"));
        List<WebElement> row = tableRows.get(rowNumber - 1).findElements(By.tagName("td"));
        WebElement td = row.get(columnNumber - 1);
        td.findElement(By.tagName("a")).click();
    }

    @Then("on [$page] page the table [$tableName] contains exactly the following data:$expectedData")
    public void tableContainsExactlyFollowingData(String page, String tableName, ExamplesTable data) {
        List<Map<String, String>> expectedTableData = data.getRowsAsParameters().stream()
            .map(Row::values)
            .collect(Collectors.toList());
        verifyTableContainsExactlyFollowingData(
            WebSteps.getCurrentSetting().getHtmlRenderer(),
            page,
            tableName,
            expectedTableData,
            true);
    }

    @Then("on [$page] page the table [$tableName] contains in row $rowNumber the following data:$expectedData")
    public void tableContainsFollowingDataInRow(String page, String tableName, int rowNumber, ExamplesTable expectedData) {
        List<Map<String, String>> expectedTableData = expectedData.getRowsAsParameters().stream()
            .map(Row::values)
            .collect(Collectors.toList());
        verifyTableContainsFollowingDataInSpecifiedRow(
            WebSteps.getCurrentSetting().getHtmlRenderer(),
            page,
            tableName,
            expectedTableData.get(0),
            rowNumber - 1);
    }

    @Then("on [$page] page the table [$tableName] contains in rows $startRow to $endRow the following data:$expectedData")
    public void tableContainsFollowingDataInRow(String page, String tableName, int startRow, int endRow, ExamplesTable expectedData) {
        List<Map<String, String>> expectedTableData = expectedData.getRowsAsParameters().stream()
            .map(Row::values)
            .collect(Collectors.toList());
        verifyTableContainsFollowingDataInSpecifiedRows(
            WebSteps.getCurrentSetting().getHtmlRenderer(),
            page,
            tableName,
            expectedTableData,
            startRow - 1,
            endRow - 1);
    }

    @Then("on [$page] page the table [$tableName] contains the following data:$expectedData")
    public void tableContainsFollowingData(String page, String tableName, ExamplesTable expectedData) {
        List<Map<String, String>> expectedTableData = expectedData.getRowsAsParameters().stream()
            .map(Row::values)
            .collect(Collectors.toList());
        verifyTableContainsAtLeastFollowingData(
            WebSteps.getCurrentSetting().getHtmlRenderer(),
            page,
            tableName,
            expectedTableData);
    }

    @Then("on [$page] page the table [$tableName] contains all of the following data regardless of order:$expectedData")
    public void tableContainsFollowingDataWithNoOrder(String page, String tableName, ExamplesTable expectedData) {
        List<Map<String, String>> expectedTableData = expectedData.getRowsAsParameters().stream()
            .map(Row::values)
            .collect(Collectors.toList());
        verifyTableContainsExactlyFollowingData(
            WebSteps.getCurrentSetting().getHtmlRenderer(),
            page,
            tableName,
            expectedTableData,
            false);
    }

    @SuppressWarnings("squid:S1166")
    private WebElement findElementByPageAndName(String page, String elementName, int maxLocateAttempts) {
        int currentAttempt = 1;
        do {
            waitForLoad();
            try {
                return driver.findElement(elementRegistry.getLocator(page, elementName));
            } catch (NoSuchElementException e) {
                log.error("Missing reference for element: {}, attempt: {} of {}", elementName, currentAttempt++, maxLocateAttempts);
            }
        } while (currentAttempt <= maxLocateAttempts);
        throw new NoSuchElementException(page + ": " + elementName);
    }

    private WebElement findElementByPageAndName(String page, String elementName) {
        return findElementByPageAndName(page, elementName, MAXIMUM_LOCATE_ATTEMPTS);
    }

    private List<Map<String, String>> convertSimpleTableToListOfMaps(WebElement table, Collection<String> relevantHeaders, int startRow, int endRow) {
        HashMap<Integer, String> headers = new HashMap<>();
        List<WebElement> tableHeaders = table.findElement(By.tagName("thead")).findElement(By.tagName("tr")).findElements(By.tagName("th"));
        int index = 0;
        for (WebElement th : tableHeaders) {
            headers.put(index++, th.getAttribute(TEXT_CONTENT).trim());
        }

        for (String relevantHeader : relevantHeaders) {
            assertThat(headers.containsValue(relevantHeader)).as("Column '%s' was not found in table.", relevantHeader).isTrue();
        }

        List<Map<String, String>> tableData = new ArrayList<>();
        List<WebElement> tableRows = table.findElements(By.tagName("tbody")).stream()
            .flatMap(e -> e.findElements(By.tagName("tr")).stream())
            .collect(Collectors.toList());

        startRow = (startRow == -1) ? 0 : startRow;
        endRow = (endRow == -1) ? tableRows.size() - 1 : endRow;

        for (int rowNumber = startRow; rowNumber <= endRow - startRow; rowNumber++) {
            WebElement tr = tableRows.get(rowNumber);
            List<WebElement> row = tr.findElements(By.tagName("td"));
            HashMap<String, String> rowData = new HashMap<>();
            for (Map.Entry<Integer, String> entry : headers.entrySet()) {
                if (relevantHeaders == null || relevantHeaders.contains(entry.getValue())) {
                    WebElement td = row.get(entry.getKey());
                    rowData.put(entry.getValue(), td.getText());
                }
            }

            tableData.add(rowData);
        }

        return tableData;
    }

    private List<Map<String, String>> convertWicketTableToListOfMaps(WebElement table, Collection<String> relevantHeaders, int startRow, int endRow) {
        ArrayList<String> headers = new ArrayList<>();
        List<WebElement> tableHeaders = table.findElement(By.className("imxt-head")).findElement(By.tagName("tr")).findElements(By.tagName("th"));
        for (WebElement th : tableHeaders) {
            List<WebElement> headingElement = th.findElements(By.xpath("div/div/a/div/div"));
            if (headingElement.size() == 1) {
                String headerText = headingElement.get(0).getAttribute(TEXT_CONTENT);
                headers.add(headerText);
            } else {
                break;
            }
        }

        List<Map<String, String>> tableData = new ArrayList<>();
        List<WebElement> tableRows = table.findElement(By.className("imxt-body")).findElements(By.className("imxt-grid-row"));
        startRow = (startRow == -1) ? 0 : startRow;
        if (endRow == -1) {
            endRow = tableRows.size() - 1;
        }

        for (int rowNumber = startRow; rowNumber <= endRow - startRow; rowNumber++) {
            WebElement tr = tableRows.get(rowNumber);
            List<WebElement> row = tr.findElements(By.tagName("td"));
            HashMap<String, String> rowData = new HashMap<>();
            for (int i = 0; i < headers.size(); i++) {
                if (relevantHeaders == null || relevantHeaders.contains(headers.get(i))) {
                    WebElement td = row.get(i);
                    rowData.put(headers.get(i), td.findElement(By.tagName("div")).getAttribute(TEXT_CONTENT));
                }
            }
            tableData.add(rowData);
        }

        return tableData;
    }

    private void verifyTableContainsFollowingDataInSpecifiedRow(
        HtmlRenderer htmlRenderer,
        String page,
        String tableName,
        Map<String, String> expectedTableData,
        int rowNumber) {
        verifyTableContainsFollowingDataInSpecifiedRows(
            htmlRenderer,
            page,
            tableName,
            Collections.singletonList(expectedTableData),
            rowNumber,
            rowNumber);
    }

    private void verifyTableContainsFollowingDataInSpecifiedRows(
        HtmlRenderer htmlRenderer,
        String page,
        String tableName,
        List<Map<String, String>> expectedTableData,
        int startRow,
        int endRow) {
        Collection<String> relevantHeaders = expectedTableData.get(0).keySet();
        WebElement table = findElementByPageAndName(page, tableName);
        List<Map<String, String>> htmlTableData = convertHtmlTableToListOfMaps(htmlRenderer, table, relevantHeaders, startRow, endRow);
        for (int rowNumber = startRow; rowNumber <= endRow - startRow; rowNumber++) {
            Map<String, String> expectedRow = expectedTableData.get(rowNumber);
            for (String header : expectedRow.keySet()) {
                assertThat(htmlTableData.get(rowNumber).get(header))
                    .as("table cell on row '" + (rowNumber + 1) + "' and column '" + header + "' should contain '" + expectedRow.get(header)
                        + "'. It contains '" + htmlTableData.get(rowNumber).get(header) + "'.")
                    .isEqualTo(expectedRow.get(header));
            }
        }
    }

    /**
     * returns the row which matches the item in the list
     */
    private int listContainsItem(final List<Map<String, String>> list, Map<String, String> item, Set<Integer> skipRows) {
        boolean verificationResult = false;
        for (int i = 0; i < list.size(); i++) {
            if (!skipRows.contains(i)) {
                Map<String, String> actualRow = list.get(i);
                for (String header : actualRow.keySet()) {
                    verificationResult = item.get(header).equals(actualRow.get(header));
                    if (!verificationResult) {
                        break;
                    }
                }
                if (verificationResult) {
                    return i;
                }
            }
        }
        return -1;
    }

    private void compareListOfMaps(final List<Map<String, String>> expectedTableData, final List<Map<String, String>> htmlTableData, boolean strictOrder) {
        if (strictOrder) {
            for (int i = 0; i < expectedTableData.size(); i++) {
                Map<String, String> row = expectedTableData.get(i);
                for (String header : row.keySet()) {
                    boolean verificationResult = expectedTableData.get(i).get(header).equals(htmlTableData.get(i).get(header));
                    assertThat(verificationResult).
                        as("table cell on row '" + (i + 1) + "' and column '" + header + "' should contain '" + expectedTableData.get(i).get(header)
                            + "'. It contains '" + htmlTableData.get(i).get(header) + "'.")
                        .isTrue();
                }
            }
        } else {
            HashSet<Integer> foundRows = new HashSet<>();
            for (int i = 0; i < expectedTableData.size(); i++) {
                Map<String, String> row = expectedTableData.get(i);
                int foundIndex = listContainsItem(htmlTableData, row, foundRows);
                if (foundIndex != -1) {
                    foundRows.add(foundIndex);
                } else {
                    throw new AssertionFailedError("Row " + (i + 1) + " not found");
                }
            }
        }
    }

    private void verifyTableContainsExactlyFollowingData(
        HtmlRenderer htmlRenderer,
        String page,
        String tableName,
        List<Map<String, String>> expectedTableData,
        boolean strictOrder) {
        Collection<String> relevantHeaders = expectedTableData.get(0).keySet();
        WebElement table = findElementByPageAndName(page, tableName);
        List<Map<String, String>> htmlTableData = convertHtmlTableToListOfMaps(htmlRenderer, table, relevantHeaders, -1, -1);

        assertThat(htmlTableData.size())
            .as("expected number of rows doesn't match")
            .isEqualTo(expectedTableData.size());

        compareListOfMaps(expectedTableData, htmlTableData, strictOrder);
    }

    private void verifyTableContainsAtLeastFollowingData(
        HtmlRenderer htmlRenderer,
        String page,
        String tableName,
        List<Map<String, String>> expectedTableData) {
        Collection<String> relevantHeaders = expectedTableData.get(0).keySet();
        WebElement table = findElementByPageAndName(page, tableName);
        List<Map<String, String>> foundTableData = convertHtmlTableToListOfMaps(htmlRenderer, table, relevantHeaders, -1, -1);
        verifyFoundDataContainExpectedData(expectedTableData, foundTableData);
    }

    private void verifyFoundDataContainExpectedData(final List<Map<String, String>> expectedData, final List<Map<String, String>> foundData) {
        HashSet<Integer> foundRows = new HashSet<>();
        for (int i = 0; i < expectedData.size(); i++) {
            Map<String, String> row = expectedData.get(i);
            int foundIndex = listContainsItem(foundData, row, foundRows);
            if (foundIndex != -1) {
                foundRows.add(foundIndex);
            } else {
                throw new AssertionFailedError("row " + (i + 1) + " not found");
            }
        }
    }

    // TODO: think of a better way to handle element parsing for different rendering technologies (Aurelia, Wicket)
    private List<Map<String, String>> convertHtmlTableToListOfMaps(
        final HtmlRenderer htmlRenderer,
        final WebElement table,
        final Collection<String> relevantHeaders,
        int startRow,
        int endRow) {
        List<Map<String, String>> htmlTableData;
        switch (htmlRenderer) {
            case SIMPLE:
                htmlTableData = convertSimpleTableToListOfMaps(table, relevantHeaders, startRow, endRow);
                break;
            case WICKET:
                htmlTableData = convertWicketTableToListOfMaps(table, relevantHeaders, startRow, endRow);
                break;
            default:
                throw new IllegalArgumentException("unknown renderer");
        }
        return htmlTableData;
    }

    private void waitForLoad() {
        new WebDriverWait(driver, 10).until((ExpectedCondition<Boolean>) wd ->
            ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete"));
        webSettings.stream()
            .forEach(s -> s.getWaitForLoad().accept(driver));
    }

}
