package org.jbehavesupport.core.internal;

import org.apache.commons.lang3.Validate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TableParameterAligner {

    private TableParameterAligner() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Aligns the table parameter in the specified string. The input string is expected to have only
     * one table, if more than one is present then the behavior is unpredictable.
     *
     * @param inputString table string
     * @return aligned table string
     */
    public static String alignTableInString(String inputString) {

        String[] lines = inputString.split("\n");
        List<String> alignedTableLines = getAlignedTableLines(Arrays.asList(lines));
        Validate.isTrue(lines.length == alignedTableLines.size(), "Aligned number of lines was different to that of non aligned lines");

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < alignedTableLines.size(); i++) {
            String alignedLine = alignedTableLines.get(i);
            sb.append(alignedLine);
            if (i != alignedTableLines.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }

    private static List<String> getAlignedTableLines(List<String> tableLines) {

        List<String> alignedLines = new ArrayList<>(tableLines.size());

        // work out the max column widths
        int[] maxColumnWidths = null;
        for (String line : tableLines) {
            if (line.startsWith("|--") || !line.startsWith("|")) {
                // "|--" no need to do anything with a comment line
                // "|" ignore non table lines to support functionality provided by alignTableInString()
                continue;
            }

            // for the split below to work correctly
            line = fillEmptyValues(line);

            String[] split = line.split("\\|");

            // remove the first element as it is always empty
            split = Arrays.copyOfRange(split, 1, split.length);
            if (maxColumnWidths == null) {
                maxColumnWidths = new int[split.length];
            }

            for (int i = 0; i < split.length; i++) {
                int fieldLength = split[i].trim().length();
                int maxFieldLength = maxColumnWidths[i];
                if (fieldLength > maxFieldLength) {
                    maxColumnWidths[i] = fieldLength;
                }
            }

        }

        return alignLines(tableLines, alignedLines, maxColumnWidths);
    }

    private static List<String> alignLines(List<String> tableLines, List<String> alignedLines, int[] maxColumnWidths) {
        for (String line : tableLines) {
            if (line.startsWith("|--") || !line.startsWith("|")) {
                // "|--" simply add comment line
                // "|" simply add non table lines to support functionality provided by alignTableInString() method above
                alignedLines.add(line);
                continue;
            }

            StringBuilder alignedLine = new StringBuilder();
            line = fillEmptyValues(line); // for the split below to work correctly
            String[] split = line.trim().split("\\|");
            // remove the first element as it is always empty
            split = Arrays.copyOfRange(split, 1, split.length);
            // add it back to the aligned line
            alignedLine.append("|");

            for (int i = 0; i < split.length; i++) {
                String field = split[i].trim();
                int maxFieldLength = maxColumnWidths.length < i ? 0 : maxColumnWidths[i];
                int dif = maxFieldLength - field.length();
                if (dif > 0) {
                    char[] padChars = new char[dif];
                    Arrays.fill(padChars, ' ');
                    field = field + new String(padChars);
                }
                alignedLine.append(field);
                alignedLine.append("|");
            }
            alignedLines.add(alignedLine.toString());
        }
        return alignedLines;
    }

    private static String fillEmptyValues(String line) {
        int i = line.indexOf("||");
        while (i != -1) {
            line = line.substring(0, i + 1) + " " + line.substring(i + 1);
            i = line.indexOf("||");
        }
        return line;
    }
}
