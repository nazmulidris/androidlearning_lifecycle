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

package zen.utlis;

import android.content.*;
import android.graphics.*;
import android.graphics.drawable.*;
import android.os.*;
import android.telephony.*;
import android.text.*;
import android.text.method.*;
import android.util.*;
import android.widget.*;
import zen.core.*;

import java.io.*;
import java.util.*;

/**
 * Note - In Android, there is no built in way to sense whether the EDT (aka UI Thread)
 * is currently running!
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since Jul 29, 2010, 11:48:43 PM
 */
public class AndroidUtils {

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// drawable resources
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
public static int getResId(Context ctx, String resourceName) {
  return ctx.getResources().getIdentifier(
      resourceName,
      "drawable",
      ctx.getPackageName()
  );
}


//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// toast stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public static void showToastShort(Context ctx, String msg) {
  Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
}

public static void showToastLong(Context ctx, String msg) {
  Toast.makeText(ctx, msg, Toast.LENGTH_LONG).show();
}

public static void showToastShort(Context ctx, int rid) {
  Toast.makeText(ctx, ctx.getString(rid), Toast.LENGTH_SHORT).show();
}

public static void showToastLong(Context ctx, int rid) {
  Toast.makeText(ctx, ctx.getString(rid), Toast.LENGTH_LONG).show();
}


//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// field filters
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public static EditText applyNumericFilter(EditText field) {
  field.setKeyListener(new DigitsKeyListener());
  return field;
}

public static EditText applyMaxCharsFilter(EditText field, int length) {
  InputFilter[] FilterArray = new InputFilter[1];
  FilterArray[0] = new InputFilter.LengthFilter(length);
  field.setFilters(FilterArray);
  return field;
}


//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// rudimentary access control
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/**
 * Nazmul's Droid 2 - A00000226E8480
 * Izabels' LG Ally - A000002121A9F0
 * Simulator        - 000000000000000
 * Neil Hanna's Droid X - A0000022436CA0
 * Izzy's HTC Incredible - A1000013776A46
 * Nazmul's HTC Incredible - A1000013783B65
 */
public static boolean isDeviceAuthorized(Context ctx) {

  String deviceid = getDeviceId(ctx);
  if (
      deviceid.equals("000000000000000") ||
      deviceid.equals("A00000226E8480") ||
      deviceid.equals("A000002121A9F0") ||
      deviceid.equals("A1000013776A46") ||
      deviceid.equals("A1000013783B65") ||
      deviceid.equals("A0000022436CA0")
      )
  { return true; }
  else { return false; }

}

public static String getDeviceId(Context ctx) {
  TelephonyManager telephonyManager = (TelephonyManager) ctx.getSystemService(Context.TELEPHONY_SERVICE);
  String deviceid = telephonyManager.getDeviceId();
  return deviceid;
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// assets
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** @return null if fname couldnt be found or read */
public static String getAssetAsString(String fname, Context ctx) {

  try {
    InputStream inputStream = ctx.getAssets().open(fname);
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    byte buf[] = new byte[100 * 1024]; // 100k read buffer
    int len;

    while ((len = inputStream.read(buf)) != -1) {
      outputStream.write(buf, 0, len);
    }

    outputStream.close();
    inputStream.close();
    return outputStream.toString();

  }
  catch (Exception e) {
    AndroidUtils.logErr(IconPaths.System,
                        new StringBuilder()
                            .append("Problem loading asset: ")
                            .append(fname).toString(),
                        e);
  }

  return null;

}

/** processes one line at a time with the given functor */
public static void processStringAssetByLine(String fname,
                                            Context ctx,
                                            LineProcessorIF functor)
{

  try {
    InputStream is = ctx.getAssets().open(fname);
    final InputStreamReader isr = new InputStreamReader(is);
    BufferedReader br = new BufferedReader(isr);

    String line = null;
    while ((line = br.readLine()) != null) {
      if (!SharedUtils.isNullOrEmpty(line)) { functor.processLine(line); }
    }

    br.close();
    isr.close();
    is.close();

  }
  catch (Exception e) {
    AndroidUtils.logErr(IconPaths.System,
                        new StringBuilder()
                            .append("Problem processing string asset by line: ")
                            .append(fname).toString(),
                        e);
  }

}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// helper methods
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public static ContextHolderIF getSimpleContextHolderWrapper(final Handler myhandler,
                                                            final Context mycontext)
{
  return new ContextHolderIF() {
    public Handler getMyHandler() {
      return myhandler;
    }

    public Context getMyContext() {
      return mycontext;
    }
  };
}

/** will start a service, given the class object of the service */
public static void startService(Context ctx, Class c) {
  ctx.startService(new Intent(ctx, c));
}

/** will start an activity, given the class object of the activity */
public static void startActivity(Context ctx, Class c) {
  ctx.startActivity(new Intent(ctx, c));
}

/** will stop a service, given the class object of the service */
public static void stopService(Context ctx, Class c) {
  ctx.stopService(new Intent(ctx, c));
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// LOGGING
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public static void log(IconPaths marker, String msg) {
  if (PerfDirectives.EnableLogging) { Log.i(marker.name(), msg); }
}

public static void log(IconPaths marker, String msg1, String msg2) {
  if (PerfDirectives.EnableLogging) {
    Log.i(marker.name(), new StringBuilder().append(msg1).append(" : ").append(msg2).toString());
  }
}

public static void log(IconPaths marker, String msg1, String msg2, String msg3) {
  if (PerfDirectives.EnableLogging) {
    Log.i(marker.name(),
          new StringBuilder()
              .append(msg1).append(" : ")
              .append(msg2).append(" : ")
              .append(msg3)
              .toString());
  }
}

public static void logErr(IconPaths marker, String msg) {
  if (PerfDirectives.EnableLogging) { Log.e(marker.name(), String.format("!!! %s", msg)); }
}

public static void logErr(IconPaths marker, String msg, Throwable t) {
  if (PerfDirectives.EnableLogging) { Log.e(marker.name(), String.format("!!! %s", msg), t); }
}

public static void logErr(IconPaths marker, String msg, String msg2, Throwable t) {
  if (PerfDirectives.EnableLogging) {
    Log.e(marker.name(),
          new StringBuilder()
              .append(String.format("!!! %s", msg))
              .append(" : ")
              .append(msg2)
              .toString(),
          t);
  }
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// MISC
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** convenience method to turn the given items into an arraylist */
public static <T> ArrayList<T> asList(T... items) {
  ArrayList<T> retval = new ArrayList<T>();
  for (T item : items) {
    retval.add(item);
  }
  return retval;
}

/** convenience method to turn the given items into an arraylist */
public static <T> T[] asArray(T... items) {
  return items;
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// graphics stuff
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

/** simple resizing of the given image to the desired width/height */
public static Bitmap getBitmapFromResource(Context ctx, int id,
                                           int x1, int y1)
{
  BitmapDrawable bd = (BitmapDrawable) ctx.getResources().getDrawable(id);
  Bitmap b = bd.getBitmap();

  int x = b.getWidth();
  int y = b.getHeight();

  float scaleX = (float) x1 / x;
  float scaleY = (float) y1 / y;
  float scale = 1f;

  boolean scaleXInBounds = (scaleX * x) <= x1 && (scaleX * y) <= y1;
  boolean scaleYInBounds = (scaleY * x) <= x1 && (scaleY * y) <= y1;

  if (scaleXInBounds && scaleYInBounds) { scale = (scaleX > scaleY) ? scaleX : scaleY;}
  else if (scaleXInBounds) {scale = scaleX;}
  else if (scaleYInBounds) {scale = scaleY;}

  return Bitmap.createScaledBitmap(b, (int) (scale * x), (int) (scale * y), true);
}

/** @see <a href="http://developer.android.com/guide/practices/screens_support.html#dips-pels">Docs</a> */
public static int dpToPx(Context ctx, float dp) {
  // Get the screen's density scale
  final float scale = ctx.getResources().getDisplayMetrics().density;
  // Convert the dps to pixels, based on density scale
  return (int) (dp * scale + 0.5f);
}

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// Asset file processing helpers
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

public interface LineProcessorIF {

  public void processLine(String line);

}//end class LineProcessorIF

}//end class AndroidUtils
