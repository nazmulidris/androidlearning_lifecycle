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

package zen.utlis;

import java.io.*;
import java.lang.ref.*;
import java.util.*;

/**
 * Copied from <a href="http://code.ohloh.net/search?s=SoftHashMap&browser=Default">Ohloh Code</a>.
 * <p/>
 * This implementation is taken from <a
 * href="http://www.javaspecialists.co.za/archive/Issue098.html">The Java
 * Specialists' Newsletter [Issue 098]</a> with permission of the original
 * author. - Dr. Heinz M. Kabutz
 * <p/>
 * Notes - use this class judiciously. When you use it, don't be surprised when entries go missing
 * over time, since that's what this is designed to do. Only use this class for a cache that can
 * easily be replenished, and is meant to be replenished by the code that uses this!
 */
public class SoftHashMap<K, V> extends AbstractMap<K, V> implements Serializable {

/** The internal HashMap that will hold the SoftReference. */
private final Map<K, SoftReference<V>> hash = new HashMap<K, SoftReference<V>>();

private final Map<SoftReference<V>, K> reverseLookup = new HashMap<SoftReference<V>, K>();

/** Reference queue for cleared SoftReference objects. */
private final ReferenceQueue<V> queue = new ReferenceQueue<V>();

@Override
public V get(Object key) {
  expungeStaleEntries();
  V result = null;
  // We get the SoftReference represented by that key
  SoftReference<V> soft_ref = hash.get(key);
  if (soft_ref != null) {
    // From the SoftReference we get the value, which can be
    // null if it has been garbage collected
    result = soft_ref.get();
    if (result == null) {
      // If the value has been garbage collected, remove the
      // entry from the HashMap.
      hash.remove(key);
      reverseLookup.remove(soft_ref);
    }
  }
  return result;
}

private void expungeStaleEntries() {
  Reference<? extends V> sv;
  while ((sv = queue.poll()) != null) {
    hash.remove(reverseLookup.remove(sv.get()));
  }
}

@Override
public V put(K key, V value) {
  expungeStaleEntries();
  SoftReference<V> soft_ref = new SoftReference<V>(value, queue);
  reverseLookup.put(soft_ref, key);
  SoftReference<V> result = hash.put(key, soft_ref);
  if (result == null) { return null; }
  return result.get();
}

@Override
public V remove(Object key) {
  expungeStaleEntries();
  SoftReference<V> result = hash.remove(key);
  if (result == null) { return null; }
  return result.get();
}

@Override
public void clear() {
  hash.clear();
  reverseLookup.clear();
}

@Override
public int size() {
  expungeStaleEntries();
  return hash.size();
}

/**
 * Returns a copy of the key/values in the map at the point of calling.
 * However, setValue still sets the value in the actual SoftHashMap.
 */
@Override
public Set<Entry<K, V>> entrySet() {
  expungeStaleEntries();
  Set<Entry<K, V>> result = new LinkedHashSet<Entry<K, V>>();
  for (final Entry<K, SoftReference<V>> entry : hash.entrySet()) {
    final V value = entry.getValue().get();
    if (value != null) {
      result.add(new Entry<K, V>() {
        public K getKey() {
          return entry.getKey();
        }

        public V getValue() {
          return value;
        }

        public V setValue(V v) {
          entry.setValue(new SoftReference<V>(v, queue));
          return value;
        }
      });
    }
  }
  return result;
}

}// end class SoftHashMap