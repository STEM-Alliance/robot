package org.wfrobotics.robot.vision;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;

import org.wfrobotics.reuse.utilities.HerdLogger;
import org.wfrobotics.robot.vision.util.CameraListener;
import org.wfrobotics.robot.vision.util.VisionMessage;

/** Gets messages from the co-processor, tells anybody registered **/
public class CameraServer implements Runnable
{
    private static CameraServer instance = null;

    private HerdLogger log = new HerdLogger(CameraServer.class);
    private CameraListener listener;
    private Socket socket;

    private CameraServer()
    {
        socket = new Socket();
        log.setLevel(Level.WARNING);
        new Thread(this).start();
    }

    public static CameraServer getInstance()
    {
        if (instance == null) { instance = new CameraServer(); }
        return instance;
    }

    public synchronized void register(CameraListener consumer)
    {
        listener = consumer;
    }

    public void send(VisionMessage m)
    {
        try
        {
            OutputStream io = socket.getOutputStream();
            io.write(m.toBytes());
        }
        catch (IOException e)
        {
            log.error("CameraServer", "Can't send");
        }
    }

    public void run()
    {
        if (socket == null) { return; }
        try
        {
            InputStream io = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int len;

            log.debug("CameraServer", "Connected");
            while (socket.isConnected() && (len = io.read(buffer)) != -1)
            {
                String raw = new String(buffer, 0, len);
                listener.CameraCallback(raw);
            }
            log.debug("CameraServer", "Disconnected");
        }
        catch (IOException error)
        {
            log.error("CameraServer", "Socket unresponsive");
        }
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                log.error("CameraServer", "Socket wont close");
            }
        }
    }
}