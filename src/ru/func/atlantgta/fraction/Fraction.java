package ru.func.atlantgta.fraction;

import org.bukkit.Location;

public class Fraction extends FractionBuilder implements IFraction {

    private int minLevel;
    private String name;
    private String subName;
    private Location baseLocation;

    Fraction(final FractionBuilder fractionBuilder) {
        this.minLevel = fractionBuilder.getMinLevel();
        this.name = fractionBuilder.getName();
        this.subName = fractionBuilder.getSubName();
        this.baseLocation = fractionBuilder.getBaseLocation();
    }

    @Override
    public String getSubName() {
        return subName;
    }

    @Override
    public int getMinLevel() {
        return minLevel;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Location getBaseLocation() {
        return baseLocation;
    }

    @Override
    public void setBaseLocation(Location baseLocation) {
        this.baseLocation = baseLocation;
    }
}
