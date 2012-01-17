package com.tinygame.lianliankan.engine;

public enum Direction {
    north, east, south, west;

    public int padding(boolean xAxis, int sideLength) {
        if (xAxis) {
            if (this == north) {
                return 0;
            } else if (this == south) {
                return 0;
            } else if (this == east) {
                return sideLength / 2 + 1;
            } else {
                return - sideLength / 2 - 1;
            }
        } else {
            if (this == north) {
                return - sideLength / 2 - 1;
            } else if (this == south) {
                return sideLength / 2 + 1;
            } else if (this == east) {
                return 0;
            } else {
                return 0;
            }
        }
    }
}
