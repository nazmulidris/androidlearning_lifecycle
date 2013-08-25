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

package zen.core.localevent;

import android.content.*;
import android.os.*;
import android.support.v4.content.*;
import integration.*;
import zen.core.*;
import zen.utlis.*;

/**
 * This is a helper class that makes it easy to fire local events using {@link LocalBroadcastManager}.
 * It also works hand in hand with {@link LocalEvents} enum that defines all the local events.
 * It also uses {@link IntentHelper} to make the magic of passing objects around between activities
 * and services happen.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 3/11/13, 2:49 PM
 */
public class LocalEventsManager {

enum PayloadKeys {
  String, Object
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// history stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
private static SoftHashMap<LocalEvents, String> localHistory = new SoftHashMap<LocalEvents, String>();

public static boolean historyEntryExistsFor(LocalEvents event) {
  return localHistory.containsKey(event);
}

public static String getLastPayloadFor(LocalEvents event) {
  try {
    return localHistory.get(event);
  }
  catch (Exception e) {
    return null;
  }
}

private static void _saveHistory(String name, String payload1) {
  try {
    localHistory.put(LocalEvents.valueOf(name), payload1 != null ? payload1 : "");
  }
  catch (Exception e) {}
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// payload stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** extract the string value of the key from the intent's bundle */
public static String getStringPayload(Intent intent) {
  try {
    String key = PayloadKeys.String.name();
    return intent.getExtras().getString(key);
  }
  catch (Exception e) {return null;}
}

/** extract the object value of the key from the intent's bundle (using {@link IntentHelper}) */
public static Object getObjectPayload(Intent intent) {
  try {
    String key = PayloadKeys.Object.name();
    Object value = null;

    if (intent.getExtras() != null && intent.getExtras().containsKey(key)) {
      String value_proxyKey = intent.getExtras().getString(key);
      value = IntentHelper.getObject(value_proxyKey);
    }

    return value;

  }
  catch (Exception e) {return null;}
}

private static void putStringPayload(Intent intent, String payload) {
  if (intent != null && payload != null) {
    intent.putExtra(PayloadKeys.String.name(), payload);
  }
}

/** @return the UUID of the inserted object */
private static String putObjectPayload(Intent intent, Object payload) {
  if (intent != null && payload != null) {
    String value_proxyKey = IntentHelper.putObject(payload);
    intent.putExtra(PayloadKeys.Object.name(), value_proxyKey);
    return value_proxyKey;
  }
  else { return null; }
}

/**
 * utility method to quickly create an intent for this event, with it's extra bundle key-value pair populated,
 * used by {@link LocalBroadcastManager} to fire the event
 */
private static Intent _getIntent(String name, String stringPayload, Object objectPayload) {
  Intent retval = new Intent(name);
  if (stringPayload != null) { putStringPayload(retval, stringPayload); }
  if (objectPayload != null) { putObjectPayload(retval, objectPayload); }
  _saveHistory(name, stringPayload);
  return retval;
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// firing stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * create a new intent that has a normal stringPayload, and an extra objectPayload as well (using {@link IntentHelper}).
 * <p/>
 * {@link LocalEventsListener#onReceive(String, Object, Bundle)} is on the other side of this method.
 *
 * @param stringPayload can be null
 * @param objectPayload can be null
 */
public static void fireEvent(Context ctx, LocalEvents type, String stringPayload, Object objectPayload) {
  // fire actual event
  LocalBroadcastManager.getInstance(ctx).sendBroadcast(
      _getIntent(type.name(), stringPayload, objectPayload)
  );

  // fire debug event
  if (PerfDirectives.EnableDebugMode) {
    LocalBroadcastManager.getInstance(ctx).sendBroadcast(
        _getIntent(LocalEvents.Debug.name(),
                   null,
                   new DebugEventHolder(type, stringPayload, objectPayload))
    );
  }

  // log it
  AndroidUtils.log(IconPaths.LocalEvents,
                   String.format("LocalEventsManager - fired event type[%s] stringPayload[%s] objectPayload[%s]",
                                 type.name(), stringPayload, objectPayload
                   )
  );

}

public static class DebugEventHolder {

  public LocalEvents type;
  public String      stringPayload;
  public Object      objectPayload;

  public DebugEventHolder(LocalEvents type, String stringPayload, Object objectPayload) {
    this.type = type;
    this.stringPayload = stringPayload;
    this.objectPayload = objectPayload;
  }

}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// broadcast receiver stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * this actually registers the given listener to the {@link LocalBroadcastManager} for the intents
 * that match the {@link IntentFilter}.
 */
public static void registerBroadcastReceiver(Context ctx, LocalEvents type, BroadcastReceiver listener) {
  LocalBroadcastManager.getInstance(ctx)
                       .registerReceiver(listener, new IntentFilter(type.name()));
}

/** this actually unregisters the given listener from the {@link LocalBroadcastManager} */
public static void unregisterBroadcastReceiver(Context ctx, BroadcastReceiver listener) {
  LocalBroadcastManager.getInstance(ctx)
                       .unregisterReceiver(listener);
}

}//end class LocalEventsManager