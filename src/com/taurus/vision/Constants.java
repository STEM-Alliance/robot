package com.taurus.vision;

public class Constants {

    public static final double DiagonalFOV = 68.5;
    
    public static final double Width = 320;
    public static final double Height = 240;
    public static final double Ratio = Width/Height;

    public static final double TargetWidthIn = 20; 
    public static final double TargetHeightIn = 14; //TODO
    
    // see http://www.pyimagesearch.com/2015/01/19/find-distance-camera-objectmarker-using-python-opencv/
    private static final double TestTargetDistanceIn = 130; 
    private static final double TestTargetWidthPixel = 56; 
    public static final double FocalLengthIn = TestTargetWidthPixel * TestTargetDistanceIn / TargetWidthIn;
    

}
