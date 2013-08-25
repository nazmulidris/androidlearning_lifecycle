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
import android.os.*;
import android.widget.*;
import app.data.*;
import app.screen.*;
import integration.*;
import zen.base.*;
import zen.core.*;
import zen.core.localevent.*;
import zen.utlis.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * This is a service that's started when the app launches. It spawns an scheduled executor
 * that does this every second:
 * <ol>
 * <li>creates a new notification with some timestamp</li>
 * <li>broadcasts a local event ({@link LocalEvents#Test}) and passes a String
 * and a Date object to all listeners {@link LocalEventsListener}</li>
 * </ol>
 * <p/>
 * It also registers a {@link LocalEventsListener} that listens to
 * {@link LocalEvents#ShutdownMyService}, which actually causes this service to shut itself down.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 11/21/12, 3:49 PM
 */
public class MyService extends SimpleService implements ConstantsIF {

/** this is used to run service operations in a background thread */
ScheduledExecutorService executor;

public void onCreate() {
  super.onCreate();
  getLifecycleHelper().addResource(new LocalEventsListener() {
    public LocalEvents getLocalEvent() {
      return LocalEvents.ShutdownMyService;
    }

    public String getName() {
      return "responds to shutdown service event";
    }

    public void onReceive(String stringPayload, Object objectPayload, Bundle extras) {
      stopServiceNow();
    }
  });
}

/** service is started, by call to {@link #startServiceNow(Context)} */
public int onStartCommand(Intent intent, int flags, int startId) {
  AndroidUtils.log(IconPaths.MyApp, "MyService start");

  /*
  // another way of checking to see if this service has started
  if (getAppData().observablePropertyManager.getValueAsBoolean(ObservableProperty.ServiceIsStarted))
    _startRecurringTask();
  */
  if (executor == null) { _startRecurringTask(); }

  return START_STICKY;
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// helper methods to start/stop the service from elsewhere
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** start the service (will call {@link #onStartCommand(Intent, int, int)}) */
public static void startServiceNow(Context ctx) {
  ctx.startService(new Intent(ctx, MyService.class));
}

/** stop this service, and update any observable properties */
public void stopServiceNow() {
  // notify observers that this service has stopped
  getAppData().observablePropertyManager.setValue(ObservableProperty.ServiceIsStarted, Boolean.FALSE);
  // actually stop the service
  stopForeground(true);
  stopSelf();
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// service implementation
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** actually start the executor that performs a recurring task */
private void _startRecurringTask() {
  // create the background thread (via the executor)
  executor = Executors.newSingleThreadScheduledExecutor();
  // add it to lifecycleHelper so it can be reclaimed automagically
  lifecycleHelper.addResource(executor);

  Toast.makeText(getApplicationContext(), "Starting the service!", Toast.LENGTH_LONG).show();

  getAppData().observablePropertyManager.setValue(ObservableProperty.ServiceIsStarted, Boolean.TRUE);

  NotificationsManager.moveServiceToForeground(
      Notifications.myservice,
      this,
      "MyService started at: " + new Date().toString(),
      "Info from MyService",
      new Intent(this, MyActivity.class),
      Notification.FLAG_ONGOING_EVENT
  );

  executor.scheduleAtFixedRate(
      new Runnable() {
        public void run() {
          StringBuilder sb = new StringBuilder();
          sb.append("MyService: time is [").append(new Date().toString()).append("]");
          AndroidUtils.log(IconPaths.MyApp, sb.toString());

          // update notification
          NotificationManager mgr =
              (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

          Notifications n = Notifications.myservice;
          mgr.notify(n.id,
                     new Notification.Builder(MyService.this)
                         .setSmallIcon(n.resId)
                         .setContentTitle("MyService updated at:" + new Date().toString())
                         .setContentText(sb.toString())
                         .setContentIntent(
                             PendingIntent.getActivity(MyService.this,
                                                       0,
                                                       new Intent(MyService.this, MyActivity.class),
                                                       0)
                         )
                         .build());

          // broadcast event locally
          LocalEventsManager.fireEvent(MyService.this, LocalEvents.Test,
                                       "passing a date object",
                                       new Date());

        }
      },
      0, MyServiceRepeatDelay_ms, TimeUnit.MILLISECONDS
  );
}
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// service stub and binding
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public IBinder onBind(Intent intent) {
  return new MyServiceStub();
}

public class MyServiceStub extends Binder {

  public MyService getService() {return MyService.this;}
}

}//end class MyService