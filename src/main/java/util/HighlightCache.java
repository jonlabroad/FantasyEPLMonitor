package util;

import com.google.gson.Gson;
import data.youtube.Item;
import org.apache.commons.io.IOUtils;

import java.io.*;

public class HighlightCache {
    private int _gameweek;

    public HighlightCache(int gameweek) {
        _gameweek = gameweek;
    }

    public boolean hasChanged(Item[] newItems) {
        Item[] previous = getPreviousItems();
        if (previous != null && previous.length == newItems.length) {
            System.out.println("Identical number of highlights in latest data and current cache");
            return false;
        }
        if (newItems != null) {
            writeNewItems(newItems);
        }
        return true;
    }

    private void writeNewItems(Item[] newItems) {
        String json = new Gson().toJson(newItems);
        try {
            File file = new File(getFilepath());
            new File("cache").mkdirs();
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Item[] getPreviousItems() {
        String itemData = "";
        try {
            itemData = IOUtils.toString(new FileInputStream(getFilepath()));
            return new Gson().fromJson(itemData, Item[].class);
        } catch (Exception e) {
            System.out.println("No existing highlight cache found at " + getFilepath());
            //e.printStackTrace();
            return null;
        }
    }

    private String getFilepath() {
        return String.format("cache/highlight_%d.json", _gameweek);
    }
}
