package org.wfrobotics.vision;

import org.wfrobotics.led.Color;
import org.wfrobotics.vision.Vision.COLORS;

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
    private Image frameTH;
    private boolean threshold;
    
    public FrameSender()
    {
        frame = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_RGB, 10);
        frameTH = NIVision.imaqCreateImage(NIVision.ImageType.IMAGE_U8, 10);
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
                    if(!threshold)
                        processSmartDashboardImage(frame, targetInfo, color);
                    else
                        processSmartDashboardImage(frameTH, targetInfo, color);
                }
                
                // send frame
                if(!threshold)
                    CameraServer.getInstance().setImage(frame);
                else
                    CameraServer.getInstance().setImage(frameTH);
                    
                ready = false;
            }
        }
    }

    public void sendFrame(Image frame)
    {
        sendFrame(frame,false,null,0, false);
    }

    public void sendFrame(Image frame, Target targetInfo)
    {
        sendFrame(frame,true,targetInfo,COLORS.WHITE, true);
    }
    
    public void sendFrame(Image frame, Target targetInfo, int color)
    {
        sendFrame(frame,true,targetInfo,color, false);
    }
    
    public void sendFrame(Image frame, boolean addTargetInfo, Target targetInfo, int color, boolean threshold)
    {
        // copy frame
        if(!threshold)
            NIVision.imaqScale(this.frame, frame, 1, 1, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);
        else
            NIVision.imaqScale(this.frameTH, frame, 1, 1, ScalingMode.SCALE_SMALLER, NIVision.NO_RECT);
            
        
        // store other fields
        if(targetInfo != null && addTargetInfo)
        {
            this.targetInfo = targetInfo;
        }
        else
        {
            this.targetInfo = null;
        }
        this.addTargetInfo = addTargetInfo;
        
        this.threshold = threshold;
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
                new Point(0,Constants.BallShotY),
                new Point((int) Constants.Width,Constants.BallShotY), color);
        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(Constants.BallShotX,0),
                new Point(Constants.BallShotX,(int) Constants.Height), color);

        int color2 = COLORS.WHITE;
        
        if(color != COLORS.WHITE)
        {
            color2 = COLORS.CYAN;
        }
        
        NIVision.imaqDrawLineOnImage(frame, frame, DrawMode.DRAW_VALUE,
                new Point(0,Constants.BallShotY2),
                new Point((int) Constants.Width,Constants.BallShotY2), color2);
        
        if(targetInfo != null)
        {
            // add Target Drawing
            Rect targetRect = new Rect((int)targetInfo.Top(), (int)targetInfo.Left(), (int)Math.ceil(targetInfo.H()), (int)Math.ceil(targetInfo.W()));
            // draw an outline of a rectangle around the target
            NIVision.imaqDrawShapeOnImage(frame, frame, targetRect, DrawMode.DRAW_VALUE, ShapeMode.SHAPE_RECT, color);            
        }
    }

}
