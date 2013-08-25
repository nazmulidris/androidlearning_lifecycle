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
import zen.utlis.*;

import java.lang.ref.*;
import java.util.*;

/**
 * This is a class that is used to pass object references. After you call {@link #putObject(Object)}
 * for a given key, then you can call {@link #getObject(String)}.
 * <p/>
 * This is a way to get around Android not allowing passing Java object references in {@link Intent}
 * objects. The use case is passing an object between a service and activity/fragment/UI, or between
 * a service and another service, or between multiple activities/fragments/UIs. It actually works
 * really well with the decoupled nature of Android fragments and services.
 * <p/>
 * The {@link #hashMap} values are automatically garbage collected through the use of
 * {@link SoftReference}s and the {@link SoftHashMap}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 3/3/13, 2:17 PM
 */
public class IntentHelper {

private static SoftHashMap<String, Object> hashMap = new SoftHashMap<String, Object>();

/** @return UUID for the newly inserted object; this will be null if object is null */
public static String putObject(Object object) {
  if (object == null) { return null; }
  String key = UUID.randomUUID().toString();
  hashMap.put(key, object);
  return key;
}

/** @return get the object corresponding to the UUID, maybe null if not set */
public static Object getObject(String key) {
  if (key != null) { return hashMap.get(key); }
  else { return null; }
}

}// end class IntentHelper