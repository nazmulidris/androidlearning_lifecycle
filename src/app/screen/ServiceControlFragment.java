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

package app.screen;

import android.os.*;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.view.*;
import android.widget.*;
import app.data.*;
import app.service.*;
import integration.*;
import sample.app.androidlifecycle.*;
import zen.base.*;
import zen.core.localevent.*;
import zen.core.observableprops.*;
import zen.utlis.*;

/**
 * This is a simple fragment that displays the service control panel (and db test button).
 * <p/>
 * <a href="http://goo.gl/3YveU">Here's more info on the lifecycle</a> of {@link Fragment}.
 * Here's a description of the buttons:
 * <ul>
 * <li><code>Start Service</code> - this starts the service, if it's not already started (and it uses
 * {@link ObservableProperty#ServiceIsStarted} to ensure that state of the button reflects
 * whether the service is started or not. Eg: if the service is started, then this button
 * is disabled, and vice versa.</li>
 * <li><code>Stop Service</code> - this stops the service, and uses the same
 * {@link ObservableProperty#ServiceIsStarted} to ensure that the state of the button reflects
 * whether the service is started on not. Eg: if the service is started, then this button is
 * enabled, and vice versa.</li>
 * <li><code>Test DBs</code> - this actually clears out all the declared databases in
 * {@link DB_blob_enum} and {@link DB_kvp_enum}.</li>
 * </ul>
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/22/13, 7:29 PM
 */
public class ServiceControlFragment extends SimpleFragment implements ConstantsIF {

private MyActivity activity;
private ViewHolder myViews;

/** struct to hold all the UI objects needed by this fragment */
public class ViewHolder {

  private TextView lbl_status;
  private Button   btn_startservice;
  private Button   btn_stopservice;
  private Button   btn_testdb;

}

/** this must have the given signature to be created using reflection by {@link MyActivity.Fragments} */
public ServiceControlFragment(MyActivity activity) {
  this.activity = activity;
}

/**
 * actually creates the view, opposite of {@link #onDestroyView()}.
 * this houses the layout {@link R.layout#servicecontrol_fragment}
 */
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
  super.onCreateView(inflater, container, savedInstanceState);
  try {
    View rootView = inflater.inflate(R.layout.servicecontrol_fragment, container, false);
    myViews = new ViewHolder();
    _bindToUIElements(rootView);
    _wireStartStopButtons();
    _wireDbButton();
    _loadDataIntoUI();
    return rootView;
  }
  finally {
    AndroidUtils.log(IconPaths.MyApp, "ServiceControlFragment - view is created");
  }
}

/**
 * called when view is destroyed, opposite of {@link #onCreateView}. this happens if the
 * user swipes a few tabs over the tab containing this fragment. the {@link ViewPager}
 * does all of this.
 */
@Override
public void onDestroyView() {
  super.onDestroyView();
  _saveDataFromUI();
  AndroidUtils.log(IconPaths.MyApp, "ServiceControlFragment - view is destroyed");
}

/**
 * called when activity goes off screen; the UI state data is saved here just in case the
 * main activity gets killed while the app is off screen. in this case, when the fragment is
 * created via {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} this UI state data
 * will be restored. the other case in which UI data is saved is in {@link #onDestroyView()}
 */
public void onSaveInstanceState(Bundle outState) {
  super.onSaveInstanceState(outState);
  _saveDataFromUI();
  AndroidUtils.log(IconPaths.MyApp, "ServiceControlFragment - activity going off screen");
}

/** opposite of {@link #_saveDataFromUI} */
private void _loadDataIntoUI() {
  String text = SavedDataKeys.lbl_status.get(activity, null);
  if (text != null) {
    myViews.lbl_status.setText(text);
    AndroidUtils.log(IconPaths.MyApp, String.format("ServiceControlFragment - loaded data into UI: '%s'", text));
  }
}

/** opposite of {@link #_loadDataIntoUI()} */
private void _saveDataFromUI() {
  String text = myViews.lbl_status.getText().toString();
  SavedDataKeys.lbl_status.put(activity, text);
  AndroidUtils.log(IconPaths.MyApp, String.format("ServiceControlFragment - saved data from UI: '%s'", text));
}

/** set references to the all the UI elements in this fragment */
private void _bindToUIElements(View rootView) {
  myViews.lbl_status = (TextView) rootView.findViewById(R.id.lbl_status);
  myViews.btn_startservice = (Button) rootView.findViewById(R.id.btn_startservice);
  myViews.btn_stopservice = (Button) rootView.findViewById(R.id.btn_stopservice);
  myViews.btn_testdb = (Button) rootView.findViewById(R.id.btn_testdb);
}

/** wire the buttons to start/stop the service */
private void _wireStartStopButtons() {
  myViews.btn_startservice.setOnClickListener(
      new View.OnClickListener() {
        public void onClick(View view) {
          MyService.startServiceNow(getActivity());
        }
      });

  myViews.btn_stopservice.setOnClickListener(new View.OnClickListener() {
    public void onClick(View view) {
      LocalEventsManager.fireEvent(getActivity(), LocalEvents.ShutdownMyService, null, null);
    }
  });

  getAppData().observablePropertyManager.addPropertyChangeObserver(
      new ObservablePropertyListener() {
        public ObservableProperty getProperty() {
          return ObservableProperty.ServiceIsStarted;
        }

        public String getName() {return "MyActivity service start/stop buttons";}

        public void onChange(String propertyName, Object value) {
          if (value == Boolean.TRUE) {
            myViews.btn_startservice.setEnabled(false);
            myViews.btn_stopservice.setEnabled(true);
          }
          else {
            myViews.btn_startservice.setEnabled(true);
            myViews.btn_stopservice.setEnabled(false);
          }
        }
      },
      true
  );

}

/** wire the button to the db */
private void _wireDbButton() {
  // onClick behavior for button
  myViews.btn_testdb.setOnClickListener(new View.OnClickListener() {
    public void onClick(View view) {
      new AsyncTask<Void, Void, Void>() {
        protected Void doInBackground(Void... voids) {

          try {
            getAppData().dbManager.test();
          }
          catch (Exception e) {
            AndroidUtils.logErr(IconPaths.MyApp,
                                String.format("ServiceControlFragment - problem running db test, error is: %s",
                                              e.toString()));
          }
          return null;
        }

        protected void onPreExecute() {
          myViews.btn_testdb.setEnabled(false);
        }

        protected void onPostExecute(Void aVoid) {
          myViews.btn_testdb.setEnabled(true);
          Toast.makeText(activity, "DB Test Completed", Toast.LENGTH_SHORT).show();
        }
      }.execute();
    }
  });
}

}// end class ServiceControlFragment
