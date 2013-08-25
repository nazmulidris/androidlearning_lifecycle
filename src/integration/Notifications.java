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

package integration;

import sample.app.androidlifecycle.*;

/**
 * This enum just makes it easy to fire notification, and it allows bundling each
 * enum to an icon as well.
 * <p/>
 * <a href="http://goo.gl/5Ztfn">Refer to this icon guideline</a> for icon sizes.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 8/23/13, 12:43 PM
 */
public enum Notifications {
  myservice(1, R.drawable.notify_myservice),
  boot(2, R.drawable.notify_boot),
  network(3, R.drawable.notify_network),
  myintentservice(4, R.drawable.notify_intentservice);

public int id;
public int resId;

Notifications(int id, int resId) {
  this.id = id;
  this.resId = resId;
}

}
