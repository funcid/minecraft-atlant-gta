package ru.func.atlantgta.fraction;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FractionUtil {

    private static List<Fraction> fractions = new ArrayList<>();
    private static Fraction NONE = new FractionBuilder()
            .name("NONE")
            .subName("Отсутствует")
            .minLevel(1)
            .baseLocation(null)
            .build();

    public static Optional<Fraction> getFractionByName(String name) {
        return fractions.stream()
                .filter(f -> f.getName().equalsIgnoreCase(name))
                .findAny();
    }

    public static Optional<Fraction> getFractionBySubName(String subName) {
        return fractions.stream()
                .filter(f -> f.getSubName().equalsIgnoreCase(subName))
                .findAny();
    }


    public static List<Fraction> getFractions() {
        return fractions;
    }

    public static Fraction getNoneFraction() {
        return NONE;
    }
}
