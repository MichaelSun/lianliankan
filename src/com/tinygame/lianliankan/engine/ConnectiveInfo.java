package com.tinygame.lianliankan.engine;

public class ConnectiveInfo{
	
	public static final ConnectiveInfo CANNOT_FIND = new ConnectiveInfo(false,null);
	
	private final boolean result;
	private final BlankRoute route;
	
	public ConnectiveInfo(boolean result, BlankRoute route) {
		this.result = result;
		this.route = route;
	}
	public boolean getResult() {
		return result;
	}
	public BlankRoute getRoute() {
		return route;
	}
}