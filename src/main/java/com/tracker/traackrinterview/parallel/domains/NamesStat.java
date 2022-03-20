package com.tracker.traackrinterview.parallel.domains;

import java.util.LinkedList;
import java.util.List;

public class NamesStat {
    private int uniqueFirstNameCardinality;
    private int uniqueLastNameCardinality;
    private int uniqueFullNameCardinality;
    private List<NameAndCount> topTenFistNames = new LinkedList<NameAndCount>();
    private List<NameAndCount> topTenLastNames = new LinkedList<NameAndCount>();
    private List<NameAndCount> topTenFullNames = new LinkedList<NameAndCount>();
    private List<String> modifiedFullNames = new LinkedList<String>();
    private long executionDuration;

    public long getExecutionDuration() {
        return executionDuration;
    }

    public void setExecutionDuration(long executionDuration) {
        this.executionDuration = executionDuration;
    }

    public List<String> getModifiedFullNames() {
        return modifiedFullNames;
    }

    public void setModifiedFullNames(List<String> modifiedFullNames) {
        this.modifiedFullNames = modifiedFullNames;
    }

    public int getUniqueFirstNameCardinality() {
        return uniqueFirstNameCardinality;
    }

    public void setUniqueFirstNameCardinality(int uniqueFirstNameCardinality) {
        this.uniqueFirstNameCardinality = uniqueFirstNameCardinality;
    }

    public int getUniqueLastNameCardinality() {
        return uniqueLastNameCardinality;
    }

    public void setUniqueLastNameCardinality(int uniqueLastNameCardinality) {
        this.uniqueLastNameCardinality = uniqueLastNameCardinality;
    }

    public int getUniqueFullNameCardinality() {
        return uniqueFullNameCardinality;
    }

    public void setUniqueFullNameCardinality(int uniqueFullNameCardinality) {
        this.uniqueFullNameCardinality = uniqueFullNameCardinality;
    }

    public List<NameAndCount> getTopTenFistNames() {
        return topTenFistNames;
    }

    public void setTopTenFistNames(List<NameAndCount> topTenFistNames) {
        this.topTenFistNames = topTenFistNames;
    }

    public List<NameAndCount> getTopTenLastNames() {
        return topTenLastNames;
    }

    public void setTopTenLastNames(List<NameAndCount> topTenLastNames) {
        this.topTenLastNames = topTenLastNames;
    }

    public List<NameAndCount> getTopTenFullNames() {
        return topTenFullNames;
    }

    public void setTopTenFullNames(List<NameAndCount> topTenFullNames) {
        this.topTenFullNames = topTenFullNames;
    }

    @Override
    public String toString() {
        return "NamesStat{" +
                "uniqueFirstNameCardinality=" + uniqueFirstNameCardinality +
                ", uniqueLastNameCardinality=" + uniqueLastNameCardinality +
                ", uniqueFullNameCardinality=" + uniqueFullNameCardinality +
                ", topTenFistNames=" + topTenFistNames +
                ", topTenLastNames=" + topTenLastNames +
                ", topTenFullNames=" + topTenFullNames +
                ", modifiedFullNames=" + modifiedFullNames +
                ", executionDuration=" + executionDuration +
                '}';
    }
}
