/*
 * Copyright (C) 2013 Square, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package it.sephiroth.android.library.picasso;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

final class TargetAction extends Action<Target> {

  Callback callback;

  TargetAction(Picasso picasso, Target target, Request data, int memoryPolicy, int networkPolicy,
      Drawable errorDrawable, String key, Object tag, int errorResId, long fadeTime, Callback callback) {
    super(picasso, target, data, memoryPolicy, networkPolicy, errorResId, errorDrawable, key, tag,
        fadeTime);
    this.callback = callback;
  }

  @Override void complete(Bitmap result, Picasso.LoadedFrom from) {
    if (result == null) {
      throw new AssertionError(
          String.format("Attempted to complete action with no result!\n%s", this));
    }
    Target target = getTarget();
    if (target != null) {
      target.onBitmapLoaded(result, from);
      if (result.isRecycled()) {
        throw new IllegalStateException("Target callback must not recycle bitmap!");
      }
    }

    if (callback != null) {
      callback.onSuccess();
    }
  }

  @Override void error() {
    Target target = getTarget();
    if (target != null) {
      if (errorResId != 0) {
        target.onBitmapFailed(picasso.context.getResources().getDrawable(errorResId));
      } else {
        target.onBitmapFailed(errorDrawable);
      }
    }

    if (callback != null) {
      callback.onError();
    }
  }

  @Override
  void cancel() {
    super.cancel();
    if (callback != null) {
      callback = null;
    }
  }
}
