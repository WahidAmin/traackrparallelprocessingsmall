package com.tracker.traackrinterview.parallel.multitasking;

import com.tracker.traackrinterview.parallel.lineprocessors.LineProcessor;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.stream.Stream;

public class NamesCallable implements Callable<Optional<LinkedList<Map<String, Integer>>>> {
    private LineProcessor lineProcessor;
    private String filePath;

    public NamesCallable(String filePath, LineProcessor lineProcessor) {
        this.lineProcessor = lineProcessor;
        this.filePath = filePath;
    }

    @Override
    public Optional<LinkedList<Map<String, Integer>>> call() throws Exception {
        Path path = Paths.get(filePath);
        try(Stream<String> lines = Files.lines(path)) {
            Map<String, Integer> fullNamesMap = new HashMap<>();
            Map<String, Integer> firstNamesMap = new HashMap<>();
            Map<String, Integer> lastNamesMap = new HashMap<>();
            lines
                .unordered()
                .forEach(line -> lineProcessor.processLine(line, fullNamesMap, firstNamesMap, lastNamesMap));

            LinkedList<Map<String, Integer>> list = new LinkedList<>();
            list.add(firstNamesMap);
            list.add(lastNamesMap);
            list.add(fullNamesMap);

            return Optional.of(list);
        } catch (IOException ioException) {
            //TODo: handle or rethrow
            ioException.printStackTrace();
        }

        return Optional.empty();
    }
}
