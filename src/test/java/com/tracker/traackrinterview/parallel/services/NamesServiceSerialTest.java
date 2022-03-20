package com.tracker.traackrinterview.parallel.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tracker.traackrinterview.parallel.domains.NameAndCount;
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
class NamesServiceSerialTest {
    NamesServiceSerial namesServiceSerial;
    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeAll
    public void setThingsUp() {
        LineProcessor lineProcessor = new CommaAndDashLineProcessor();
        this.namesServiceSerial = new NamesServiceSerial(lineProcessor);
    }

    @Test
    void processNamesWithOneHundredThousandDataSample() throws Exception {
        String path = "src/test/resources/coding-test-data.txt";
        int modifiedNamesCount = 25;

        Optional<NamesStat> namesStatOptional = namesServiceSerial.processNames(path, 0, modifiedNamesCount);
        NamesStat namesStat = namesStatOptional.get();

        assertThat(namesStat.getUniqueFirstNameCardinality()).isEqualTo(3006);
        assertThat(namesStat.getUniqueLastNameCardinality()).isEqualTo(468);
        assertThat(namesStat.getUniqueFullNameCardinality()).isEqualTo(48418);
        //TODO: add more tests cases
        System.out.println("NamesServiceSerial - 100K: Execution duration in milliseconds: " + namesStat.getExecutionDuration());
        System.out.println(objectMapper.writeValueAsString(namesStat));
    }

    @Test
    void processNamesWithMinorSampleData() {
        String path = "src/test/resources/coding-test-data-minor.txt";
        int modifiedNamesCount = 5;

        Optional<NamesStat> namesStatOptional = namesServiceSerial.processNames(path, 0, modifiedNamesCount);
        NamesStat namesStat = namesStatOptional.get();

        assertThat(namesStat.getUniqueFirstNameCardinality()).isEqualTo(3);
        assertThat(namesStat.getUniqueLastNameCardinality()).isEqualTo(4);
        assertThat(namesStat.getUniqueFullNameCardinality()).isEqualTo(5);

        assertThat(namesStat.getTopTenFistNames())
                .containsExactlyInAnyOrder(new NameAndCount("Joan", 3),
                        new NameAndCount("Eric", 1), new NameAndCount("Sam", 1));

        assertThat(namesStat.getTopTenLastNames())
                .containsExactlyInAnyOrder(new NameAndCount("Smith", 2), new NameAndCount("Thomas", 1),
                        new NameAndCount("Upton", 1), new NameAndCount("Cartman", 1));

        assertThat(namesStat.getTopTenFullNames())
                .containsExactlyInAnyOrder(new NameAndCount("Smith, Sam", 1), new NameAndCount("Thomas, Joan", 1),
                        new NameAndCount("Smith, Joan", 1), new NameAndCount("Cartman, Eric", 1),
                        new NameAndCount("Upton, Joan", 1));

        assertThat(namesStat.getModifiedFullNames().size()).isGreaterThan(1);
    }

    @Disabled
    @Test
    void processNamesWithHugeSampleData() throws Exception {
        //TODO: put the path of the data file
        String path = "/Users/wahid/coding/data-huge.txt";
        int modifiedNamesCount = 25;

        Optional<NamesStat> namesStatOptional = namesServiceSerial.processNames(path, 0, modifiedNamesCount);
        NamesStat namesStat = namesStatOptional.get();

        assertThat(namesStat.getUniqueFirstNameCardinality()).isEqualTo(2452);
        assertThat(namesStat.getUniqueLastNameCardinality()).isEqualTo(468);
        assertThat(namesStat.getUniqueFullNameCardinality()).isEqualTo(5212);
        System.out.println("NamesServiceSerial - 18 Million records: Execution duration in milliseconds: " + namesStat.getExecutionDuration());
        System.out.println(objectMapper.writeValueAsString(namesStat));
    }
}