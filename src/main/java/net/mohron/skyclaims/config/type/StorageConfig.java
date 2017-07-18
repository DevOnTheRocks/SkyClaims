/*
 * SkyClaims - A Skyblock plugin made for Sponge
 * Copyright (C) 2017 Mohron
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * SkyClaims is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with SkyClaims.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.mohron.skyclaims.config.type;

import net.mohron.skyclaims.SkyClaims;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.io.File;

@ConfigSerializable
public class StorageConfig {

    @Setting(value = "Location", comment = "The location to store SkyClaims data. Default: ${CONFIG}/data")
    private String location;
    @Setting(value = "Type", comment = "The type of data storage to use. Supports [SQLite, MySQL]")
    private String type;
    @Setting(value = "MySQL", comment = "MySQL Not Yet Implemented!")
    private MysqlConfig mysqlConfig;

    public StorageConfig() {
        location = "${CONFIG}/data";
        type = "SQLite";
        mysqlConfig = new MysqlConfig();
    }

    public String getLocation() {
        return location
            .replace("*CONFIG*", SkyClaims.getInstance().getConfigDir().toString())
            .replace("${CONFIG}", SkyClaims.getInstance().getConfigDir().toString())
            .replace("/", File.separator)
            .replace("\\", File.separator);
    }

    public String getType() {
        return type;
    }

    public MysqlConfig getMysqlConfig() {
        return mysqlConfig;
    }
}