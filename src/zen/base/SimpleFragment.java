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
import android.os.*;
import android.support.v4.app.Fragment;
import android.view.*;
import zen.core.*;
import zen.utlis.*;

/**
 * This is a base class that should be extended whenever a {@link Fragment} is needed.
 * It takes care of a lot of things that are done over and over again, if you
 * subclass {@link Fragment}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 2/23/13, 4:19 PM
 */
public abstract class SimpleFragment extends Fragment implements SimpleContextIF {

protected LifecycleHelper lifecycleHelper;

/** get a reference to the {@link Application} subclass for this project */
@Override
public AppData getAppData() {
  return (AppData) getActivity().getApplication();
}

@Override
public LifecycleHelper getLifecycleHelper() {
  return lifecycleHelper;
}

/** this is the constructor */
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
  super.onCreateView(inflater, container, savedInstanceState);
  lifecycleHelper = new LifecycleHelper(this.getClass().getSimpleName(), getAppData());
  lifecycleHelper.onCreate();
  return null;
}

@Override
public void onDestroyView() {
  lifecycleHelper.onDestroy();
  super.onDestroyView();
}

@Override
public void showToastShort(String msg) {
  AndroidUtils.showToastShort(getActivity(), msg);
}

@Override
public void showToastLong(String msg) {
  AndroidUtils.showToastLong(getActivity(), msg);
}

@Override
public void showToastShort(int rid) {
  AndroidUtils.showToastShort(getActivity(), rid);
}

@Override
public void showToastLong(int rid) {
  AndroidUtils.showToastLong(getActivity(), rid);
}

}//end class SimpleFragment