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
import integration.*;
import zen.core.*;
import zen.utlis.*;

import java.util.*;

/**
 * This class collects all the database related stuff in one place. this class has to be instantiated
 * in order to be used (it's not static). You can reference it via {@link AppData#dbManager}
 * instance variable.
 * <p/>
 * The enums {@link DB_blob_enum} and {@link DB_kvp_enum} make it really easy to declare your desired
 * databases, and these will all be created when this class is instantiated by it's constructor. All
 * the lifecycle stuff is tied to {@link AppData} and it takes care of creation and destruction of
 * all the database resources.
 */
public class DBManager implements DBConstantsIF {

/** stores db connections to all dbs declared in {@link DB_blob_enum} */
private HashMap<DB_blob_enum, DB_blob> DB_blob_map = new HashMap<DB_blob_enum, DB_blob>();
/** stores db connections to all dbs declared in {@link DB_kvp_enum} */
private HashMap<DB_kvp_enum, DB_kvp>   DB_kvp_map  = new HashMap<DB_kvp_enum, DB_kvp>();

/**
 * create all the declared dbs (kvp & blob) in the following enums -
 * {@link DB_kvp_enum} and {@link DB_blob_enum}
 */
public DBManager(Context ctx) {
  for (DB_blob_enum dbEnum : DB_blob_enum.values()) {
    DB_blob_map.put(dbEnum, new DB_blob(ctx, dbEnum.name(), DbVersion));
  }

  for (DB_kvp_enum dbEnum : DB_kvp_enum.values()) {
    DB_kvp_map.put(dbEnum, new DB_kvp(ctx, dbEnum.name(), DbVersion));
  }
}

/** get a reference to the {@link DB_blob} that's bound to this {@link DB_blob_enum} */
public DB_blob getDB(DB_blob_enum dbEnum) {
  return DB_blob_map.get(dbEnum);
}

/** get a reference to the {@link DB_kvp} that's bound to this {@link DB_kvp_enum} */
public DB_kvp getDB(DB_kvp_enum dbEnum) {
  return DB_kvp_map.get(dbEnum);
}

/**
 * test all the declared dbs (kvp & blob) in the following enums -
 * {@link DB_kvp_enum} and {@link DB_blob_enum}
 */
public void test() {
  for (DB_blob_enum dbEnum : DB_blob_enum.values()) {
    getDB(dbEnum).test();
  }

  for (DB_kvp_enum dbEnum : DB_kvp_enum.values()) {
    getDB(dbEnum).test();
  }
}

/**
 * shutdown all the declared dbs (kvp & blob) in the following enums -
 * {@link DB_kvp_enum} and {@link DB_blob_enum} and stored in
 * {@link #DB_kvp_map} and {@link #DB_blob_map}.
 * <p/>
 * This is deprecated because the maps are no longer static. The initial implementation
 * used static maps, which is why there was this explicit release mechanism; this is
 * due to Android persisting the value of static objects between app lifecycle instances,
 * as long as the underlying Linux process was not destroyed.
 */
@Deprecated
public void shutdown() {

  for (DB_blob_enum dbEnum : DB_blob_enum.values()) {
    getDB(dbEnum).shutdown();
  }

  for (DB_kvp_enum dbEnum : DB_kvp_enum.values()) {
    getDB(dbEnum).shutdown();
  }

  DB_kvp_map.clear();
  DB_blob_map.clear();

  AndroidUtils.log(IconPaths.System,
                   "DBManager.shutdown - cleared all static objects");

}

}//end class DBManager