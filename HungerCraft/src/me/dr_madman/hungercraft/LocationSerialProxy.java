package me.dr_madman.hungercraft;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

public final class LocationSerialProxy implements ConfigurationSerializable {
	 
    private final String world;
    private final String uuid;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;
    private transient Location loc;
 
    public LocationSerialProxy(Location l) {
        this.world = l.getWorld().getName();
        this.uuid = l.getWorld().getUID().toString();
        this.x = l.getX();
        this.y = l.getY();
        this.z = l.getZ();
        this.yaw = l.getYaw();
        this.pitch = l.getPitch();
    }
 
    public LocationSerialProxy(Map<String, Object> map) {
        this.world = (String) map.get("world");
        this.uuid = (String) map.get("uuid");
        this.x = (Double) map.get("x");
        this.y = (Double) map.get("y");
        this.z = (Double) map.get("z");
        this.yaw = ((Double) map.get("yaw")).floatValue();
        this.pitch = ((Double) map.get("pitch")).floatValue();
    }
 
    public static LocationSerialProxy deserialize(Map<String, Object> map) {
        return new LocationSerialProxy(map);
    }
 
    public final Map<String, Object> serialize() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("world", this.world);
        map.put("uuid", this.uuid);
        map.put("x", this.x);
        map.put("y", this.y);
        map.put("z", this.z);
        map.put("yaw", this.yaw);
        map.put("pitch", this.pitch);
        return map;
    }
 
    public final Location getLocation(Server server) {
        if (loc == null) {
            loc = new Location(server.getWorld(this.world), x, y, z, yaw, pitch);
        }
        return loc;
    }
}
