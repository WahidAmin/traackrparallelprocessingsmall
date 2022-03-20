package com.tracker.traackrinterview.parallel.domains;

import java.util.Objects;

public class NameAndCount {
    private String name;
    private int count;

    public NameAndCount(String name, int count) {
        this.name = name;
        this.count = count;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NameAndCount that = (NameAndCount) o;
        return getCount() == that.getCount() && getName().equals(that.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getCount());
    }

    @Override
    public String toString() {
        return "{" +
                "name='" + name + '\'' +
                ", count=" + count +
                '}';
    }
}
