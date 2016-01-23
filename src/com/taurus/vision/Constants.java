package com.taurus.vision;

public class Constants {

    public static final double DiagonalFOV = 68.5;
    
    public static final double Width = 640;
    public static final double Height = 480;
    public static final double Ratio = Width/Height;

    public static final double TargetWidthIn = 0; //TODO
    public static final double TargetHeightIn = 0; //TODO
    
    // see http://www.pyimagesearch.com/2015/01/19/find-distance-camera-objectmarker-using-python-opencv/
    private static final double TestTargetDistanceIn = 0; //TODO
    private static final double TestTargetWidthPixel = 0; //TODO
    public static final double FocalLengthIn = TestTargetWidthPixel * TestTargetDistanceIn / TargetWidthIn;

}
