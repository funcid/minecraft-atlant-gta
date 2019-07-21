package ru.func.atlantgta.listener;

import com.google.common.collect.Maps;

import java.util.Map;
import java.util.UUID;

public class ContractUtil {

    private static Map<UUID, Integer> contracts = Maps.newHashMap();

    public static Map<UUID, Integer> getContracts() {
        return contracts;
    }
}
