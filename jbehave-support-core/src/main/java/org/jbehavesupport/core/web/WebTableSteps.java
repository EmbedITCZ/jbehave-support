package org.jbehavesupport.core.web;

import junit.framework.AssertionFailedError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jbehave.core.annotations.Then;
import org.jbehave.core.model.ExamplesTable;
import org.jbehave.core.steps.Row;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebTableSteps {

    private static final String TEXT_CONTENT = "textContent";

    private final WebElementLocator elementLocator;

    @Then("on [$page] page the table [$tableName] contains exactly the following data:$expectedData")
    public void tableContainsExactlyFollowingData(String page, String tableName, ExamplesTable data) {
        List<Map<String, String>> expectedTableData = data.getRowsAsParameters().stream()
            .map(Row::values)
            .collect(Collectors.toList());
        verifyTableContainsExactlyFollowingData(
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
            page,
            tableName,
            expectedTableData,
            false);
    }

    private List<Map<String, String>> convertHtmlTableToListOfMaps(WebElement table, Collection<String> relevantHeaders, int startRow, int endRow) {
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
        endRow = (endRow == -1) ? tableRows.size() - 1 : endRow + 1;

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

    private void verifyTableContainsFollowingDataInSpecifiedRow(
        String page,
        String tableName,
        Map<String, String> expectedTableData,
        int rowNumber) {
        verifyTableContainsFollowingDataInSpecifiedRows(
            page,
            tableName,
            Collections.singletonList(expectedTableData),
            rowNumber,
            rowNumber);
    }

    private void verifyTableContainsFollowingDataInSpecifiedRows(
        String page,
        String tableName,
        List<Map<String, String>> expectedTableData,
        int startRow,
        int endRow) {
        Collection<String> relevantHeaders = expectedTableData.get(0).keySet();
        WebElement table = elementLocator.findElement(page, tableName);
        List<Map<String, String>> htmlTableData = convertHtmlTableToListOfMaps(table, relevantHeaders, startRow, endRow);
        for (int rowNumber = 0; rowNumber <= endRow - startRow; rowNumber++) {
            Map<String, String> expectedRow = expectedTableData.get(rowNumber);
            for (String header : expectedRow.keySet()) {
                assertThat(htmlTableData.get(rowNumber).get(header))
                    .as("table cell on row '" + (rowNumber + startRow + 1) + "' and column '" + header + "' should contain '" + expectedRow.get(header)
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
        String page,
        String tableName,
        List<Map<String, String>> expectedTableData,
        boolean strictOrder) {
        Collection<String> relevantHeaders = expectedTableData.get(0).keySet();
        WebElement table = elementLocator.findElement(page, tableName);
        List<Map<String, String>> htmlTableData = convertHtmlTableToListOfMaps(table, relevantHeaders, -1, -1);

        assertThat(htmlTableData.size())
            .as("expected number of rows doesn't match")
            .isEqualTo(expectedTableData.size());

        compareListOfMaps(expectedTableData, htmlTableData, strictOrder);
    }

    private void verifyTableContainsAtLeastFollowingData(
        String page,
        String tableName,
        List<Map<String, String>> expectedTableData) {
        Collection<String> relevantHeaders = expectedTableData.get(0).keySet();
        WebElement table = elementLocator.findElement(page, tableName);
        List<Map<String, String>> foundTableData = convertHtmlTableToListOfMaps(table, relevantHeaders, -1, -1);
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

}
