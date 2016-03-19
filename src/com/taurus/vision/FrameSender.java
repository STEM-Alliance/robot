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
    private Target targetInfo = null;
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
                    processSmartDashboardImage(frame, targetInfo, color);
                }
                
                // send frame
                CameraServer.getInstance().setImage(frame);
                ready = false;
            }
        }
    }

    public void sendFrame(Image frame)
    {
        sendFrame(frame,false,null,0);
    }
    
    public void sendFrame(Image frame, Target targetInfo, int color)
    {
        sendFrame(frame,true,targetInfo,color);
    }
    
    public void sendFrame(Image frame, boolean addTargetInfo, Target targetInfo, int color)
    {
        // copy frame
        NIVision.imaqScale(this.frame, frame, 1, 1, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);
        
        // store other fields
        if(targetInfo != null && addTargetInfo)
        {
            this.targetInfo = targetInfo;
        }
        this.addTargetInfo = addTargetInfo;
        
        this.color = color;
        
        ready = true;
    }
    

    private void processSmartDashboardImage(Image frame, Target targetInfo, int color)
    {
        //Rect centerRect = new Rect(Constants.BallShotY - Constants.BallShotDiameter/2,  // top
//                                   Constants.BallShotX - Constants.BallShotDiameter/2,  // left
//                                   Constants.BallShotDiameter,                          // width
//                                   Constants.BallShotDiameter);                         // height
        // draw a circle in the center of the image/where the ball shoots
        //NIVision.imaqDrawShapeOnImage(frame, frame, centerRect, DrawMode.PAINT_VALUE, ShapeMode.SHAPE_OVAL, color);

        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(0,(int)Constants.Height - Constants.BallShotY),
                new Point((int) Constants.Width,(int)Constants.Height - Constants.BallShotY), color);
        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(Constants.BallShotX,0),
                new Point(Constants.BallShotX,(int) Constants.Height), color);
        
        if(targetInfo != null)
        {
            // add Target Drawing
            Rect targetRect = new Rect((int)targetInfo.Top(), (int)targetInfo.Left(), (int)Math.ceil(targetInfo.H()), (int)Math.ceil(targetInfo.W()));
            // draw an outline of a rectangle around the target
            NIVision.imaqDrawShapeOnImage(frame, frame, targetRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, color);            
        }
    }

}
