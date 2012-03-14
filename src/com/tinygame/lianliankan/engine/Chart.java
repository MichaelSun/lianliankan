package com.tinygame.lianliankan.engine;

import android.util.Log;

import com.tinygame.lianliankan.config.Config;


public class Chart {
    
	public final int xSize, ySize;

	Tile[][] tiles;

    public Chart(int[][] pics) {
        this.xSize = pics[0].length;
        this.ySize = pics.length;

        tiles = new Tile[ySize][xSize];
        for (int y = 0; y < ySize; y++) {
            for (int x = 0; x < xSize; x++) {
                tiles[y][x] = new Tile(x, y, pics[y][x], this);
            }
        }
    }
    
    public Chart(int x, int y) {
        xSize = x;
        ySize = y;
    }
    
    public Chart align(int mode) {
        Chart copyChart = copy();
        switch (mode) {
        case Config.ALIGN_LEFT:
            alignLeft(copyChart);
            return copyChart;
        case Config.ALIGN_RIGHT:
            alignRight(copyChart);
            return copyChart;
        case Config.ALIGN_TOP:
            alignTop(copyChart);
            return copyChart;
        case Config.ALIGN_BOTTOM:
            alignBottom(copyChart);
            return copyChart;
        }
        
        return null;
    }
    
    private static void alignBottom(Chart chart) {
        if (chart != null) {
            for (int x = 1; x < chart.xSize - 1; ++x) {
                int dIndex = chart.ySize - 2;
                for (int y = chart.ySize - 2; y > 0; --y) {
                    if (!chart.tiles[y][x].isBlank()) {
                        chart.tiles[dIndex][x].setImageIndex(chart.tiles[y][x].getImageIndex());
                        chart.tiles[dIndex][x].resetDismiss();
                        if (dIndex != y) {
                            chart.tiles[y][x].dismiss(); 
                        }
                        dIndex--;
                    }
                }
            }
        }
    }
    
    private static void alignTop(Chart chart) {
        if (chart != null) {
            for (int x = 1; x < chart.xSize - 1; ++x) {
                int dIndex = 1;
                for (int y = 1; y < chart.ySize - 1; ++y) {
                    if (!chart.tiles[y][x].isBlank()) {
                        chart.tiles[dIndex][x].setImageIndex(chart.tiles[y][x].getImageIndex());
                        chart.tiles[dIndex][x].resetDismiss();
                        if (dIndex != y) {
                            chart.tiles[y][x].dismiss(); 
                        }
                        dIndex++;
                    }
                }
            }
        }
    }
    
    private static void alignRight(Chart chart) {
        if (chart != null) {
            for (int y = 1; y < chart.ySize - 1; ++y) {
                int dIndex = chart.xSize - 2;
                for (int x = chart.xSize - 2; x > 0; --x) {
                    if (!chart.tiles[y][x].isBlank()) {
                        chart.tiles[y][dIndex].setImageIndex(chart.tiles[y][x].getImageIndex());
                        chart.tiles[y][dIndex].resetDismiss();
                        if (dIndex != x) {
                            chart.tiles[y][x].dismiss(); 
                        }
                        dIndex--;
                    }
                }
            }
        }
    }
    
    private static void alignLeft(Chart chart) {
        if (chart != null) {
            for (int y = 1; y < chart.ySize - 1; ++y) {
                int dIndex = 1;
                for (int x = 1; x < chart.xSize - 1; ++x) {
                    if (!chart.tiles[y][x].isBlank()) {
                        chart.tiles[y][dIndex].setImageIndex(chart.tiles[y][x].getImageIndex());
                        chart.tiles[y][dIndex].resetDismiss();
                        if (dIndex != x) {
                            chart.tiles[y][x].dismiss(); 
                        }
                        dIndex++;
                    }
                }
            }
        }
    }
    
    private Chart copy() {
        Chart ret = new Chart(this.xSize, this.ySize);
        ret.tiles = new Tile[ySize][xSize];
        for (int y = 0; y < ySize; ++y) {
            for (int x = 0; x < xSize; ++x) {
                ret.tiles[y][x] = this.tiles[y][x];
            }
        }
        
        return ret;
    }
	
	public boolean isAllBlank(){
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if(!tiles[y][x].isBlank())
					return false;
			}
		}
		
		return true;
	}
	
    public void reArrange() {
        int[][] src = new int[tiles.length][tiles[0].length];
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[0].length; x++) {
                src[y][x] = tiles[y][x].getImageIndex();
            }
        }
        FillContent.reArrange(src);
        for (int y = 0; y < tiles.length; y++) {
            for (int x = 0; x < tiles[0].length; x++) {
                tiles[y][x].setImageIndex(src[y][x]);
            }
        }
    }

	public ConnectiveInfo connectvie(Tile tile, Tile other) {
		if (null == tile || null == other) {
			System.err.println("some one is null");
			return ConnectiveInfo.CANNOT_FIND;
		}

		if (tile == Tile.UN_EXIST_TILE || other == Tile.UN_EXIST_TILE) {
			System.err.println("some one is UN_EXIST_TILE");
			return ConnectiveInfo.CANNOT_FIND;
		}
		if (tile == other) {
			System.err.println("is the same tile");
			return ConnectiveInfo.CANNOT_FIND;
		}

		BlankRoute route = new BlankRoute(tile, other);
		boolean result = tile.isLink(route);
		ConnectiveInfo info = new ConnectiveInfo(result, route);
		return info;
	}

	public Tile getTile(int x, int y) {
		if (null == tiles)
			throw new IllegalStateException("game is not start!");

		try {
			return tiles[y][x];
		} catch (ArrayIndexOutOfBoundsException ex) {
			return Tile.UN_EXIST_TILE;
		}
	}
	
	public void dumpChart() {
//	    for (int y = 0; y < ySize; ++y) {
//	        StringBuilder data = new StringBuilder();
//	        for (int x = 0; x < xSize; ++x) {
//	            data.append(tiles[y][x].getImageIndex()).append(",");
//	        }
//	        Log.d("[[Chart]]", data.toString());
//	    }
	}
}
