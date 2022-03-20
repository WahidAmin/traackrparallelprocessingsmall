package com.tracker.traackrinterview.parallel.lineprocessors;

import java.util.Map;

public interface LineProcessor {
    //TODO: the method should return value rather than having side effects- Will fix
    void processLine(String line, Map<String, Integer> fullNamesMap,
                             Map<String, Integer> firstNamesMap, Map<String, Integer> lastNamesMap);
}
