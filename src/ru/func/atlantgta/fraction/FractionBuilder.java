package ru.func.atlantgta.fraction;

import org.bukkit.Location;

public class FractionBuilder {

    private int minLevel;
    private String name;
    private String subName;
    private Location baseLocation;

    public FractionBuilder minLevel(final int minLevel) {
        this.minLevel = minLevel;
        return this;
    }

    public FractionBuilder name(final String name) {
        this.name = name;
        return this;
    }

    public FractionBuilder subName(final String subName) {
        this.subName = subName;
        return this;
    }

    public FractionBuilder baseLocation(final Location baseLocation) {
        this.baseLocation = baseLocation;
        return this;
    }

    protected int getMinLevel() {
        return minLevel;
    }

    protected String getName() {
        return name;
    }

    protected String getSubName() {
        return subName;
    }

    protected Location getBaseLocation() {
        return baseLocation;
    }

    public Fraction build() {
        return new Fraction(this);
    }
}
