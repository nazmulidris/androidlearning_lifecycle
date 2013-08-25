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

import android.app.*;
import android.content.*;
import android.os.*;
import android.support.v4.app.DialogFragment;
import android.view.*;
import android.widget.*;
import sample.app.androidlifecycle.*;

/**
 * Provides dialog implementation for the popup when a list item gets clicked.
 * <p/>
 * for more info on building a dialog fragment, <a href="http://goo.gl/6zugL">click here</a>.
 */
public class PopupInfoDialogFragment extends DialogFragment {

private final String item;

public PopupInfoDialogFragment(String item) {
  this.item = item;
}

/** pull in the fragment layout {@link R.id#ttf_dialog_info} and create the dialog */
@Override
public Dialog onCreateDialog(Bundle savedInstanceState) {
  AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

  View rootView = getActivity().getLayoutInflater().inflate(R.layout.dialog_fragment, null);

  // set the info text field value
  TextView ttf_info = (TextView) rootView.findViewById(R.id.ttf_dialog_info);
  ttf_info.setText(item);

  builder.setView(rootView)
         .setPositiveButton(android.R.string.ok,
                            new DialogInterface.OnClickListener() {
                              @Override
                              public void onClick(DialogInterface dialog, int id) {
                                getDialog().cancel();
                              }
                            });
  builder.setCancelable(false);
  return builder.create();
}
}//end class PopupInfoDialogFragment
