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

import sample.app.androidlifecycle.*;

/**
 * These are system icons that are tied to logging as well.
 * 1. name()  - name of the thing, like "Alert"
 * 2. value() - android res id (int) of the thing
 * 3. text()  - optional string for display in toast, defaults to name() if not set.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @see AndroidUtils#log
 * @see AndroidUtils#logErr
 * @since Jul 29, 2010, 11:53:53 PM
 */
public enum IconPaths {

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// app icons
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

  /** app scope - things that are done by your app that are using framework stuff heavily */
  MyApp(R.drawable.zen_app, "App Info"),

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// zen system icons
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

  /** webmethod scope - stuff related to webmethods */
  LocalEvents(R.drawable.zen_flag, "Local event fired"),
  /** webmethod scope - stuff related to webmethods */
  WebMethod(R.drawable.zen_webmethod, "Web method call"),
  /** framework scope - stuff related to the framework classes */
  System(R.drawable.zen_system, "Zen App Framework"),
  /** framework scope - stuff related to the framwork and heavy resource auto-management */
  Resource(R.drawable.zen_resource, "Resource Info"),
  /** framework scope - stuff related to DB and filesystem */
  Storage(R.drawable.zen_storage),
  /** framework scope - stuff related to low level network operations */
  Network(R.drawable.zen_network, "Network connectivity"),
  /** framework scope - stuff related to Location Providers */
  Gps(R.drawable.zen_gps, "GPS Alert"),
  /** framework or app scope - stuff related to social networking */
  Social(R.drawable.zen_social, "Social"),

  Wireless(R.drawable.zen_wireless, "Wireless connectivity"),
  Alert(R.drawable.zen_alert, "Alert"),
  Check(R.drawable.zen_check),
  Debug(R.drawable.zen_debug, "Debug Info"),
  Clock(R.drawable.zen_clock),
  Email(R.drawable.zen_email),
  Flag(R.drawable.zen_flag),
  Info(R.drawable.zen_info, "Information"),
  Question(R.drawable.zen_question);

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// the actual value is tied to the android resource...
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

private int    val;
private String text;

IconPaths(int res) {
  val = res;
}

IconPaths(int res, String text) {
  val = res;
  this.text = text;
}

public int resId() {return val;}

public String text() {
  return this.text == null ? name() : this.text;
}

}//end interface IconPaths
