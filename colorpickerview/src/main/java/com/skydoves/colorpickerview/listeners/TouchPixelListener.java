package com.skydoves.colorpickerview.listeners;

public interface TouchPixelListener extends ColorPickerViewListener {

  void onPointDownSelected(double x, double y);
  void onPointUpSelected();
}
