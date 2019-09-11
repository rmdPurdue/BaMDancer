package util.algorithms;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.*;

public enum Algorithm {
    PARTIAL_PERCENT_LOW ("0 to 60"),
    PARTIAL_PERCENT_HIGH ("30 to 100"),
    PERCENT_TO_THIRTY("0 to 30"),
    PERCENT_TO_FIFTY("0 to 50"),
    PERCENT_TO_SEVENTY("0 to 70"),
    PERCENT_FROM_THIRTY("30 to 0"),
    PERCENT_FROM_FIFTY("50 to 0"),
    PERCENT_FROM_SEVENTY("70 to 0"),
    PERCENT_TWENTY_TO_SIXTY("20 to 60"),
    PERCENT_SIXTY_TO_TWENTY("60 to 60"),
    PERCENT_LEVELS ("0 to 100"),
    PERCENT_LEVELS_INVERTED ("100 to 0"),
    ONE_BYTE_LEVELS ("0 to 255"),
    ONE_BYTE_LEVELS_INVERTED ("255 to 0"),
    UNIT_INTERVAL ("0.0 to 1.0"),
    UNIT_INTERVAL_INVERTED ("1.0 to 0.0"),
    AUDIO_LEVELS ("-60 to 12"),
    AUDIO_LEVELS_INVERTED ("12 to -60");

    private String value;

    private static final ArrayList<String> KEYS;
    private static final ArrayList<String> VALUES;
    private static final Map<String, Algorithm> lookup = new HashMap<>();

    static {
        KEYS = new ArrayList<>();
        for(Algorithm algorithm : Algorithm.values()) {
            KEYS.add(algorithm.name());
        }
    }

    static {
        VALUES = new ArrayList<>();
        for(Algorithm algorithm : Algorithm.values()) {
            VALUES.add(algorithm.value);
        }
    }

    static {
        for(Algorithm algorithm : Algorithm.values())
            lookup.put(algorithm.getValue(), algorithm);

    }

    Algorithm(String value) {
        this.value = value;
    }

    public static ObservableList<String> getKeys() {
        return FXCollections.observableList(KEYS);
    }

    public String getValue() {
        return value;
    }

    public static Algorithm get(String value) {
        return lookup.get(value);
    }

    public static ObservableList<String> getValues() {
        return FXCollections.observableList(VALUES);
    }

    @Override
    public String toString() {
        return this.name();
    }

}
