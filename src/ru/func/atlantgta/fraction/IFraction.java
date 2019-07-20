package ru.func.atlantgta.fraction;

import org.bukkit.Location;

public interface IFraction {

    String getSubName();

    int getMinLevel();

    String getName();

    Location getBaseLocation();

    void setBaseLocation(Location baseLocation);
}
