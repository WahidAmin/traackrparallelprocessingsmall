package com.tracker.traackrinterview.parallel.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.traackrinterview.parallel.domains.NamesStat;
import com.tracker.traackrinterview.parallel.lineprocessors.CommaAndDashLineProcessor;
import com.tracker.traackrinterview.parallel.lineprocessors.LineProcessor;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class NamesServiceMultiThreadingTest {
    NamesServiceMultiThreading namesServiceMultiThreading;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void setThingsUp() {
        LineProcessor lineProcessor = new CommaAndDashLineProcessor();
        this.namesServiceMultiThreading = new NamesServiceMultiThreading(lineProcessor);
    }

    //TODO: Add more tests to take care of other cases

    @Test
    void processNamesWithOneHundredThousandDataSample() throws Exception {
        String[] paths = new String[]{
                "src/test/resources/coding-test-data-one.txt",
                "src/test/resources/coding-test-data-two.txt"};
        int modifiedNamesCount = 25;

        Optional<NamesStat> namesStatOptional = namesServiceMultiThreading.processNames(paths, modifiedNamesCount);
        NamesStat namesStat = namesStatOptional.get();

        assertThat(namesStat.getUniqueFirstNameCardinality()).isEqualTo(3006);
        assertThat(namesStat.getUniqueLastNameCardinality()).isEqualTo(468);
        assertThat(namesStat.getUniqueFullNameCardinality()).isEqualTo(48418);
        //TODO: add more tests cases
        System.out.println("NamesServiceMultiThreading - 100K: Execution duration in milliseconds: " + namesStat.getExecutionDuration());
        System.out.println(objectMapper.writeValueAsString(namesStat));
    }

    @Disabled
    @Test
    void processNamesWithHugeSampleData() throws Exception{
        //TODO: put the path of the data file
        String[] paths = {"/Users/wahid/coding/data-huge-shard-one.txt", "/Users/wahid/coding/data-huge-shard-two.txt"};

        int modifiedNamesCount = 25;

        Optional<NamesStat> namesStatOptional = namesServiceMultiThreading.processNames(paths, modifiedNamesCount);
        NamesStat namesStat = namesStatOptional.get();

        System.out.println("NamesServiceMultiThreading - 18Mil: Execution duration in milliseconds: " + namesStat.getExecutionDuration());
        System.out.println(objectMapper.writeValueAsString(namesStat));
    }
}