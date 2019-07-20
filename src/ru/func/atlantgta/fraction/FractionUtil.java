package ru.func.atlantgta.fraction;

import java.util.ArrayList;
import java.util.List;

public class FractionUtil {

    private static List<Fraction> fractions = new ArrayList<>();
    private static Fraction NONE = new Fraction(1, "NONE", "Отсутствует", null);

    public static Fraction getFractionByName(String name) {
        for (Fraction fraction : fractions)
            if (fraction.getName().equals(name))
                return fraction;
        return null;
    }

    public static List<Fraction> getFractions() {
        return fractions;
    }

    public static Fraction getNoneFraction() {
        return NONE;
    }
}
