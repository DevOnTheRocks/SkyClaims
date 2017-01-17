package net.mohron.skyclaims.database;

/**
 * Created by adam_ on 01/17/17.
 */
public class Schemas {

    public static String IslandTable =
            "CREATE TABLE IF NOT EXISTS %s " +
                    "(" +
                    "id 		INTEGER  		AUTO_INCREMENT  ," +
                    "UUID 		VarChar(36) ," +
                    "Player 	VarChar(36) ," +
                    "Claim 	    VarChar(36)," +
                    "RegionX 	INTEGER," +
                    "RegionY 	INTEGER," +
                    "SpawnX 	INTEGER," +
                    "SpawnY 	INTEGER," +
                    "SpawnZ 	INTEGER," +
                    "Yaw 		DECIMAL," +
                    "Pitch 		DECIMAL," +
                    "Created 	TIMESTAMP," +
                    "PlayTime	INT," +
                    "Size 		TINYINT," +
                    "PRIMARY KEY (id), KEY (UUID)" +
                    ")";
    public static String PlayerTable =
            "CREATE TABLE IF NOT EXISTS %s " +
                    "(" +
                    "id 		INTEGER  		AUTO_INCREMENT  ," +
                    "Player 		STRING ," +
                    "PRIMARY KEY (id), KEY (Player)" +
                    ")";
}
