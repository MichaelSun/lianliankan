package com.tinygame.lianliankan.db;

class DataBaseConfig {

    public static final String SPLITOR = ";";
    
    public static final String INTEGRAL_DATABASE_CREATE = "create table integral "
            + "(_id INTEGER primary key autoincrement, "
            + "category TEXT not null, "
            + "level TEXT not null, "
            + "count TEXT not null, " 
            + "continue TEXT not null, "
            + "maxcontinue TEXT not null, "
            + "costtime TEXT not null)";

    public static final String INTEGRAL_TABLE_NAME = "integral";
    public static final String INTEGRAL_TABLE_CATEGORY= "category";
    public static final String INTEGRAL_TABLE_LEVEL = "level";
    public static final String INTEGRAL_TABLE_COUNT = "count";
    public static final String INTEGRAL_TABLE_CONTINUE = "continue";
    public static final String INTEGRAL_TABLE_MAX_CONTINUE = "maxcontinue";
    public static final String INTEGRAL_TABLE_COST = "costtime";
}
