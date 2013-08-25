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

package zen.uicomponents;

import android.*;
import android.content.*;
import android.util.*;
import android.view.animation.*;
import android.widget.*;

/**
 * <ol>
 * <li><a href="http://www.java2s.com/Code/Android/UI/extendsFrameLayout.htm">Sample code</a>
 * </li>
 * <li><a href="http://developer.android.com/guide/topics/graphics/view-animation.html">Android animation
 * tutorial</a></li>
 * </ol>
 *
 * @author Nazmul Idris
 * @version 1.0
 * @since 7/28/13, 3:58 PM
 */
public class AnimatedButton extends Button {

private Context ctx = null;

private Animation inAnimation;
private Animation outAnimation;

public AnimatedButton(Context context) {
  super(context);
  this.ctx = context;

}

public AnimatedButton(Context context, AttributeSet attrs) {
  super(context, attrs);
  this.ctx = context;
  _init();
}

public AnimatedButton(Context context, AttributeSet attrs, int defStyle) {
  super(context, attrs, defStyle);
  this.ctx = context;
  _init();
}

private void _init() {
  setInAnimation(AnimationUtils.loadAnimation(ctx, R.anim.fade_in));
  setOutAnimation(AnimationUtils.loadAnimation(ctx, R.anim.slide_out_right));
}

public void setInAnimation(Animation inAnimation) {
  this.inAnimation = inAnimation;
}

public void setOutAnimation(Animation outAnimation) {
  this.outAnimation = outAnimation;
}

@Override
public void setVisibility(int visibility) {

  if (getVisibility() != visibility) {
    if (visibility == VISIBLE) {
      if (inAnimation != null) { startAnimation(inAnimation); }
    }
    else if ((visibility == INVISIBLE) || (visibility == GONE)) {
      if (outAnimation != null) { startAnimation(outAnimation); }
    }
  }

  super.setVisibility(visibility);

}

}//end class AnimatedButton