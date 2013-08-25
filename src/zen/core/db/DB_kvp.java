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
import android.database.*;
import android.database.sqlite.*;
import android.provider.*;
import integration.*;
import zen.core.localevent.*;
import zen.utlis.*;

import java.util.*;

/**
 * You have to provide the dbName and dbVersion params to create an object of this type.
 * You can create as many of these as you want. The primary characteristic of this class is
 * that it creates a table that has 2 columns:
 * <ol>
 * <li>Key (string) column that uniquely identifies this row</li>
 * <li>Payload (string) column that contains a String which is the payload text</li>
 * </ol>
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/15/13, 6:20 PM
 */
public class DB_kvp extends DB_base {

/** constructor that creates the db connection, release this in {@link #shutdown()} */
public DB_kvp(Context ctx, String dbName, int dbVersion) {
  super(ctx, dbName, dbVersion);
}

/** actually create the db connection, release this in {@link #shutdown()} */
@Override
public SQLiteDatabase actuallyCreateDatabase() {
  return new Schema(ctx, dbName, dbVersion).getWritableDatabase();
}

/**
 * remove the row with the given id from the table
 *
 * @return null if the id couldn't be found, otherwise returns the value that was removed ({@link Schema#COL_VALUE})
 */
public String remove(long id) {
  String retval = get(id);
  int rowCount = dbConnection.delete(Schema.TABLE_MAP,
                                     Schema.SQL_WHERE_COL_ID,
                                     new String[]{String.valueOf(id)});
  if (rowCount > 0) { dbConnection.execSQL(Schema.SQL_PURGE); }
  LocalEventsManager.fireEvent(ctx, LocalEvents.DB_kvp_Change, dbName, null);
  return retval;
}

/**
 * remove the row that matches the given key to the {@link Schema#COL_KEY} col
 *
 * @param key remove the key value pair from the table, that matches this key
 *
 * @return null if the key couldn't be found, otherwise returns the value that was removed ({@link Schema#COL_VALUE})
 */
public String remove(String key) {
  String retval = get(key);
  int rowCount = dbConnection.delete(Schema.TABLE_MAP,
                                     Schema.SQL_WHERE_COL_KEY,
                                     new String[]{key});
  if (rowCount > 0) { dbConnection.execSQL(Schema.SQL_PURGE); }
  LocalEventsManager.fireEvent(ctx, LocalEvents.DB_kvp_Change, dbName, null);
  return retval;
}

/**
 * see if the key exists in the database (if there's a row who's {@link Schema#COL_KEY} equals key)
 *
 * @return true means that the key exists, false means that it doesn't
 */
public boolean containsKey(String key) {
  Cursor cursor = dbConnection.query(Schema.TABLE_MAP,
                                     Schema.COLS,
                                     Schema.SQL_WHERE_COL_KEY,
                                     new String[]{key},
                                     null, null, null);
  if (cursor.getCount() > 0) {return true;}
  else { return false; }
}

/**
 * get the payload string for the row, where the {@link Schema#COL_KEY} equals the given key
 *
 * @return null if the key can't be found, otherwise returns the value from the {@link Schema#COL_VALUE}
 */
public String get(String key) {
  Cursor cursor = dbConnection.query(Schema.TABLE_MAP,
                                     Schema.COLS,
                                     Schema.SQL_WHERE_COL_KEY,
                                     new String[]{key},
                                     null, null, null);
  cursor.moveToFirst();
  String retval = resolveCursor(cursor);
  cursor.close();
  return retval;
}

/**
 * get the payload string for the row, with {@link Schema#COL_ID} equal to the given id
 *
 * @return null if the id can't be found, otherwise returns the value from the {@link Schema#COL_VALUE}
 */
public String get(long id) {
  Cursor cursor = dbConnection.query(Schema.TABLE_MAP,
                                     Schema.COLS,
                                     Schema.SQL_WHERE_COL_ID,
                                     new String[]{String.valueOf(id)},
                                     null, null, null);
  cursor.moveToFirst();
  String retval = resolveCursor(cursor);
  cursor.close();
  return retval;
}

/**
 * gets the number of rows in the table. <a href="http://goo.gl/hxFjq">more info</a>
 *
 * @return number of rows in the table
 */
public long getRowCount() {
  return DatabaseUtils.queryNumEntries(dbConnection, Schema.TABLE_MAP);
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// crud operations
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** simply drops and re-creates the db */
public void removeAll() {
  dbConnection.execSQL(Schema.SQL_DROP_DB);
  dbConnection.execSQL(Schema.SQL_CREATE_DB);
  LocalEventsManager.fireEvent(ctx, LocalEvents.DB_kvp_Change, dbName, null);
}

/**
 * get all the payloads in the table, and return them in a list
 *
 * @return this list might be empty if the table is empty, but will never be null
 */
public ArrayList<String> getAll() {
  ArrayList<String> retval = new ArrayList<String>();

  Cursor cursor = getAllCursor();
  cursor.moveToFirst();
  String value = null;
  do {
    value = resolveCursor(cursor);
    if (value != null) { retval.add(value); }
  }
  while (cursor.moveToNext());
  cursor.close();

  return retval;
}

public Cursor getAllCursor() {
  return dbConnection.query(Schema.TABLE_MAP,
                            Schema.COLS,
                            null, null, null, null, null);
}

/**
 * simple helper to get the value of the {@link Schema#COL_VALUE} from the given cursor
 *
 * @return null if there is a problem dereferencing this cursor
 */
private String resolveCursor(Cursor cursor) {
  try {
    return cursor.getString(cursor.getColumnIndex(Schema.COL_VALUE));
  }
  catch (Exception e) {
    return null;
  }
}

/** test all the code in this class, assume that onCreate() has already been called */
public void test() {

  AndroidUtils.log(IconPaths.Storage, ">> " + getClass().getSimpleName() + " <<");

  // add & get
  AndroidUtils.log(IconPaths.Storage, ">> add() <<");
  long id_test1 = add("key1", "value1");
  AndroidUtils.log(IconPaths.Storage, "adding {key1,value1}, id:" + id_test1 + ", val:" + get("key1"));
  AndroidUtils.log(IconPaths.Storage, " .. get by id: " + id_test1 + ", " + get(id_test1));
  long id_test2 = add("key2", "value2");
  AndroidUtils.log(IconPaths.Storage, "adding {key2,value2}, id:" + id_test2 + ", val:" + get("key2"));
  AndroidUtils.log(IconPaths.Storage, " .. get by id: " + id_test2 + ", " + get(id_test2));
  long id_test3 = add("key3", "value3");
  AndroidUtils.log(IconPaths.Storage, "adding {key3,value3}, id:" + id_test3 + ", val:" + get("key3"));
  AndroidUtils.log(IconPaths.Storage, " .. get by id: " + id_test3 + ", " + get(id_test3));

  // containsKey
  AndroidUtils.log(IconPaths.Storage, ">> containsKey() <<");
  AndroidUtils.log(IconPaths.Storage, "key1 exists: " + containsKey("key1"));
  AndroidUtils.log(IconPaths.Storage, "key2 exists: " + containsKey("key2"));
  AndroidUtils.log(IconPaths.Storage, "key3 exists: " + containsKey("key3"));
  AndroidUtils.log(IconPaths.Storage, "key4 exists: " + containsKey("key4"));

  // getAll
  AndroidUtils.log(IconPaths.Storage, ">> getRowCount() <<");
  AndroidUtils.log(IconPaths.Storage, String.valueOf(getRowCount()));

  AndroidUtils.log(IconPaths.Storage, ">> getAll() <<");
  AndroidUtils.log(IconPaths.Storage, getAll().toString());

  // dumping db contents
  AndroidUtils.log(IconPaths.Storage, ">> dumping entire table contents <<");
  AndroidUtils.log(IconPaths.Storage, DatabaseUtils.dumpCursorToString(dbConnection.query(Schema.TABLE_MAP,
                                                                                          Schema.COLS,
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          null)));

  // updating
  AndroidUtils.log(IconPaths.Storage, ">> update() <<");
  String old_test1_val = update(id_test1, "test1mod");
  AndroidUtils.log(IconPaths.Storage, "updated key1, old value: " + old_test1_val);
  String old_test3_val = update(id_test3, "test3mod");
  AndroidUtils.log(IconPaths.Storage, "updated key3, old value: " + old_test3_val);
  String old_test2_val = update("key2", "newKey2Value");
  AndroidUtils.log(IconPaths.Storage, "updated key2, old value: " + old_test2_val);
  AndroidUtils.log(IconPaths.Storage, " .. get by id:" + id_test2 + ", " + get(id_test2));

  // removing
  AndroidUtils.log(IconPaths.Storage, ">> remove() <<");
  String del_test2_val = remove(id_test2);
  AndroidUtils.log(IconPaths.Storage, "removed id_test2, value was: " + del_test2_val);
  String del_test3_val = remove("key3");
  AndroidUtils.log(IconPaths.Storage, "removed key3, value was:" + del_test3_val);

  // getAll
  AndroidUtils.log(IconPaths.Storage, ">> getRowCount() <<");
  AndroidUtils.log(IconPaths.Storage, String.valueOf(getRowCount()));

  AndroidUtils.log(IconPaths.Storage, ">> getAll() <<");
  AndroidUtils.log(IconPaths.Storage, getAll().toString());

  // dumping db contents
  AndroidUtils.log(IconPaths.Storage, ">> dumping entire table contents <<");
  AndroidUtils.log(IconPaths.Storage, DatabaseUtils.dumpCursorToString(dbConnection.query(Schema.TABLE_MAP,
                                                                                          Schema.COLS,
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          null)));

  // removeAll
  AndroidUtils.log(IconPaths.Storage, ">> removeAll() <<");
  removeAll();

  // getAll
  AndroidUtils.log(IconPaths.Storage, ">> getRowCount() <<");
  AndroidUtils.log(IconPaths.Storage, String.valueOf(getRowCount()));

  AndroidUtils.log(IconPaths.Storage, ">> getAll() <<");
  AndroidUtils.log(IconPaths.Storage, getAll().toString());

  // dumping db contents
  AndroidUtils.log(IconPaths.Storage, ">> dumping entire table contents <<");
  AndroidUtils.log(IconPaths.Storage, DatabaseUtils.dumpCursorToString(dbConnection.query(Schema.TABLE_MAP,
                                                                                          Schema.COLS,
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          null,
                                                                                          null)));

}

/**
 * updates the row with the new payload, and returns the old payload
 *
 * @return null means that the row with given id couldn't be found,
 *         otherwise return the old value of {@link Schema#COL_VALUE} before the update.
 */
public String update(long id, String newPayload) {
  ContentValues map = new ContentValues();
  map.put(Schema.COL_VALUE, newPayload);
  String retval = get(id);
  if (retval != null) {
    dbConnection.update(Schema.TABLE_MAP,
                        map,
                        Schema.SQL_WHERE_COL_ID,
                        new String[]{String.valueOf(id)});
    LocalEventsManager.fireEvent(ctx, LocalEvents.DB_kvp_Change, dbName, null);
  }
  return retval;
}

/**
 * updates the row with the new payload, and returns the old payload
 *
 * @return null means that the row with the given key couldn't be found,
 *         otherwise return the old value of {@link Schema#COL_VALUE} before th update.
 */
public String update(String key, String newValue) {
  ContentValues map = new ContentValues();
  map.put(Schema.COL_VALUE, newValue);
  String retval = get(key);
  if (retval != null) {
    dbConnection.update(Schema.TABLE_MAP,
                        map,
                        Schema.SQL_WHERE_COL_KEY,
                        new String[]{key});
    LocalEventsManager.fireEvent(ctx, LocalEvents.DB_kvp_Change, dbName, null);
  }
  return retval;
}

/**
 * simply insert the given payload into the table and create a new ID ({@link Schema#COL_ID}) for the
 * payload (in the table) which is returned.
 *
 * @return -1 means that an error occurred, otherwise
 *         returns the {@link Schema#COL_ID} value for the newly created row
 *
 * @throws IllegalArgumentException if the key or value is null
 */
public long add(String key, String value) {
  if (key == null || value == null) { throw new IllegalArgumentException("key or value can't be null"); }
  ContentValues map = new ContentValues();
  map.put(Schema.COL_KEY, key);
  map.put(Schema.COL_VALUE, value);
  long rowId = dbConnection.replace(Schema.TABLE_MAP, null, map);
  LocalEventsManager.fireEvent(ctx, LocalEvents.DB_kvp_Change, dbName, null);
  return rowId;
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// self test method
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** close the db connection */
public void shutdown() {
  if (dbConnection != null) {
    dbConnection.close();
    dbConnection = null;
  }
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// db schema creation
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * This class is used to create and maintain the db schema, create the db, and upgrade it.
 * <a href="http://www.sqlite.org/lang_createtable.html">More info on SQLLite SQL</a>
 */
public static class Schema extends SQLiteOpenHelper implements DBConstantsIF {

  public static final  String   TABLE_MAP         = "keyvaluepairs";
  /** UID for a row */
  public static final  String   COL_ID            = BaseColumns._ID;
  public static final  String   COL_KEY           = "key";
  public static final  String   COL_VALUE         = "value";
  /** all the cols in the table */
  public static final  String[] COLS              = {COL_ID, COL_KEY, COL_VALUE};
  /** command to <a href="http://sqlite.org/lang_vacuum.html">purge</a> deleted rows */
  public static final  String   SQL_PURGE         = "VACUUM";
  public static final  String   SQL_WHERE_COL_KEY = COL_KEY + " = ?";
  /** create a table with 2 cols. the PK col is autoincrement, so nothing has to be set on it */
  private static final String   SQL_CREATE_DB     = "create table " + TABLE_MAP + "(" +
                                                    COL_ID + " integer primary key autoincrement, " +
                                                    COL_KEY + " text unique not null, " +
                                                    COL_VALUE + " text not null" +
                                                    ")";
  private static final String   SQL_DROP_DB       = "drop table if exists " + TABLE_MAP;
  private static final String   SQL_WHERE_COL_ID  = COL_ID + " = ?";

  /** constructor */
  private Schema(Context context, String dbName, int dbVersion) {
    super(context, dbName, null, dbVersion);
  }

  /** create the database for the first time */
  public void onCreate(SQLiteDatabase database) {
    database.execSQL(SQL_CREATE_DB);
  }

  /** upgrade the db when the version changes */
  public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
    AndroidUtils.logErr(IconPaths.Storage,
                        getClass().getSimpleName() + "upgrading db to a newer version");
    database.execSQL(SQL_DROP_DB);
    onCreate(database);
  }

}//end class Schema

}//end class DB_blob