package net.mohron.skyclaims.database;

/**
 * Created by adam_ on 01/17/17.
 */
public class Schemas {

    public static String IslandTable =
            "CREATE TABLE IF NOT EXISTS %s " +
                    "(" +
                    "id 		INTEGER  		AUTO_INCREMENT  ," +
                    "UUID 		MEDIUMTEXT(36) ," +
                    "Player 	MEDIUMTEXT(36) ," +
                    "Claim 	    MEDIUMTEXT(36)," +
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
                    "Player 		MEDIUMTEXT(36) ," +
                    "PRIMARY KEY (id), KEY (Player)" +
                    ")";
}
