package com.tinygame.lianliankan.engine;

import java.util.ArrayList;
import java.util.List;

public class Hint {
    public Tile[] tile = new Tile[2];

    private Chart chart;

    public Hint(Chart chart) {
        this.chart = chart;
    }

    public Tile[] findHint() {
        boolean find = false;
        List<Tile> finded = new ArrayList<Tile>();
        while (find == false) {
            Tile one = null;
            for (int y = 0; y < chart.tiles.length; y++) {
                for (int x = 0; x < chart.tiles[0].length; x++) {
                    if (finded.contains(chart.tiles[y][x])) {

                    } else {
                        if (chart.tiles[y][x].isBlank() == false) {
                            if (one == null) {
                                one = chart.tiles[y][x];
                            } else {
                                if (one.getImageIndex() == chart.tiles[y][x].getImageIndex()) {
                                    if (chart.connectvie(one, chart.tiles[y][x]).getResult()) {
                                        return new Tile[] { one, chart.tiles[y][x] };
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (one == null) {
                return null;
            } else {
                finded.add(one);
            }
        }
        return null;
    }

}
