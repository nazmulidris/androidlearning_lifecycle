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

package app.service;

import android.app.*;
import android.content.*;
import app.data.*;
import app.screen.*;
import integration.*;
import zen.base.*;
import zen.core.*;
import zen.utlis.*;

import java.util.*;

/**
 * This is an intent service that's run repeatedly by an alarm that is set in {@link MyActivity}.
 * Note that this service actually does run in a background thread by default. This service also
 * has to be added to the AndroidManifest.xml file for it to work, just like a normal Service.
 * <p/>
 * Also note that the alarm which triggers this service recurrently also must be cancelled when
 * this is no longer desired, since the alarm lives outside the app (and has a different lifetime
 * altogether).
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/13/13, 10:49 PM
 */
public class MyIntentService extends SimpleIntentService implements ConstantsIF {

/** default constructor */
public MyIntentService() {
  super(MyIntentService.class.getSimpleName());
}

/**
 * helper method to create a recurring alarm that's used to fire off this intent service
 *
 * @param ctx    caller has to supply this since this is a static method
 * @param enable true means create the alarm, false means cancel it
 */
public static void scheduleRecurringAlarm(Context ctx, boolean enable) {
  PendingIntent pendingIntent = PendingIntent.getService(ctx,
                                                         -1,
                                                         new Intent(ctx, MyIntentService.class),
                                                         PendingIntent.FLAG_UPDATE_CURRENT
  );
  AlarmManager mgr = (AlarmManager) ctx.getSystemService(ALARM_SERVICE);
  if (enable) {
    mgr.setInexactRepeating(AlarmManager.RTC,
                            System.currentTimeMillis(),
                            MyIntentServiceRepeatDelay_ms,
                            pendingIntent);
    AndroidUtils.log(IconPaths.MyApp, "MyIntentService alarm created");
  }
  else {
    mgr.cancel(pendingIntent);
    AndroidUtils.log(IconPaths.MyApp, "MyIntentService alarm cancelled");
  }
}

/** create notification */
protected void onHandleIntent(Intent intent) {

  NotificationsManager.buildAndNotify(
      Notifications.myintentservice,
      this,
      "My Intent Service at: " + new Date().toString(),
      "Info from intent service",
      new Intent(this, MyActivity.class),
      Notification.FLAG_AUTO_CANCEL
  );

  AndroidUtils.log(IconPaths.MyApp, "MyIntentService run");

}

}//end class MyIntentService
