package com.tinygame.lianliankan.engine;

import com.tinygame.lianliankan.utils.ThemeManager;


public class Tile {
	public static Tile UN_EXIST_TILE = new Tile(-1, -1, ThemeManager.NO_IMAGE, null);
	public final int x, y;
	private int image;

	private Chart belongChart;

	public Tile(int x, int y, int image, Chart belongChart) {
		this.image = image;
		this.x = x;
		this.y = y;
		this.belongChart = belongChart;
	}

	private int alpha = 255;
	
    public int deAlpha(int number) {
        alpha -= number;
        return alpha;
    }
	
	public void dismiss() {
		this.image = ThemeManager.NO_IMAGE;
	}

	public int getImageIndex() {
		return image;
	}
	
	public void setImageIndex(int image){
		this.image = image;
	}

	public boolean isBlank() {
		return this.image == ThemeManager.NO_IMAGE || isDismissing();
	}

	private Tile getNear(Direction direction) {
		if (belongChart == null)
			return UN_EXIST_TILE;

		if (Direction.north == direction) {
			return belongChart.getTile(x, y - 1);
		} else if (Direction.east == direction) {
			return belongChart.getTile(x + 1, y);
		} else if (Direction.south == direction) {
			return belongChart.getTile(x, y + 1);
		} else if (Direction.west == direction) {
			return belongChart.getTile(x - 1, y);
		}
		throw new IllegalArgumentException("which direction? are you crazy!!");
	}

	private boolean toOtherXBlank(Tile other) {
		int min = Math.min(this.x, other.x);
		int max = Math.max(this.x, other.x);
		for (int index = min + 1; index < max; index++) {
			if (this.belongChart.getTile(index, y).isBlank() == false) {
				return false;
			}
		}
		return true;
	}

	private boolean toOtherYBlank(Tile other) {
		int min = Math.min(this.y, other.y);
		int max = Math.max(this.y, other.y);
		for (int index = min + 1; index < max; index++) {
			if (this.belongChart.getTile(x, index).isBlank() == false) {
				return false;
			}
		}
		return true;
	}

	public boolean isLink(BlankRoute route) {
        if (route.isInPath(this)) {
            return false;
        }
        if (this.isBlank() || this == route.start) {
            {
                boolean canGoThisTile = route.addRouteTile(this);
                if (canGoThisTile == false) {
                    return false;
                }
            }

			Tile eastNear = getNear(Direction.east);
			Tile southNear = getNear(Direction.south);
			Tile westNear = getNear(Direction.west);
			Tile northNear = getNear(Direction.north);
			if (eastNear == route.end || westNear == route.end
					|| southNear == route.end || northNear == route.end) {
				return route.addRouteTile(route.end);
			}

			boolean result = false;
			boolean inAxis = route.isInAxis(this);
		
			if (inAxis) {
				if (this.x == route.end.x) {
					if (toOtherYBlank(route.end)) {
						if (this.y > route.end.y) {
							result = northNear.isLink(route);
						} else {
							result = southNear.isLink(route);
						}
					}

				} else if (this.y == route.end.y) {
					if (toOtherXBlank(route.end)) {
						if (this.x > route.end.x) {
							result = westNear.isLink(route);
						} else {
							result = eastNear.isLink(route);
						}
					}
				} else {
					if (this.x > route.end.x) {
						if (this.y > route.end.y) {
							if (toOtherXBlank(route.end)) {
								result = result || westNear.isLink(route);
							}
							if (toOtherYBlank(route.end)) {
								result = result || northNear.isLink(route);
							}
						} else {
							if (toOtherXBlank(route.end)) {
								result = result || westNear.isLink(route);
							}
							if (toOtherYBlank(route.end)) {
								result = result || southNear.isLink(route);
							}
						}
					} else {
						if (this.y > route.end.y) {
							if (toOtherXBlank(route.end)) {
								result = result || eastNear.isLink(route) ;
							}
							if (toOtherYBlank(route.end)) {
								result = result || northNear.isLink(route);
							}
						} else {
							if (toOtherXBlank(route.end)) {
								result = result || eastNear.isLink(route);
							}
							if (toOtherYBlank(route.end)) {
								result = result || southNear.isLink(route);
							}
						}
					}
				}
				if (result == false) {
					Direction direction = route.getDirection();
					if (Direction.east == direction)
						result = eastNear.isLink(route);
					else if (Direction.south == direction)
						result = southNear.isLink(route);
					else if (Direction.west == direction)
						result = westNear.isLink(route);
					else if (Direction.north == direction)
						result = northNear.isLink(route);
					else{
						result = eastNear.isLink(route) || 
						southNear.isLink(route)||
						westNear.isLink(route) ||
						northNear.isLink(route);
					}
				}
            } else {
                Tile someTile = getNear(route.getDirection());
                result = result || someTile.isLink(route);
            }
            if (result == false) {
                route.removeTile(this);
            }
			return result;
		}
		return false;
	}
	
	
	public String toString(){
		return "X:" + x  + " Y:" + y;
	}

	private boolean dismissing = false;
	
	private boolean isDismissing(){
		return dismissing;
	}
	
	public void resetDismiss() {
	    dismissing = false;
	}
	
	public void dismissing() {
		dismissing = true;
	}
}
