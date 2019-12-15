package ru.func.atlantgta.command.prison;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class PrisonUtil {

    private static Map<UUID, Integer> prisoners = Maps.newHashMap();

    public static Map<UUID, Integer> getPrisoners() {
        return prisoners;
    }
}
