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
import android.app.FragmentTransaction;
import android.os.*;
import android.support.v4.app.Fragment;
import android.support.v4.app.*;
import android.support.v4.view.*;
import android.view.*;
import app.data.*;
import app.service.*;
import sample.app.androidlifecycle.*;
import zen.base.*;
import zen.core.*;
import zen.utlis.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * This is the main activity for this app. It does a bunch of stuff:
 * <ul>
 * <li>Sets up the tabs in the UI</li>
 * <li>It launches a long running background service ({@link MyService}) in {@link #onCreate},
 * if this service isn't running already</li>
 * <li>Schedules a recurring alarm that invokes an intent service {@link MyIntentService} in
 * {@link #onCreate}</li>
 * <li>Registers an onStopFunctor {@link Runnable} with {@link LifecycleHelper} that
 * cancels the recurring alarm to spawn the intent service {@link MyIntentService} when the
 * activity is destroyed</li>
 * <li>It spawns a new scheduled executor, and adds it as a resource to {@link LifecycleHelper}
 * that just dumps a message to the log every second; this will be automagically destroyed
 * when this activity is destroyed</li>
 * </ul>
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 11/21/12, 3:49 PM
 */
public class MyActivity extends SimpleFragmentActivity implements ConstantsIF {

/** Called when the activity is first created. */
@Override
public void onCreate(Bundle savedInstanceState) {
  super.onCreate(savedInstanceState);

  setContentView(R.layout.myactivity);

  _setupFragments();

  _startMyService();

  _scheduleRepeatingTask();

  // cancel alarm to start recurring intent service
  getLifecycleHelper().addOnStopFunctor(new Runnable() {
    public void run() {
      MyIntentService.scheduleRecurringAlarm(MyActivity.this, false);
    }
  });

  // set alarm to start recurring intent service
  MyIntentService.scheduleRecurringAlarm(MyActivity.this, true);

  AndroidUtils.log(IconPaths.MyApp, "MyActivity: created");
}

/**
 * this enum contains a list of all the {@link Fragment} classes
 * that have to be created in the {@link FragmentPagerAdapter}
 */
public enum Fragments {
  ServiceControl("Service Test", ServiceControlFragment.class),
  ListFromCursor("List cursor", ListFromCursorFragment.class),
  ListFromArray("List array", ListFromArrayFragment.class);
  final String                    title;
  final Class<? extends Fragment> aClass;

  Fragments(String title, Class<? extends Fragment> aClass) {
    this.title = title;
    this.aClass = aClass;
  }
}

/**
 * For more info on using view pager and tabs and action bar, <a href="http://goo.gl/7CsM9">checkout this article</a>
 * in the Android developer site's "Training" section.
 * <p/>
 * For more info on ViewPager, <a href="http://goo.gl/NgCUO">read this article</a>.
 */
private void _setupFragments() {
  // set up the action bar
  final ActionBar actionBar = getActionBar();
  actionBar.setHomeButtonEnabled(false);
  actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

  // create the FragmentPagerAdapter
  FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
    @Override
    public Fragment getItem(int position) {
      try {
        return Fragments.values()[position].aClass.getConstructor(MyActivity.class).newInstance(MyActivity.this);
      }
      catch (Exception e) {
        AndroidUtils.logErr(IconPaths.MyApp, "MyActivity - problem creating fragments", e);
        // something has gone wrong - this shouldn't happen
        return new Fragment() {
          /** make a fragment to house {@link R.layout#error_fragment} */
          public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return super.onCreateView(inflater, container, savedInstanceState);
          }
        };
      }
    }

    @Override
    public int getCount() {
      return Fragments.values().length;
    }

    public CharSequence getPageTitle(int position) {
      try {
        return Fragments.values()[position].title;
      }
      catch (Exception e) {
        return "N/A";
      }
    }
  };

  // Set up the ViewPager, attaching the adapter and setting up a listener for when the
  // user swipes between sections.
  final ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
  viewPager.setOffscreenPageLimit(Fragments.values().length); // keep all the pages in memory
  viewPager.setAdapter(fragmentPagerAdapter);
  viewPager.setOnPageChangeListener(
      new ViewPager.SimpleOnPageChangeListener() {
        /**
         * When swiping between different app sections, select the corresponding tab.
         * We can also use ActionBar.Tab#select() to do this if we have a reference to the Tab.
         */
        @Override
        public void onPageSelected(int position) {
          super.onPageSelected(position);
          actionBar.setSelectedNavigationItem(position);
        }
      }
  );
  // create TabListener
  ActionBar.TabListener tabListener = new

      ActionBar.TabListener() {
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction transaction) {
          viewPager.setCurrentItem(tab.getPosition());
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction transaction) {}

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction transaction) {}
      };

  // setup tabs
  for (int i = 0; i < fragmentPagerAdapter.getCount(); i++) {
    actionBar.addTab(actionBar.newTab()
                              .setText(fragmentPagerAdapter.getPageTitle(i))
                              .setTabListener(tabListener)
    );
  }

}

/** create a repeating executor that displays debug messages after interacting with service */
private void _scheduleRepeatingTask() {
  ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
  executor.scheduleAtFixedRate(
      new Runnable() {
        public void run() {
          try {
            StringBuilder sb = new StringBuilder();
            sb.append("ScheduledExecutorRunning - ").append(new Date().toString());
            AndroidUtils.log(IconPaths.MyApp, sb.toString());
          }
          catch (Exception e) {}
        }
      },
      0, MyActivityRepeatDelay_ms, TimeUnit.MILLISECONDS
  );
  lifecycleHelper.addResource(executor);
}

/** start a service and connect to it via the stub */
private void _startMyService() {
  // must start the service for this type of binding
  MyService.startServiceNow(this);
}

}// end class MyActivity