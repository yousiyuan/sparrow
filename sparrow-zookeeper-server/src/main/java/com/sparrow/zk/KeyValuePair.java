package com.sparrow.zk;

import org.springframework.lang.NonNull;

public class KeyValuePair implements Comparable<KeyValuePair> {

    public KeyValuePair(String key, Object value) {
        this.setKey(key);
        this.setValue(value);
    }

    private String key;
    private Object value;

    String getKey() {
        return key;
    }

    private void setKey(String key) {
        this.key = key;
    }

    Object getValue() {
        return value;
    }

    private void setValue(Object value) {
        this.value = value;
    }

    @Override
    public int compareTo(@NonNull KeyValuePair kvp) {
        return String.valueOf(this.getKey()).compareTo(String.valueOf(kvp.getKey())) > 0 ? 1 : -1;
    }

    @Override
    public String toString() {
        return "{" + "\"key:\"" + this.getKey() + "\"value:\"" + this.getValue() + "}";
    }
}
