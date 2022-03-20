package com.tracker.traackrinterview.parallel.services;

import com.tracker.traackrinterview.parallel.domains.NameAndCount;
import com.tracker.traackrinterview.parallel.domains.NamesStat;
import com.tracker.traackrinterview.parallel.lineprocessors.LineProcessor;
import com.tracker.traackrinterview.parallel.multitasking.NamesCallable;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class NamesServiceMultiThreading {
    private LineProcessor lineProcessor;
    public NamesServiceMultiThreading(LineProcessor lineProcessor) {
        this.lineProcessor = lineProcessor;
    }

    public Optional<NamesStat> processNames(String[] pathsString, int modifiedNamesCount) {

        try {
            NamesCallable namesCallableShard1Machine1 = new NamesCallable(pathsString[0], lineProcessor);
            NamesCallable namesCallableShard2Machine2 = new NamesCallable(pathsString[1], lineProcessor);

            ExecutorService executorService = Executors.newFixedThreadPool(2);

            Timestamp startTimestamp = Timestamp.from(Instant.now());

            List<Future<Optional<LinkedList<Map<String, Integer>>>>> futures =
                    executorService.invokeAll(Arrays.asList(namesCallableShard1Machine1, namesCallableShard2Machine2));

            Optional<LinkedList<Map<String, Integer>>> r1Optional = futures.get(0).get();
            Optional<LinkedList<Map<String, Integer>>> r2Optional = futures.get(1).get();

            if (r1Optional.isPresent() && r2Optional.isPresent()) {
                //Merge
                final Map<String, Integer> firstNamesMap = r1Optional.get().get(0);
                final Map<String, Integer> lastNamesMap = r1Optional.get().get(1);
                final Map<String, Integer> fullNamesMap = r1Optional.get().get(2);

                r2Optional.get().get(0)
                    .entrySet()
                    .stream()
                    .iterator()
                    .forEachRemaining(entry -> merge(entry, firstNamesMap));

                r2Optional.get().get(1)
                    .entrySet()
                    .stream()
                    .iterator()
                    .forEachRemaining(entry -> merge(entry, lastNamesMap));

                r2Optional.get().get(2)
                    .entrySet()
                    .stream()
                    .iterator()
                    .forEachRemaining(entry -> merge(entry, fullNamesMap));

                NamesStat namesStat = generateStats(fullNamesMap, firstNamesMap, lastNamesMap, modifiedNamesCount);
                Timestamp endTimestamp = Timestamp.from(Instant.now());
                namesStat.setExecutionDuration(endTimestamp.getTime() - startTimestamp.getTime());

                return Optional.of(namesStat);
            }

        } catch (InterruptedException | ExecutionException exception) {
            //TODo: handle or rethrow
            exception.printStackTrace();
        }

        return Optional.empty();
    }

    private void merge(Map.Entry<String, Integer> entry, Map<String, Integer> namesMap) {
        if (namesMap.containsKey(entry.getKey())) {
            Integer count = namesMap.get(entry.getKey());
            count = count + entry.getValue();
            namesMap.put(entry.getKey(), count);
        } else {
            namesMap.put(entry.getKey(), entry.getValue());
        }
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
                .parallel()
                .map(Map.Entry::getKey)
                .collect(Collectors.toCollection(LinkedList::new));

        LinkedList<String> lastNamesList =
            lastNamesMap
                .entrySet()
                .stream()
                .parallel()
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
                .parallel()
                .sorted((entrySetA, entrySetB) -> entrySetB.getValue().compareTo(entrySetA.getValue()))
                .limit(10)
                .map(entrySet -> new NameAndCount(entrySet.getKey(), entrySet.getValue()))
                .collect(Collectors.toCollection(LinkedList::new));
    }
}
