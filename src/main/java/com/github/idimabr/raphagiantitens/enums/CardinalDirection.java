package com.github.idimabr.raphagiantitens.enums;

import org.bukkit.entity.Player;

public enum CardinalDirection {
	
	// 0 / 180 /270 / 90

    NORTH, SOUTH, EAST, WEST;

	 public static CardinalDirection getCardinalDirection(Player player) {
	        float yaw = player.getLocation().getYaw();
	        if (yaw < 0) { yaw += 360; }
	        if (yaw >= 315 || yaw < 45) {
	            return SOUTH;
	        } else if (yaw < 135) {
	            return WEST;
	        } else if (yaw < 225) {
	            return NORTH;
	        } else if (yaw < 315) {
	            return EAST;
	        }
	        return NORTH;
	    }
	 
	 public static Enum getDirection(Player player) {
	        float yaw = player.getLocation().getYaw();
	        if (yaw < 0) { yaw += 360; }
	        if (yaw >= 315 || yaw < 45) {
	            return SOUTH;
	        } else if (yaw < 135) {
	            return WEST;
	        } else if (yaw < 225) {
	            return NORTH;
	        } else if (yaw < 315) {
	            return EAST;
	        }
	        return NORTH;
	    }

    CardinalDirection() {}

}