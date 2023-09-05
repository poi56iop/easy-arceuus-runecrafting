package com.foo;

import java.util.Objects;

public enum RuneType {
    BLOOD("Blood"),
    SOUL("Soul"),

    ;

    final String name;

    RuneType(String name) {
        this.name = name;
    }

    public String toString() {
        return this.name;
    }

    public boolean isBlood() {
        return (Objects.equals(this.name, "Blood"));
    }

    public boolean isSoul() {
        return (Objects.equals(this.name, "Soul"));
    }
}
