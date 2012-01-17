package com.tinygame.lianliankan.engine;

import java.util.LinkedList;

public class BlankRoute {
	
	public static final int MAX_TRUN_COUNT = 2;
	public final Tile start, end;
	private final LinkedList<Tile> routes = new LinkedList<Tile>();
	
	public BlankRoute dismissing(){
		this.start.dismissing();
		this.end.dismissing();
		return this;
	}
	
	public BlankRoute(Tile start, Tile end){
		this.start = start;
		this.end = end;
	}
	
	public boolean isInAxis(Tile tile){
		return tile == start || tile == end || tile.x == this.start.x || tile.y == this.start.y ||
		tile.x == this.end.x || tile.y == this.end.y;
	}
	
	public DirectionPath[] getpath(){
		int routeBodySize = routes.size() - 2;
		
		DirectionPath[] result = new DirectionPath[routeBodySize];
		for(int i=0; i<routeBodySize; i++){
			result[i] = new DirectionPath(routes.get(i),routes.get(i+1),routes.get(i+2));
		}
		return result;
	}
	
	public boolean isInPath(Tile someTile){
		return routes.contains(someTile);
	}
	
	public boolean addRouteTile(Tile tile){
		this.routes.add(tile);
		if(calTurnCount() > MAX_TRUN_COUNT){
			this.routes.remove(tile);
			return false;
		}
		return true;
		
	}
	
	public Direction getDirection(){
		int size = routes.size();
		if(routes.size()>=2){
			Tile last = routes.getLast();
			Tile beforeLast = routes.get(size - 2);
			if(last.x == beforeLast.x){
				if(last.y > beforeLast.y){
					return Direction.south;
				}else{
					return Direction.north;
				}
			}else{
				if(last.x > beforeLast.x){
					return Direction.east;
				}else{
					return Direction.west;
				}
			}
		}
		return null;
	}
	
	private int calTurnCount() {
		int turnCount = -1;
		Direction calDirection = null;
		Tile lastTile = start;
		for(int i=1; i<routes.size(); i++){
			Tile eachTile = routes.get(i);
			if(lastTile.x == eachTile.x){
				if(lastTile.y > eachTile.y){
					if(calDirection != Direction.north){
						calDirection = Direction.north;
						turnCount++;
					}
				}else{
					if(calDirection != Direction.south){
						calDirection = Direction.south;
						turnCount++;
					}
				}
			}else{
				if(lastTile.x > eachTile.x){
					if(calDirection != Direction.west){
						calDirection = Direction.west;
						turnCount++;
					}
				}else{
					if(calDirection != Direction.east){
						calDirection = Direction.east;
						turnCount++;
					}
				}
			}
			lastTile = eachTile;
		}
		return turnCount;
		
	}

	public boolean removeTile(Tile tile){
		return this.routes.remove(tile);
	}
}
