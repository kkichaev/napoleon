package com.novotek.dataobjects;

import com.novotek.dataobjects.xml.Alias;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Action {
    public String description = "";
    public String description_big = "";

    public String url = "";
    public String url_big = "";

    @Alias(name = "date_begin")
    public Date begin = new Date();
    @Alias(name = "date_end")
    public Date end = new Date();

    public List<ActionItem> items = new ArrayList<>();

    static final Date NULL_DATE = new Date(2 * 24 * 3600 * 10000);

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

    public boolean isActive(Date now) {
        return begin.before(now) && (end.before(NULL_DATE) || end.after(now));
    }
}
