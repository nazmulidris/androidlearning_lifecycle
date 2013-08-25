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
import integration.*;

/**
 * This class makes it easy to work with {@link Notification} objects.
 * It works hand in hand with the {@link Notifications} enum, that has
 * some pre-defined notifications.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 3/11/13, 4:41 PM
 */
public class NotificationsManager {

/**
 * move the service into the foreground, and create the persistent notification and
 * show it using {@link NotificationManager}
 */
public static void moveServiceToForeground(Notifications nenum,
                                           Service svc,
                                           String contentTitle,
                                           String contentText,
                                           Intent launchActivityIntent,
                                           int notificationFlags)
{
  svc.startForeground(nenum.id,
                      _build(nenum, svc, contentTitle, contentText, launchActivityIntent, notificationFlags));
}

/** create the notification and show it using {@link NotificationManager} */
public static Notification.Builder buildWithoutIcon(Notifications nenum,
                                                    Context ctx,
                                                    String contentTitle,
                                                    String contentText,
                                                    Intent launchActivityIntent)
{
  Notification.Builder retval = new Notification.Builder(ctx)
      .setContentTitle(contentTitle)
      .setContentText(contentText)
      .setContentIntent(
          PendingIntent.getActivity(ctx, 0, launchActivityIntent, 0)
      );
  return retval;
}

/** create the notification and show it using {@link NotificationManager} */
public static void buildAndNotify(Notifications nenum,
                                  Context ctx,
                                  String contentTitle,
                                  String contentText,
                                  Intent launchActivityIntent,
                                  int notificationFlags)
{
  _notify(nenum,
          ctx,
          _build(nenum, ctx, contentTitle, contentText, launchActivityIntent, notificationFlags)
  );
}

/**
 * build the notification given the params, but don't show it
 *
 * @param notificationFlags this is stuff like {@link Notification#FLAG_AUTO_CANCEL}
 *                          and {@link Notification#FLAG_ONGOING_EVENT}
 */
private static Notification _build(Notifications nenum,
                                   Context ctx,
                                   String contentTitle,
                                   String contentText,
                                   Intent launchActivityIntent,
                                   int notificationFlags)
{
  Notification retval = new Notification.Builder(ctx)
      .setSmallIcon(nenum.resId)
      .setContentTitle(contentTitle)
      .setContentText(contentText)
      .setContentIntent(
          PendingIntent.getActivity(ctx, 0, launchActivityIntent, 0)
      )
      .build();
  retval.flags |= notificationFlags;
  return retval;
}

/** actually show the given notification */
private static void _notify(Notifications nenum, Context ctx, Notification n) {
  getManager(ctx).notify(nenum.id, n);
}

/** utility method just to get a reference to the {@link NotificationManager} */
public static NotificationManager getManager(Context ctx) {
  return (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
}

}//end class NotificationsManager
