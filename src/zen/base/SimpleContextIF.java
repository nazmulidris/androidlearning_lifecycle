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

package zen.base;

import zen.core.*;

/**
 * Interface that makes it easy to tie all these different types of base classes to
 * different parts of the framework.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 2/23/13, 4:11 PM
 */
public interface SimpleContextIF {

public LifecycleHelper getLifecycleHelper();

public AppData getAppData();

public void showToastShort(String msg);

public void showToastLong(String msg);

public void showToastShort(int rid);

public void showToastLong(int rid);

}//end interface SimpleContextIF
