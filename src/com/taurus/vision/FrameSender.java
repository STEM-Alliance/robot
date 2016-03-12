package com.taurus.vision;

import com.ni.vision.NIVision;
import com.ni.vision.NIVision.DrawMode;
import com.ni.vision.NIVision.Image;
import com.ni.vision.NIVision.Point;
import com.ni.vision.NIVision.Rect;
import com.ni.vision.NIVision.ScalingMode;
import com.ni.vision.NIVision.ShapeMode;

import edu.wpi.first.wpilibj.CameraServer;

public class FrameSender implements Runnable {

    private boolean ready = false;
    private boolean addTargetInfo = false;
    private int color;
    private Image frame;
    
    public FrameSender()
    {
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
    }
    
    @Override
    public void run()
    {
        while(true)
        {
            if(ready)
            {
                
                // add target info
                if(addTargetInfo)
                {
                    processSmartDashboardImage(frame, color);
                }
                
                // send frame
                CameraServer.getInstance().setImage(frame);
                ready = false;
            }
        }
    }
    
    public void sendFrame(Image frame, boolean addTargetInfo, int color)
    {
        // copy frame
        NIVision.imaqScale(this.frame, frame, 1, 1, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);
        
        // store other fields
        this.addTargetInfo = addTargetInfo;
        this.color = color;
        ready = true;
    }
    

    private void processSmartDashboardImage(Image frame, int color)
    {
        //Rect centerRect = new Rect(Constants.BallShotY - Constants.BallShotDiameter/2,  // top
//                                   Constants.BallShotX - Constants.BallShotDiameter/2,  // left
//                                   Constants.BallShotDiameter,                          // width
//                                   Constants.BallShotDiameter);                         // height
        // draw a circle in the center of the image/where the ball shoots
        //NIVision.imaqDrawShapeOnImage(frame, frame, centerRect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_OVAL, color);

        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(0,Constants.BallShotY),
                new Point((int) Constants.Width,Constants.BallShotY), color);
        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(Constants.BallShotX,0),
                new Point(Constants.BallShotX,(int) Constants.Height), color);
        
        Target largestTarget = Vision.getInstance().getTarget();
                                   
        if(largestTarget != null)
        {
            // add Target Drawing
            Rect targetRect = new Rect((int)largestTarget.Top(), (int)largestTarget.Left(), (int)Math.ceil(largestTarget.H()), (int)Math.ceil(largestTarget.W()));
            // draw an outline of a rectangle around the target
            NIVision.imaqDrawShapeOnImage(frame, frame, targetRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, color);            
        }
    }

}
