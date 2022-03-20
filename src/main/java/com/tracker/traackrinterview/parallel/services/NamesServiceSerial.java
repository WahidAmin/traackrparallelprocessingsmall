package com.tracker.traackrinterview.parallel.services;

import com.tracker.traackrinterview.parallel.domains.NameAndCount;
import com.tracker.traackrinterview.parallel.domains.NamesStat;
import com.tracker.traackrinterview.parallel.lineprocessors.LineProcessor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class NamesServiceSerial {
    private LineProcessor lineProcessor;
    public NamesServiceSerial(LineProcessor lineProcessor) {
        this.lineProcessor = lineProcessor;
    }

    public Optional<NamesStat> processNames(String pathString, int limit, int modifiedNamesCount) {
        Path path = Paths.get(pathString);
        try(Stream<String> lines = Files.lines(path)) {
            Map<String, Integer> fullNamesMap = new ConcurrentHashMap<String, Integer>();
            Map<String, Integer> firstNamesMap = new ConcurrentHashMap<>();
            Map<String, Integer> lastNamesMap = new ConcurrentHashMap<>();
            Timestamp startTimestamp = Timestamp.from(Instant.now());
            
            if (limit > 0) {
                lines
                    .limit(limit)
                    .forEach(line -> lineProcessor.processLine(line, fullNamesMap, firstNamesMap, lastNamesMap));
            } else {
                lines
                    .forEach(line -> lineProcessor.processLine(line, fullNamesMap, firstNamesMap, lastNamesMap));
            }

            NamesStat namesStat = generateStats(fullNamesMap, firstNamesMap, lastNamesMap, modifiedNamesCount);
            Timestamp endTimestamp = Timestamp.from(Instant.now());
            namesStat.setExecutionDuration(endTimestamp.getTime() - startTimestamp.getTime());

            return Optional.of(namesStat);

        } catch (IOException ioException) {
            //TODo: handle or rethrow
            ioException.printStackTrace();
        }

        return Optional.empty();
    }

    private NamesStat generateStats(Map<String, Integer> fullNamesMap, Map<String, Integer>
            firstNamesMap, Map<String, Integer> lastNamesMap, int modifiedNamesCount) {

        NamesStat namesStat = new NamesStat();
        namesStat.setUniqueFirstNameCardinality(firstNamesMap.keySet().size());
        namesStat.setUniqueLastNameCardinality(lastNamesMap.keySet().size());
        namesStat.setUniqueFullNameCardinality(fullNamesMap.keySet().size());

        namesStat.setTopTenFistNames(getTopTen(firstNamesMap));
        namesStat.setTopTenLastNames(getTopTen(lastNamesMap));
        namesStat.setTopTenFullNames(getTopTen(fullNamesMap));

        namesStat.setModifiedFullNames(processModifiedNames(fullNamesMap, firstNamesMap, lastNamesMap, modifiedNamesCount));

        return namesStat;
    }

    //TODO: Could be moved into it's own util class
    private List<String> processModifiedNames(Map<String, Integer> fullNamesMap, Map<String, Integer> firstNamesMap,
                                              Map<String, Integer> lastNamesMap, int modifiedNamesCount ) {
        Set<String> modifiedNamesSet = new HashSet<>();

        LinkedList<String> firstNamesList =
            firstNamesMap
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));

        LinkedList<String> lastNamesList =
            lastNamesMap
                .entrySet()
                .stream()
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));

        // Since the data is huge, I could go to try name generation x numbers of times, where x is sum of the size of lastNamesList and firstNamesList instead of
        // trying y numbers of times, where y is size of lastNamesList multiplied by firstNamesList size.
        //This could have some margin of errors but not critical.
        int maxNumOfTries = lastNamesList.size() * firstNamesList.size();

        while (modifiedNamesCount > 0 && maxNumOfTries > 0 && lastNamesList.size() > 0 && firstNamesList.size() > 0) {
            int lastNameIndex = new Random().nextInt(lastNamesList.size());
            int firstNameIndex = new Random().nextInt(firstNamesList.size());

            String fullName = new StringBuilder(lastNamesList.get(lastNameIndex)).append(", ").append(firstNamesList.get(firstNameIndex)).toString();
            if (!modifiedNamesSet.contains(fullName) && !fullNamesMap.containsKey(fullName)) {
                modifiedNamesSet.add(fullName);
                firstNamesList.remove(firstNameIndex);
                lastNamesList.remove(lastNameIndex);
                modifiedNamesCount--;
                maxNumOfTries--;
            } else {
                maxNumOfTries--;
            }
        }

        return new LinkedList<>(modifiedNamesSet);
    }

    private List<NameAndCount> getTopTen(Map<String, Integer> namesMap) {
        return
            namesMap
                .entrySet()
                .stream()
                .sorted((entrySetA, entrySetB) -> entrySetB.getValue().compareTo(entrySetA.getValue()))
                .limit(10)
                .map(entrySet -> new NameAndCount(entrySet.getKey(), entrySet.getValue()))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
