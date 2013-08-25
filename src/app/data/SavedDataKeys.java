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

package app.data;

import android.content.*;

/**
 * This enum makes it really easy to read/write from {@link SharedPreferences}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/28/13, 8:15 PM
 */
public enum SavedDataKeys {
  lbl_status;

public void put(Context ctx, String value) {
  SharedPreferences.Editor prefs = ctx.getSharedPreferences(SavedDataKeys.class.getSimpleName(),
                                                            Context.MODE_PRIVATE)
                                      .edit();
  prefs.putString(this.name(), value);
  prefs.commit();
}

public String get(Context ctx, String defaultValue) {
  SharedPreferences prefs = ctx.getSharedPreferences(SavedDataKeys.class.getSimpleName(),
                                                     Context.MODE_PRIVATE);
  return prefs.getString(this.name(), defaultValue);
}

}