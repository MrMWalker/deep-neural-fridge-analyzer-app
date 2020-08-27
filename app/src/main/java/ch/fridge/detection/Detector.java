/* Copyright 2015 The TensorFlow Authors. All Rights Reserved.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
==============================================================================*/

package ch.fridge.detection;

import android.graphics.Bitmap;

import ch.fridge.domain.Detection;

import java.util.List;

/**
 * Generic interface for interacting with different recognition engines.
 */
public interface Detector {
  /**
   * An immutable result returned by a Detector describing what was recognized.
   */

  DetectorType getDetectorType();

  List<Detection> detectImage(Bitmap image);

  void enableStatLogging(final boolean debug);

  String getStatString();

  void close();
}

