package net.mohron.skyclaims.database;

/**
 * Created by adam_ on 01/17/17.
 */
public class Schemas {

    public static String IslandDB = String.format(
            "CREATE TABLE IF NOT EXISTS %s " +
                    "(" +
                    "id 		INTEGER  		AUTO_INCREMENT  ," +
                    "UUID 		MEDIUMTEXT(36) ," +
                    "Player 	MEDIUMTEXT(36) ," +
                    "Claim 	MEDIUMTEXT(36)," +
                    "RegionX 	INTEGER," +
                    "RegionY 	INTEGER," +
                    "SpawnX 	INTEGER," +
                    "SpawnY 	INTEGER," +
                    "SpawnZ 	INTEGER," +
                    "Yaw 		DECIMAL," +
                    "Pich 		DECIMAL," +
                    "Created 	TIMESTAMP," +
                    "PlayTime	INT," +
                    "Size 		TINYINT," +
                    "PRIMARY KEY (id), KEY (UUID)" +
                    ")"
            );
}
