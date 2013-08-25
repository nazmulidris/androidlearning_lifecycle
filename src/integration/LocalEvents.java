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

import zen.core.*;
import zen.core.localevent.*;

/**
 * Declare all the local events that your app needs, and you can do the following with these
 * events:
 * <ol>
 * <li>use {@link LocalEventsManager} to fire events across your app (does not leave the
 * process of the app)</li>
 * <li>register {@link LocalEventsListener} to respond to these events in your app; the
 * listener should be registered as a resource with {@link LifecycleHelper}</li>
 * </ol>
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 8/23/13, 12:43 PM
 */
public enum LocalEvents {

  Test,
  Debug,
  ShutdownMyService,
  DB_blob_Change,
  DB_kvp_Change,

}//end enum LocalEvents
