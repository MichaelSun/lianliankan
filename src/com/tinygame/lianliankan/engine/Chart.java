package com.tinygame.lianliankan.engine;


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
	
	public boolean isAllBlank(){
		for (int y = 0; y < ySize; y++) {
			for (int x = 0; x < xSize; x++) {
				if(tiles[y][x].isBlank() == false)
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

	public Tile get(int x, int y) {
		if (null == tiles)
			throw new IllegalStateException("game is not start!");

		try {
			return tiles[y][x];
		} catch (ArrayIndexOutOfBoundsException ex) {
			return Tile.UN_EXIST_TILE;
		}
	}
}
