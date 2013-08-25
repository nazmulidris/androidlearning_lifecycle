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

package zen.core.observableprops;

import android.os.*;
import app.data.*;
import integration.*;
import zen.core.*;
import zen.utlis.*;

import java.util.*;

import static zen.utlis.IconPaths.*;

/**
 * This class works hand in hand with {@link ObservableProperty} enum, which contains all the
 * properties for the app.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 3/11/13, 5:27 PM
 */
public class ObservablePropertyManager implements ConstantsIF {

/** this map actually holds the key value pairs, where the keys are the enum {@link ObservableProperty} */
private HashMap<ObservableProperty, Object>                                mapOfFieldValues    =
    new HashMap<ObservableProperty, Object>();
/** this map actually holds the list of listeners for each key. the key is is the enum {@link ObservableProperty} */
private HashMap<ObservableProperty, ArrayList<ObservablePropertyListener>> mapOfFieldObservers =
    new HashMap<ObservableProperty, ArrayList<ObservablePropertyListener>>();
private Handler handlerMainThread;

public ObservablePropertyManager(Handler handler) {
  handlerMainThread = handler;
}

/**
 * this adds the {@link ObservablePropertyListener} to the property, and it also fires the
 * {@link ObservablePropertyListener#onChange(String, Object)} method, if the
 * {@link #getValue(ObservableProperty, Object)} or defaultValue is NOT null.
 * <p/>
 * Notes:
 * <ol>
 * <li>instead of calling this method, consider simply adding the observer as a resource
 * to {@link LifecycleHelper#addResource(Object...)} which will not only call this method
 * but also will release the listener automagically.</li>
 * <li>there is a good reason to call this method, if you want the observer to be run
 * the first time that it's added (by making the defaultValue NOT null). There are a few
 * classes that need this complex capability.</li>
 * </ol>
 *
 * @param observer     add this observer to the list of {@link ObservablePropertyListener} for the
 *                     field
 * @param defaultValue this is the object that should be passed to observer, if there's no value set
 *                     for it yet. note that this can be null, if that's what a caller passes in here.
 *                     if this value is NOT null, then any registered observers WILL be fired.
 */
public ObservablePropertyListener addPropertyChangeObserver(ObservablePropertyListener observer,
                                                            Object defaultValue)
{

  // observer can't be null
  if (observer == null) { return observer; }

  try {
    // get the property in question from the observer
    ObservableProperty property = observer.getProperty();

    // get the list if it exists, or create a new one & save it
    ArrayList<ObservablePropertyListener> observersForProperty;
    if (mapOfFieldObservers.containsKey(property)) {
      observersForProperty = mapOfFieldObservers.get(property);
    }
    else {
      observersForProperty = new ArrayList<ObservablePropertyListener>();
      mapOfFieldObservers.put(property, observersForProperty);
    }

    // check for dupes!
    if (observersForProperty.contains(observer)) { return observer; }

    // add the observer
    observersForProperty.add(observer);
    AndroidUtils.log(MyApp,
                     String.format("ObservableProperty.[%s] property change observer [%s] added",
                                   property.name(),
                                   observer.getName()));

    // fire the onChange method on this observer (if the value isn't null!)
    Object value = getValue(property, defaultValue);
    if (value != null) {
      try {
        observer.onChange(property.name(), value);
      }
      catch (Exception e) {
        AndroidUtils.logErr(
            MyApp,
            String.format("ObservableProperty.[%s] problem running property change observer [%s]",
                          property.name(),
                          observer.getName()),
            e);
      }
      AndroidUtils.log(MyApp,
                       String.format("ObservableProperty.[%s] property change observer [%s] fired",
                                     property.name(),
                                     observer.getName())
      );
    }

    return observer;
  }
  catch (Exception e) {
    AndroidUtils.logErr(MyApp,
                        String.format("ObservableProperty.[%s] problem adding listener [%s]",
                                      observer.getProperty().name(),
                                      observer.getName())
    );
    return observer;
  }

}

/** given a observer, simply remove it from the {@link #mapOfFieldObservers} */
public void removePropertyChangeObserver(ObservablePropertyListener observer) {
  if (observer == null) { return; }
  try {
    ArrayList<ObservablePropertyListener> listOfObservers = mapOfFieldObservers.get(observer.getProperty());
    if (listOfObservers != null) { listOfObservers.remove(observer); }
  }
  catch (Exception e) {
    AndroidUtils.logErr(MyApp,
                        String.format("ObservableProperty.[%s] problem removing listener [%s]",
                                      observer.getProperty().name(),
                                      observer.getName())
    );
  }
}

/**
 * @return if the {@link #mapOfFieldValues} doesn't contain a value for this field,
 *         just return the defaultValue
 */
public Object getValue(ObservableProperty prop, Object defaultValue) {
  try {
    return mapOfFieldValues.get(prop) == null ? defaultValue : mapOfFieldValues.get(prop);
  }
  catch (Exception e) {
    return null;
  }
}

/** just a refinement of {@link #getValue(ObservableProperty, Object)} that casts the object as boolean */
public boolean getValueAsBoolean(ObservableProperty prop) {
  try {
    return (Boolean) getValue(prop, Boolean.FALSE);
  }
  catch (Exception e) {
    return false;
  }
}

/**
 * @param defaultValue if this property is not set yet, then assume this defaultValue to be it's current value
 *
 * @return true if the property contains a value and it's equal to the given value
 */

public boolean valueEquals(ObservableProperty prop, Object compareToValue, Object defaultValue) {
  try {
    return getValue(prop, defaultValue).equals(compareToValue);
  }
  catch (Exception e) {return false;}
}

/**
 * saves the value to {@link #mapOfFieldValues},
 * then fires the {@link ObservablePropertyListener#onChange} method
 * (which is run on the main thread via the {@link AppData#handlerMainThread}).
 * <p/>
 * Note that the onChange method is only fired if:
 * <ol>
 * <li>if the value is set for the first time (was null before)</li>
 * <li>if the value is different than what's been set already</li>
 * </ol>
 *
 * @param property this can't be null! will throw IllegalArgumentException if it is.
 * @param value    this can't be null! will throw IllegalArgumentException if it is.
 */
public void setValue(final ObservableProperty property, final Object value) throws IllegalArgumentException {

  // property can't be null
  if (property == null) {
    throw new IllegalArgumentException("property can't be null!");
  }

  // value can't be null
  if (value == null) {
    throw new IllegalArgumentException(
        String.format("value for property [%s] can't be null!",
                      property.name()));
  }

  try {
    // check to see if the value already exists and is the same
    if (mapOfFieldValues.containsKey(property)) {
      if (mapOfFieldValues.get(property).equals(value)) { return; }
    }

    // value does not exist, or is not the same, so save it, and send out update event
    mapOfFieldValues.put(property, value);

    if (mapOfFieldObservers.containsKey(property)) {
      ArrayList<ObservablePropertyListener> listOfObservers = mapOfFieldObservers.get(property);
      for (final ObservablePropertyListener observer : listOfObservers) {
        handlerMainThread.post(new Runnable() {
          public void run() {
            try {
              observer.onChange(property.name(), value);
            }
            catch (Exception e) {
              AndroidUtils.logErr(
                  MyApp,
                  String.format("ObservableProperty.[%s] problem running property change observer [%s]",
                                property.name(),
                                observer.getName()),
                  e);
            }
            AndroidUtils.log(MyApp,
                             String.format("ObservableProperty.[%s] property changed to [%s], and listener [%s] fired",
                                           property.name(),
                                           value.toString(),
                                           observer.getName())
            );
          }
        });
      }
    }
    else {
      AndroidUtils.log(MyApp,
                       String.format("ObservableProperty.[%s] property changed to [%s], no listeners fired",
                                     property.name(),
                                     value.toString())
      );

    }
  }
  catch (Exception e) {
    AndroidUtils.logErr(MyApp,
                        String.format("ObservableProperty.[%s] problem setting value [%s]",
                                      property.name(),
                                      value.toString())
    );
  }

}

}//end class ObservablePropertyManager
