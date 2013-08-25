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

import app.data.*;
import zen.core.*;
import zen.core.observableprops.*;

/**
 * this enumeration contains a bunch of named fields, all of which are observable.
 * Just register a {@link ObservablePropertyListener} to the field in order to observe it.
 * All the updates are sent out on the main thread {@link AppData#handlerMainThread}.
 * The actual data/values bound to these enumeration names are backed by this
 * {@link ObservablePropertyManager#mapOfFieldValues}.
 */
public enum ObservableProperty implements ConstantsIF {
  ServiceIsStarted,

}//end enum ObservableProperty