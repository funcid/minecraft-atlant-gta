package ru.func.atlantgta.fraction;

import org.bukkit.Location;

public class Fraction implements IFraction {

    private int minLevel;
    private String name;
    private String subName;
    private Location baseLocation;

    public Fraction(int minLevel, String name, String subName, Location baseLocation) {
        this.minLevel = minLevel;
        this.name = name;
        this.subName = subName;
        this.baseLocation = baseLocation;
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
