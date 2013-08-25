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

import android.app.*;
import zen.core.*;
import zen.utlis.*;

/**
 * This is a base class that should be extended whenever a {@link Service} is needed.
 * It takes care of a lot of things that are done over and over again, if you subclass
 * {@link Service}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 3/1/13, 5:54 PM
 */
public abstract class SimpleService extends Service implements SimpleContextIF {

protected LifecycleHelper lifecycleHelper;

@Override
public LifecycleHelper getLifecycleHelper() {
  return lifecycleHelper;
}

@Override
public AppData getAppData() {
  return (AppData) getApplication();
}

@Override
public void showToastShort(String msg) {
  AndroidUtils.showToastShort(this, msg);
}

@Override
public void showToastLong(String msg) {
  AndroidUtils.showToastLong(this, msg);
}

@Override
public void showToastShort(int rid) {
  AndroidUtils.showToastShort(this, rid);
}

@Override
public void showToastLong(int rid) {
  AndroidUtils.showToastLong(this, rid);
}

/** this is the constructor */
@Override
public void onCreate() {
  super.onCreate();
  lifecycleHelper = new LifecycleHelper(this.getClass().getSimpleName(), getAppData());
  lifecycleHelper.onCreate();
}

@Override
public void onDestroy() {
  lifecycleHelper.onDestroy();
  super.onDestroy();
}

}//end class SimpleService
