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

/**
 * Interface used by {@link LifecycleHelper} to make it easy to manage resources that can be
 * shutdown. You can mark any object with this interface, and it will signal the
 * {@link LifecycleHelper} that it can be automagically shutdown when that resource is
 * unbound.
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 3/1/13, 6:00 PM
 */
public interface Shutdownable {

public void shutdown();

}//end interface Shutdownable