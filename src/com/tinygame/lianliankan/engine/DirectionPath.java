package com.tinygame.lianliankan.engine;


public class DirectionPath {
	
    private final Tile self;
    private Direction[] direction = new Direction[2];

    public DirectionPath(Tile from, Tile self, Tile to) {
        this.self = self;
        direction[0] = calDirection(from, self);
        direction[1] = calDirection(to, self);
    }

	public Tile getTile(){
		return self;
	}
	
	public Direction[] getDirection(){
		return direction;
	}
	
	public Direction calDirection(Tile a, Tile b){
		if(a.x == b.x){
			if(a.y > b.y){
				return Direction.south;
			}else{
				return Direction.north;
			}
		}else{
			if(a.x > b.x){
				return Direction.east;
			}else{
				return Direction.west;
			}
		}
	}
}
