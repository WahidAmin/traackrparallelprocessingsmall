package com.tracker.traackrinterview.parallel.lineprocessors;

import java.util.Map;

public class CommaAndDashLineProcessor implements LineProcessor {
    // TODO: handle exception or rethrows
    @Override
    public void processLine(String line, Map<String, Integer> fullNamesMap,
                             Map<String, Integer> firstNamesMap, Map<String, Integer> lastNamesMap) {
        if (!line.startsWith(" ")) {
            int indexOfComma = line.indexOf(',');
            String lastName = line.substring(0, indexOfComma).trim();
            String firstName = line.substring(indexOfComma + 1, line.indexOf('-')).trim();
            String fullName = line.substring(0, line.indexOf('-')).trim();
            // Names must start with capital letters
            if (Character.isUpperCase(firstName.charAt(0)) && Character.isUpperCase(firstName.charAt(0)) &&
                    Character.isUpperCase(lastName.charAt(0)) && Character.isUpperCase(lastName.charAt(0)) &&
                    firstName.matches("[a-zA-Z]+") && lastName.matches("[a-zA-Z]+") &&
                    fullName.contains(",")) {

                if (firstNamesMap.containsKey(firstName)) {
                    Integer count = firstNamesMap.get(firstName) + 1;
                    firstNamesMap.put(firstName, count);
                } else {
                    firstNamesMap.put(firstName, 1);
                }

                if (lastNamesMap.containsKey(lastName)) {
                    Integer count = lastNamesMap.get(lastName) + 1;
                    lastNamesMap.put(lastName, count);
                } else {
                    lastNamesMap.put(lastName, 1);
                }

                if (fullNamesMap.containsKey(fullName)) {
                    Integer count = fullNamesMap.get(fullName) + 1;
                    fullNamesMap.put(fullName, count);
                } else {
                    fullNamesMap.put(fullName, 1);
                }
            }
        }
    }
}
