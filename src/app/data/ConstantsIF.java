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

package app.data;

import java.util.concurrent.*;

/**
 * @author Nazmul Idris
 * @version 1.0
 * @since 11/21/12, 4:18 PM
 */
public interface ConstantsIF {

//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
// timing for the services and activity
//XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX

long MyActivityRepeatDelay_ms      = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
long MyServiceRepeatDelay_ms       = TimeUnit.MILLISECONDS.convert(10, TimeUnit.SECONDS);
long MyIntentServiceRepeatDelay_ms = TimeUnit.MILLISECONDS.convert(15, TimeUnit.SECONDS);

}//end interface DBConstantsIF
