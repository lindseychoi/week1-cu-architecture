package io.collective;

import java.time.Clock;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SimpleAgedCache {

    private List<ExpirableEntry> entryList = new ArrayList<>();
    private Clock clock = null;

    private long startTime;

    class ExpirableEntry {
        private String value;
        private String key;
        private Integer retentionInMillis;

        public ExpirableEntry(String key, String value, Integer retentionInMillis) {
            this.value = value;
            this.key = key;
            this.retentionInMillis = retentionInMillis;
        }

        public String getValue() {
            return value;
        }

        public String getKey() {
            return key;
        }

        public Integer getRetentionInMillis() {
            return retentionInMillis;
        }
    }

    public SimpleAgedCache(Clock clock) {
        this.clock = clock;
        this.startTime = this.clock.millis();
    }

    public SimpleAgedCache() {
    }

    public void put(String key, String value, int retentionInMillis) {
        addEntry(key, value, retentionInMillis);
    }

    public boolean isEmpty() {
        return entryList.isEmpty();
    }

    public int size() {
        if (clock != null) {
            entryExpiresCheck();
        }
        return entryList.size();
    }

    public void addEntry(String key, String value, int retentionInMillis) {
        ExpirableEntry entry = new ExpirableEntry(key, value, retentionInMillis);
        entryList.add(entry);
    }
    public Object get(String key) {
        for (ExpirableEntry entry : entryList) {
            if (entry.getKey().equals(key)) {
              return entry.getValue();
            }
        }
        return null;
    }

    void entryExpiresCheck() {
        long millisPast = clock.millis();
        long difference = millisPast - this.startTime;

        entryList = entryList
            .stream()
            .filter(entry -> entry.getRetentionInMillis() > difference)
            .collect(Collectors.toList());

    }

}