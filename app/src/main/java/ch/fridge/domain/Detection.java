package ch.fridge.domain;

import android.graphics.RectF;

import java.io.Serializable;

public class Detection implements Serializable {
  /**
   * A unique identifier for what has been recognized. Specific to the class, not the instance of
   * the object.
   */
  private final String id;

  /**
   * Label for the recognition
   */
  private final Label label;

  /**
   * A sortable score for how good the recognition is relative to others. Higher should be better.
   */
  private final Float confidence;

  /** Optional location within the source image for the location of the recognized object. */
  private RectF location;

  public Detection(
          final String id, final Label label, final Float confidence, final RectF location) {
    this.id = id;
    this.label = label;
    this.confidence = confidence;
    this.location = location;
  }

  public String getId() {
    return id;
  }

  public Label getLabel() {
    return label;
  }

  public Float getConfidence() {
    return confidence;
  }

  public RectF getLocation() {
    return new RectF(location);
  }

  public void setLocation(RectF location) {
    this.location = location;
  }

  @Override
  public String toString() {
    String resultString = "";
    if (id != null) {
      resultString += "[" + id + "] ";
    }

    if (label != null) {
      resultString += label + " ";
    }

    if (confidence != null) {
      resultString += String.format("(%.1f%%) ", confidence * 100.0f);
    }

    if (location != null) {
      resultString += location + " ";
    }

    return resultString.trim();
  }

  public String getDisplayText(){
    return String.format("%s %.1f%%", label.getDisplayText(), confidence * 100.0f);
  }
}
