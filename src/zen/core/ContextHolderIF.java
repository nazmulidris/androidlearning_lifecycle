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
import android.os.*;

/**
 * Simple interface that encapsulates the raw functionality of a simple activity, and can
 * easily wrap any {@link Activity}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since Aug 19, 2010, 1:46:40 PM
 */
public interface ContextHolderIF {

/** this is a ref to a handler that can be used to post in the EDT later */
public Handler getMyHandler();

/** this is a ref to the context of the underlying Activity */
public Context getMyContext();

}//end interface ContextHolderIF
