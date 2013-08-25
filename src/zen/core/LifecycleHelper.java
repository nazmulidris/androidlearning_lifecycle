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

import android.content.*;
import app.data.*;
import zen.base.*;
import zen.core.localevent.*;
import zen.core.observableprops.*;
import zen.utlis.*;

import java.util.*;
import java.util.concurrent.*;

/**
 * This class just makes it easy to do all the bookkeeping related to allocating and deallocating
 * resources for services, activities, and fragments. You don't have to use this class, but it just
 * makes things easy to organize and account for, in a single place, without having to forget.
 * <p/>
 * This class doesn't solve everything. You can explicitly allocate things like
 * {@link ServiceConnection} but you have to deallocate this explicitly in the activity
 * that creates it (otherwise Android OS complains about leaking the connection), can't
 * really deallocate them.
 * <p/>
 * Originally, this class was made for use standalone. But they I integrated in into the framework
 * base classes ({@link SimpleContextIF} implementations) and it doesn't make sense to use this
 * standalone anymore, although you can if you want.
 * <p/>
 * So to use this class, do the following:
 * <ol>
 * <li>Create an instance of the class, and supply all the params</li>
 * <li>make sure to call {@link #onCreate()} in your onCreate, or onCreateView, or onStartCommand method</li>
 * <li>make sure to call {@link #onDestroy()} in your onDestroy, or onDestroyView method</li>
 * </ol>
 * <p/>
 * Tell the LifecycleHelper what objects you want it to manage. and tell it how to manage them (via
 * the functors).
 * <p/>
 * Binding of resources (that can be automagically bound) happens when {@link #onCreate()}
 * is called (and this applies to all the resources that have been added when this method is called).
 * <p/>
 * Unbinding of resources (that can be automagically unbound) happens when {@link #onDestroy()}
 * is called (and this applies to all the resources that have been added when this method is called).
 * <p/>
 * For any binding/unbinding that can't be handled automagically, please pass your own {@link #onStopFunctor}
 * using {@link #addOnStopFunctor(Runnable)}.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 1/28/13, 10:40 AM
 */
public class LifecycleHelper implements ConstantsIF {

private Runnable          onStopFunctor;
private ArrayList<Object> resourceList;
private String            name;
private AppData           ctx;

/** Name this object and provide the context. */
public LifecycleHelper(String name, AppData ctx)
    throws IllegalArgumentException
{

  if (ctx == null) {
    String msg = String.format("LifecycleHelper [%s] - problem in constructor, since context is null!",
                               name
    );
    AndroidUtils.logErr(IconPaths.Resource, msg);
    throw new IllegalArgumentException(msg);
  }
  this.ctx = ctx;

  if (name == null) { this.name = "N/A"; }
  else { this.name = name; }

  resourceList = new ArrayList<Object>();

}

/** the {@link #onStopFunctor} runs when {@link #onDestroy()} runs */
public void addOnStopFunctor(Runnable onStopFunctor) {
  this.onStopFunctor = onStopFunctor;
}

/**
 * add the given object(s) as a resource to {@link #resourceList}. the following will happen:
 * <ol>
 * <li>the resource will attempt to be bound via {@link #_bindResource(Object)}</li>
 * <li>which this class will attempt to automagically release when {@link #_releaseResource(Object)} gets called.</li>
 * </ol>
 *
 * @param resourceRay if this is null, an it will log an error, but will continue working.
 *                    this can be of type:
 *                    <ol>
 *                    <li>{@link ExecutorService} (can unbind)</li>
 *                    <li>{@link LocalEventsListener} (can bind, and unbind)</li>
 *                    <li>{@link ObservablePropertyListener} (can bind, unbind).</li>
 *                    <li>{@link Shutdownable} (can unbind).</li>
 *                    </ol>
 *                    passing null resources will result in a warning being written to the system log, please
 *                    refrain from doing this.
 */
public void addResource(Object... resourceRay) {
  if (resourceRay != null && resourceRay.length > 0) {
    for (int i = 0, length = resourceRay.length; i < length; i++) {
      Object resource = resourceRay[i];
      if (resource == null) {
        AndroidUtils.logErr(IconPaths.Resource, String.format(
            "LifecycleHelper [%s] - warning attempting to add a resource[#%s] param, but it's null or empty!",
            name,
            i)
        );
      }
      else {_bindResource(resource);}
    }
  }
  else {
    throw new IllegalArgumentException(
        String.format("LifecycleHelper [%s] - problem adding a resource that's null or empty",
                      name)
    );
  }
}

/**
 * simply remove the given object as a resource; this does not affect {@link #_releaseResource(Object)} and this
 * class will not attempt to unbind this object at this time.
 *
 * @param res if this is null, it won't be removed
 */
public void removeResource(Object res) {
  if (res != null) {
    try {
      resourceList.remove(res);
    }
    catch (Exception e) {
      AndroidUtils.logErr(IconPaths.Resource,
                          String.format("LifecycleHelper [%s] - problem removing resource, type=[%s], error=[%s]",
                                        name,
                                        res.getClass().getSimpleName(),
                                        e.toString())
      );
    }
  }
}

public Context getContext() {return ctx;}

public void onCreate() {}

/**
 * make sure to call this in your onDestroy, or onDestroyView methods; this will attempt to release any
 * resources that {@link LifecycleHelper} knows how to release (that are in the {@link #resourceList}
 * when this method is called).
 */
public void onDestroy() {

  try {
    if (onStopFunctor != null) {
      onStopFunctor.run();
      AndroidUtils.log(IconPaths.Resource,
                       String.format("LifecycleHelper [%s] - ran onStopFunctor", name)
      );
    }
  }
  catch (Exception e) {
    AndroidUtils.logErr(IconPaths.Resource,
                        String.format("LifecycleHelper [%s] - problem running onStopFunctor - %s",
                                      name,
                                      e.toString())
    );
  }

  if (!resourceList.isEmpty()) {
    for (int i = 0, length = resourceList.size(); i < length; i++) {
      Object resource = resourceList.get(i);

      if (resource == null) {
        AndroidUtils.logErr(IconPaths.Resource,
                            String.format(
                                "LifecycleHelper [%s] - warning attempting to release resource[#%d], but it's null!",
                                name,
                                i)
        );
      }
      else {
        _releaseResource(resource);
      }

    }
  }

}

/** try and automagically release the resources that this can */
private void _releaseResource(Object resource) {

  String resname = "N/A";

  try {
    // Shutdownable
    if (resource instanceof Shutdownable) {
      ((Shutdownable) resource).shutdown();
      AndroidUtils.log(IconPaths.Resource,
                       String.format("LifecycleHelper [%s] - shut down Shutdownable [%s]",
                                     name,
                                     resource.getClass().getSimpleName())
      );
    }
    // ExecutorService
    else if (resource instanceof ExecutorService) {
      ((ExecutorService) resource).shutdown();
      AndroidUtils.log(IconPaths.Resource,
                       String.format("LifecycleHelper [%s] - shut down ExecutorService", name)
      );
    }
    // LocalEventsListener
    else if (resource instanceof LocalEventsListener) {
      LocalEventsListener localBroadcastReceiver = (LocalEventsListener) resource;
      resname = localBroadcastReceiver.getName();
      localBroadcastReceiver.unregister(ctx);
      AndroidUtils.log(IconPaths.Resource,
                       String.format("LifecycleHelper [%s] - unregistered LocalEventsListener [%s] for event [%s]",
                                     name,
                                     localBroadcastReceiver.getName(),
                                     localBroadcastReceiver.getLocalEvent()
                       )
      );
    }
    // ObservablePropertyListener
    else if (resource instanceof ObservablePropertyListener) {
      ObservablePropertyListener observer = (ObservablePropertyListener) resource;
      resname = observer.getName();
      ctx.observablePropertyManager.removePropertyChangeObserver(observer);
      AndroidUtils.log(IconPaths.Resource,
                       String.format(
                           "LifecycleHelper [%s] - unregistered ObservablePropertyListener [%s] for property " +
                           "[%s]",
                           name,
                           observer.getName(),
                           observer.getProperty()
                       )
      );
    }
  }
  catch (Exception e) {
    AndroidUtils.logErr(IconPaths.Resource,
                        String.format("LifecycleHelper [%s] - problem releasing a resource [%s] - %s",
                                      name,
                                      resname,
                                      Arrays.toString(e.getStackTrace()))
    );
  }


}

/** try and automagically bind the resources that this can */
private void _bindResource(Object resource) {
  try {
    // LocalEventsListener
    if (resource instanceof LocalEventsListener) {
      LocalEventsListener localBroadcastReceiver = (LocalEventsListener) resource;
      localBroadcastReceiver.register(ctx);
      AndroidUtils.log(IconPaths.Resource,
                       String.format("LifecycleHelper [%s] - registered LocalEventsListener [%s] for event [%s]",
                                     name,
                                     localBroadcastReceiver.getName(),
                                     localBroadcastReceiver.getLocalEvent()
                       )
      );
    }
    // ObservablePropertyListener
    else if (resource instanceof ObservablePropertyListener) {
      ObservablePropertyListener observer = (ObservablePropertyListener) resource;
      ctx.observablePropertyManager.addPropertyChangeObserver(observer, null);
      AndroidUtils.log(IconPaths.Resource,
                       String.format(
                           "LifecycleHelper [%s] - registered ObservablePropertyListener [%s] for property [%s]",
                           name,
                           observer.getName(),
                           observer.getProperty()
                       )
      );
    }
  }
  catch (Exception e) {
    AndroidUtils.logErr(IconPaths.Resource,
                        String.format("LifecycleHelper [%s] - problem binding a resource - %s",
                                      name,
                                      e.toString())
    );
    if (PerfDirectives.EnableDebugMode) {
      String.format("LifecycleHelper [%s] - problem binding a resource DETAILS - %s",
                    name,
                    Arrays.toString(e.getStackTrace())
      );
    }
  }
  finally {
    resourceList.add(resource);
  }
}

}//end class LifecycleHelper