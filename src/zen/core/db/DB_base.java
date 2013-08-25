/*
 * Copyright [2013] [Nazmul Idris]
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zen.core.db;

import android.content.*;
import android.database.sqlite.*;

/**
 * Base class for impl of {@link DB_kvp} and {@link DB_blob}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/29/13, 3:03 PM
 */
public abstract class DB_base implements DBConstantsIF {

/** the database connection object */
public SQLiteDatabase dbConnection;
/** used to fire events */
public Context        ctx;
/** stores the db name */
public String         dbName;
/** stores the db version */
public int            dbVersion;

public DB_base(Context ctx, String dbName, int dbVersion) {
  this.ctx = ctx;
  this.dbName = dbName;
  this.dbVersion = dbVersion;
  dbConnection = actuallyCreateDatabase();
}

public abstract SQLiteDatabase actuallyCreateDatabase();

/** get the db name */
public String getDbName() {return dbName;}

/** get the db version */
public int getDbVersion() {return dbVersion;}

}//end class DB_base
