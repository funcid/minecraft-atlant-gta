package ru.func.atlantgta.fraction;

import org.bukkit.Location;

public interface IFraction {

    /**
     * @return name that used in Scoreboard, Password and other profile methods...
     */
    String getSubName();

    /**
     * @return minimal level that player must have to join fraction
     */
    int getMinLevel();

    /**
     * @return fraction name
     */
    String getName();

    /**
     * @return location that used to teleport player by /base command
     */
    Location getBaseLocation();

    /**
     * @param baseLocation location that used in player teleportation by /base command
     */
    void setBaseLocation(Location baseLocation);
}
