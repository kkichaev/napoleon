package com.novotek.dataobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Action {
    public String description = "";
    public String url = "";
    public List<String> items = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Action action = (Action) o;
        return Objects.equals(description, action.description) && Objects.equals(url, action.url);
    }

    @Override
    public int hashCode() {
        return Objects.hash(description, url);
    }
}
