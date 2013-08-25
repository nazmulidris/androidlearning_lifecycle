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
import android.os.*;
import android.view.*;
import android.widget.*;
import app.data.*;
import integration.*;
import sample.app.androidlifecycle.*;
import zen.base.*;
import zen.core.localevent.*;

import java.util.*;

/**
 * For more information on {@link ListView} and {@link ListAdapter} check out the following tutorials:
 * <ol>
 * <li><a href="http://goo.gl/LImzW">Simple array adapter</a></li>
 * <li><a href="http://goo.gl/lWlxZ">Custom view and adapter</a></li>
 * <li><a href="http://goo.gl/tdniQ">Lots of info on list views and adapters, randomly dumped in one place</a></li>
 * </ol>
 * Android {@link ListAdapter} is so simple! Nothing at all like the BlackBerry version/abomination.
 * This list adapter is intelligent and can be 'backed' by any array or list that you pass.
 * Just modify the underlying list or array and then call {@link ArrayAdapter#notifyDataSetChanged()}
 * and it will take care of syncing the list view with the adapter! This can be seen in this
 * class {@link MyArrayAdapter}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/23/13, 4:40 PM
 */
public class ListFromArrayFragment extends SimpleFragment implements ConstantsIF {

private MyArrayAdapter<String> listAdapter;
/** used to hold a bunch of debug messages from the service */
private ArrayList<String> listOfServiceMessages = new ArrayList<String>();

/** this must have the given signature to be created using reflection by {@link MyActivity.Fragments} */
public ListFromArrayFragment(MyActivity activity) {}

/**
 * make a fragment to house {@link R.layout#listarray_fragment}
 * <p/>
 * for more info on how to work with lists and adapters, <a href="http://goo.gl/KGkWB">click here</a>.
 */
@Override
public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

  super.onCreateView(inflater, container, savedInstanceState);

  View rootview = inflater.inflate(R.layout.listarray_fragment, container, false);

  // create the array ListAdapter (with row layout XML)
  listAdapter = new MyArrayAdapter<String>(getActivity(),
                                           android.R.layout.simple_list_item_1,
                                           listOfServiceMessages);

  // get the ListView
  ListView listView = (ListView) rootview.findViewById(R.id.listview_array);

  // deal with empty ListView display
  View emptyListView = rootview.findViewById(R.id.listview_array_empty);
  listView.setEmptyView(emptyListView);

  // bind the ListAdapter to the ListView
  listView.setAdapter(listAdapter);

  // attach a click listener to the ListView
  listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      PopupInfoDialogFragment newFragment = new PopupInfoDialogFragment(listOfServiceMessages.get(position));
      newFragment.show(getActivity().getSupportFragmentManager(), "info-popup-dialog");
    }
  });

  getLifecycleHelper().addResource(new LocalEventsListener() {
    public LocalEvents getLocalEvent() {
      return LocalEvents.Test;
    }

    public String getName() {
      return "Local Test Event listener";
    }

    public void onReceive(String stringPayload, Object objectPayload, Bundle extras) {
      // add the payload to the list
      Date object = (Date) objectPayload;
      listOfServiceMessages.add(object.toString());
      listAdapter.notifyDataSetChanged();
    }
  });

  return rootview;

}

/** extend this to provide custom views and bindings to the underlying data source */
public class MyArrayAdapter<T> extends ArrayAdapter<T> {

  public MyArrayAdapter(Context context, int textViewResourceId, List<T> objects) {
    super(context, textViewResourceId, objects);
  }

  /**
   * this implementation has been optimized for performance by using:
   * <ol>
   * <li>{@link ViewHolder} to remove the delay of doing view finding by id</li>
   * <li>using convertView in order to cache the row layout</li>
   * </ol>
   * For more info, <a href="http://goo.gl/5xjud">read this article</a>.
   */
  public View getView(int position, View convertView, ViewGroup parent) {

    if (convertView == null) {
      convertView = getActivity().getLayoutInflater().inflate(R.layout.list_row_layout, null);
      ViewHolder vh = new ViewHolder();
      vh.ttf_name = (TextView) convertView.findViewById(R.id.ttf_list_row_name);
      vh.ttf_position = (TextView) convertView.findViewById(R.id.ttf_list_row_position);
      convertView.setTag(vh);
    }

    ViewHolder vh = (ViewHolder) convertView.getTag();
    vh.ttf_position.setText(String.valueOf(position));
    vh.ttf_name.setText(listOfServiceMessages.get(position));

    return convertView;

  }

}// end class MyArrayAdapter

/** this is just a struct used to hold references to speed up the {@link MyArrayAdapter} implementation */
public class ViewHolder {

  public TextView ttf_position;
  public TextView ttf_name;
}// end class ViewHolder

}//end class ListFromArrayFragment
