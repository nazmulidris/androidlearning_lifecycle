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

import android.content.*;
import android.database.*;
import android.os.*;
import android.view.*;
import android.widget.*;
import app.data.*;
import integration.*;
import sample.app.androidlifecycle.*;
import zen.base.*;
import zen.core.db.*;
import zen.core.localevent.*;

import java.util.*;

/**
 * For more information on {@link SimpleCursorAdapter} check out the following resources:
 * <ol>
 * <li><a href="http://goo.gl/tdKhz">Code example of a custom cursor adapter</a></li>
 * </ol>
 * <p/>
 * There is one big difference between the {@link MyCursorAdapter} and the
 * {@link ListFromArrayFragment.MyArrayAdapter},
 * and that is with regards to calling {@link BaseAdapter#notifyDataSetChanged()}.
 * With the array, when you change it, you have to notify the adapter that some values
 * have changed. With the cursor, if you add or delete an item, then the cursor isn't
 * aware of this, since it's tied to the "old" view that it queried which
 * produced the old result set; so it's necessary to re-query, and get a new cursor
 * for this task:
 * <ol>
 * <li>{@link DB_blob#getAllCursor()}</li>
 * <li>{@link DB_kvp#getAllCursor()}</li>
 * </ol>
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/23/13, 4:39 PM
 */
public class ListFromCursorFragment extends SimpleFragment implements ConstantsIF {

private MyCursorAdapter listAdapter;

/** this must have the given signature to be created using reflection by {@link MyActivity.Fragments} */
public ListFromCursorFragment(MyActivity activity) {}


/** make a fragment to house {@link R.layout#listcursor_fragment} */
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
  super.onCreateView(inflater, container, savedInstanceState);

  View rootview = _createUI(inflater, container);

  // listen to LocalEvent and save the payload to DB_blob
  getLifecycleHelper().addResource(new LocalEventsListener() {
    public LocalEvents getLocalEvent() {
      return LocalEvents.Test;
    }

    public String getName() {
      return "add local event to db";
    }

    public void onReceive(String stringPayload, Object objectPayload, Bundle extras) {
      // add the payload to the list
      Date object = (Date) objectPayload;
      getAppData().dbManager.getDB(DB_blob_enum.Test_BLOB_DB).add(object.toString());
    }
  });

  // respond to DB_blob change events and update the list
  getLifecycleHelper().addResource(new LocalEventsListener() {
    public LocalEvents getLocalEvent() {
      return LocalEvents.DB_blob_Change;
    }

    public String getName() {
      return "respond to changes in db, and update list";
    }

    public void onReceive(String stringPayload, Object objectPayload, Bundle extras) {
      if (stringPayload.equals(getAppData().dbManager.getDB(DB_blob_enum.Test_BLOB_DB).getDbName())) {
        listAdapter.changeCursor(getAppData().dbManager.getDB(DB_blob_enum.Test_BLOB_DB).getAllCursor());
      }
    }
  });

  return rootview;
}

/** assemble the UI */
private View _createUI(LayoutInflater inflater, ViewGroup container) {
  View rootview = inflater.inflate(R.layout.listcursor_fragment, container, false);

  // create the cursor ListAdapter
  listAdapter = new MyCursorAdapter(getActivity(),
                                    getAppData().dbManager.getDB(DB_blob_enum.Test_BLOB_DB).getAllCursor());

  // get the ListView
  ListView listView = (ListView) rootview.findViewById(R.id.listview_cursor);

  // deal with empty ListView display
  View emptyListView = rootview.findViewById(R.id.listview_cursor_empty);
  listView.setEmptyView(emptyListView);

  // bind the ListAdapter to the ListView
  listView.setAdapter(listAdapter);

  // attach a click listener to the ListView
  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      Cursor cursor = listAdapter.getCursor();
      String data = cursor.getString(
          cursor.getColumnIndex(DB_blob.Schema.COL_DATA)
      );
      PopupInfoDialogFragment newFragment = new PopupInfoDialogFragment(data);
      newFragment.show(getActivity().getSupportFragmentManager(),
                       PopupInfoDialogFragment.class.getSimpleName());
    }
  });
  return rootview;
}

/** list adapter for the db cursor */
public class MyCursorAdapter extends CursorAdapter {

  public MyCursorAdapter(Context context, Cursor c) {
    super(context, c, -1);
  }

  /**
   * create a brand new view, and cache the {@link ViewHolder} so that findViewById lookups don't need to be
   * performed anymore (saving time).
   * For more info, <a href="http://goo.gl/5xjud">read this article</a>.
   */
  @Override
  public View newView(Context context, Cursor cursor, ViewGroup parent) {
    View convertView = getActivity().getLayoutInflater().inflate(R.layout.list_row_layout, null);
    ViewHolder vh = new ViewHolder();
    vh.ttf_name = (TextView) convertView.findViewById(R.id.ttf_list_row_name);
    vh.ttf_position = (TextView) convertView.findViewById(R.id.ttf_list_row_position);
    convertView.setTag(vh);
    return convertView;
  }

  /**
   * actually use the given already created view to actually put the data into.
   * For more info, <a href="http://goo.gl/5xjud">read this article</a>.
   */
  @Override
  public void bindView(View convertView, Context context, Cursor cursor) {
    ViewHolder vh = (ViewHolder) convertView.getTag();
    vh.ttf_position
      .setText(
          String.valueOf(
              cursor.getLong(
                  cursor.getColumnIndex(DB_blob.Schema.COL_ID)
              )
          )
      );
    vh.ttf_name
      .setText(
          cursor.getString(
              cursor.getColumnIndex(DB_blob.Schema.COL_DATA)
          )
      );
  }

  /** this is just a struct used to hold references to speed up the {@link MyCursorAdapter} implementation */
  public class ViewHolder {

    public TextView ttf_position;
    public TextView ttf_name;
  }// end class ViewHolder

}

}//end class ListFromCursorFragment

