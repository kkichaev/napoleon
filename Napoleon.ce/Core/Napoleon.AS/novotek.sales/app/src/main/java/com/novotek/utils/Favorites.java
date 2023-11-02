package com.novotek.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.novotek.dataobjects.Price;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Favorites {
    static final String PREF = "Favorites";
    static final String KEY = "favorites";

    Set<String> favorites;
    Context context;

    public Favorites(Context context) {
        this.context = context;
        SharedPreferences sp = context.getSharedPreferences(PREF, Context.MODE_PRIVATE);
        favorites = sp.getStringSet(KEY, new HashSet<>());
    }

    public void change(String id) {
        if(favorites.contains(id)) {
            favorites.remove(id);
        } else {
            favorites.add(id);
        }
        SharedPreferences.Editor ed = context.getSharedPreferences(PREF, Context.MODE_PRIVATE).edit();
        ed.putStringSet(KEY, favorites);
        ed.commit();
    }

    public void change(Price item) { change(item.id); }

    public boolean contains(Price item) { return favorites.contains(item.id); }

    public ArrayList<String> get() {
        ArrayList<String> dest = new ArrayList<>(favorites);
        return dest;
    }
}
