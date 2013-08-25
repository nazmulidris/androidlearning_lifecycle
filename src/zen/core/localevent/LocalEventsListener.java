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
 * You must use this class instead of {@link BroadcastReceiver} in order to work seamlessly with the
 * app framework (and classes like {@link LifecycleHelper}).
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/28/13, 11:39 AM
 */
public abstract class LocalEventsListener extends BroadcastReceiver {

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// abstract
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** define what {@link LocalEvents} you are interested in */
public abstract LocalEvents getLocalEvent();

/** used to identify this observer */
public abstract String getName();

/**
 * override this in subclass to do something when {@link #getLocalEvent()} is received
 *
 * @param stringPayload might be null
 * @param objectPayload might be null
 * @param extras        might be null (this contains the entire intent bundle as passed to the
 *                      {@link LocalEventsManager} to fire the event)
 */
public abstract void onReceive(String stringPayload, Object objectPayload, Bundle extras);

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// concrete
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** this method simply extracts the payloads from the local event and calls the simpler {@link #onReceive} */
public void onReceive(Context context, Intent intent) {
  try {
    onReceive(LocalEventsManager.getStringPayload(intent),
              LocalEventsManager.getObjectPayload(intent),
              intent != null ? intent.getExtras() : null
    );
  }
  catch (Exception e) {
    AndroidUtils.logErr(
        IconPaths.MyApp,
        "LocalEventsListener - problem calling onReceive on subclass", e);
  }
}

/** call this to register this listener with the {@link LocalBroadcastManager} */
public void register(Context ctx) {
  try {
    LocalEventsManager.registerBroadcastReceiver(ctx, getLocalEvent(), this);
  }
  catch (Exception e) {
    AndroidUtils.logErr(
        IconPaths.MyApp,
        "LocalEventsListener - problem registering listener to LocalBroadcastManager", e);
  }
}

/** call this to unregister this listener with the {@link LocalBroadcastManager} */
public void unregister(Context ctx) {
  try {
    LocalEventsManager.unregisterBroadcastReceiver(ctx, this);
  }
  catch (Exception e) {
    AndroidUtils.logErr(
        IconPaths.MyApp,
        "LocalEventsListener - problem unregistering listener from LocalBroadcastManager", e);
  }
}

}//end class LocalEventsListener
