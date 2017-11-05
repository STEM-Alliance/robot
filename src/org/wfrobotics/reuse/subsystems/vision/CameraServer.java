package org.wfrobotics.reuse.subsystems.vision;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;

import org.wfrobotics.reuse.utilities.HerdLogger;

/** Gets messages from the co-processor, tells anybody registered **/
public class CameraServer implements Runnable
{
    /** For anybody registering to receive updates from camera server **/
    public static interface CameraListener
    {
        public void CameraCallback(String m);
    }

    private static CameraServer instance = null;

    private HerdLogger log = new HerdLogger(CameraServer.class);
    private CameraListener listener;
    private Socket socket;

    private CameraServer()
    {
        socket = new Socket();  // TODO This needs to be something implementing iSocket that talks to the roo
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
        if (socket == null || socket.isClosed())
        {
            log.warning("CameraServer", "No socket");
            return;
        }

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
        if (socket == null)
        {
            log.warning("CameraServer", "No socket");
            return;
        }

        try
        {
            InputStream io = socket.getInputStream();
            byte[] buffer = new byte[1024];
            int len;

            log.debug("CameraServer", "Connected");
            while (socket.isConnected() && (len = io.read(buffer)) != -1)  // TODO Assume read returns zero without buffered message. Otherwise change below to buffer up new bytes and parse for end of message char
            {
                String raw = new String(buffer, 0, len);
                listener.CameraCallback(raw);
                try
                {
                    Thread.sleep(20);
                }
                catch (InterruptedException e)
                {
                    log.debug("CameraServer", e.getMessage());
                }
            }
            log.debug("CameraServer", "Disconnected");
        }
        catch (IOException error)
        {
            log.error("CameraServer", error.getMessage());
        }
        if (socket != null)
        {
            try
            {
                socket.close();
            }
            catch (IOException e)
            {
                log.warning("CameraServer", "Socket wont close");
            }
        }
    }
}