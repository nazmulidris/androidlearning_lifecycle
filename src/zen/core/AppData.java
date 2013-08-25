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

package zen.core;

import android.app.*;
import android.content.*;
import android.os.*;
import app.data.*;
import integration.*;
import zen.core.db.*;
import zen.core.observableprops.*;
import zen.utlis.*;

/**
 * This is the base {@link Application} class that uses the Zen Framework classes.
 * It creates the {@link ObservablePropertyManager} and {@link DBManager} objects.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 11/21/12, 4:06 PM
 */
public class AppData extends Application implements ConstantsIF, ContextHolderIF {

/** this is a database that's created at {@link #_init} */
public DBManager                 dbManager;
/** this is used to run closures/functors on the main thread */
public Handler                   handlerMainThread;
/** used to manage {@link ObservableProperty} enum */
public ObservablePropertyManager observablePropertyManager;

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// android lifecycle hooks
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** before a service or activity of this application is created, this method is called */
public void onCreate() {
  super.onCreate();
  _init(new Handler(getApplicationContext().getMainLooper()), getApplicationContext());
}

/**
 * this method never gets called - <a href="http://goo.gl/YI1JG">it's only for emulators</a>!
 * when all services and activities tied to this application are destroyed, and this application is about to be
 * removed altogether, this method is called.
 */
@Deprecated
public void onTerminate() {
  super.onTerminate();
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// underlying impl of android lifecycle hooks
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * init this singleton with a handler bound to the main thread.
 * also create a db connection. Note - the db connections are not
 * explicitly released (since there's no way to hook into this
 * in Android {@link Application#onTerminate()} is just a fake
 * method!
 */
public void _init(Handler handler, Context ctx) {
  // save a ref to the handler on the main thread
  if (handlerMainThread == null) { handlerMainThread = handler; }

  // create ObservablePropertyManager
  if (observablePropertyManager == null) { observablePropertyManager = new ObservablePropertyManager(handler); }

  // create the database & register it with {@link #mapOfData} as an observable property
  if (dbManager == null) {dbManager = new DBManager(ctx);}

  AndroidUtils.log(IconPaths.MyApp,
                   "AppData.init - created myDbs object and saved it to observable property map");
}


//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// framework integration
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
public Handler getMyHandler() {
  return handlerMainThread;
}

public Context getMyContext() {
  return getApplicationContext();
}
}//end class AppData