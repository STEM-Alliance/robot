package org.wfrobotics.reuse.subsystems.vision;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageConfig;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageTargets;
import org.wfrobotics.reuse.utilities.HerdLogger;

public class CameraServer
{
    private final static int DEFAULT_PORT = 5801;
    
    /**
     * Interface to describe the listener callback
     */
    public static interface CameraListener
    {
        void Notify(VisionMessageTargets message);
    }

    // global configuration variable
    private VisionMessageConfig Config = new VisionMessageConfig(2);
    private VisionMessageTargets LastTargets = new VisionMessageTargets();
    
    
    // network port to serve on
    private final int PORT;
    
    // list of listeners to notify when receiving messages
    private List<CameraListener> listeners = new ArrayList<CameraListener>();

    // server task to monitor for new clients
    private Runnable serverTask;
    
    // server thread to run the task on
    private Thread serverThread;
    
    // pool of threads for client connections to run under
    final ExecutorService clientProcessingPool = Executors.newFixedThreadPool(10);

    // actual socket that reads the data
    private ServerSocket serverSocket;
    
    private HerdLogger log = new HerdLogger(CameraServer.class);

    private static CameraServer instance = null;
    
    /**
     * Get the instance of the TargetServer at the default port {@value #DEFAULT_PORT}
     * @return
     */
    public static CameraServer getInstance()
    {
        if (instance == null)
        {
            instance = new CameraServer(DEFAULT_PORT);
        }
        return instance;
    }

    /**
     * Initialize the server
     * @param port
     */
    private CameraServer(int port)
    {
        this.PORT = port;

        // set up a server task so that it can run in the background
        // and wait for clients to connect
        serverTask = new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    serverSocket = new ServerSocket(PORT);
                    log.info("CameraServer", "Waiting for clients to connect at " +
                            serverSocket.getInetAddress().getHostAddress() + ":" + 
                            serverSocket.getLocalPort());

                    while (true)
                    {
                        Socket clientSocket = serverSocket.accept();
                        clientProcessingPool.submit(new ClientTask(clientSocket));
                    }
                } catch (IOException e)
                {
                    log.error("CameraServer", "Unable to process client request");
                    //e.printStackTrace();
                    //log.exception("CameraServer", e);
                }
            }
        };
        
        serverThread = new Thread(serverTask, "CameraServer");
        serverThread.start();
    }

    /**
     * Add a listener to the list
     * @param toAdd
     */
    public synchronized void AddListener(CameraListener toAdd)
    {
        listeners.add(toAdd);
    }

    /**
     * Remove a listener from the list
     * @param toRemove
     */
    public synchronized void RemoveListener(CameraListener toRemove)
    {
        listeners.remove(toRemove);
    }

    /**
     * Notify all the added listeners with the data that was received
     */
    private void NotifyListeners(VisionMessageTargets message)
    {
        for (CameraListener tl : listeners)
        {
            tl.Notify(message);
        }
    }

    public synchronized void SetConfig(VisionMessageConfig m)
    {
        Config = m;
    }

    public VisionMessageTargets GetTargets()
    {
        return LastTargets;
    }
    
    /**
     * Task to run the client connections
     */
    private class ClientTask implements Runnable
    {
        private final Socket clientSocket;

        private ClientTask(Socket clientSocket)
        {
            this.clientSocket = clientSocket;
        }

        @Override
        public void run()
        {
            boolean run = true;

            String fromClient;
            
            try
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

                log.debug("CameraServer", "Connected to a client at " + clientSocket.getInetAddress().getHostAddress());
                
                while (run)
                {
                    fromClient = in.readLine();
                    
                    if(fromClient != null)
                    {
                        // only parse while we get messages
                        VisionMessageTargets message = new VisionMessageTargets(fromClient);
                        log.debug("CameraServer", "RX: " + message.Decoded());
                        
                        // send the config message to sync latest settings
                        out.println(Config.toString());
                        log.debug("CameraServer", "TX: " + Config.Decoded());
                        
                        NotifyListeners(message);
                    }
                    else
                    {
                        // quit once we get null
                        run = false;
                    }
                }

                // close the connection now that we're done
                clientSocket.close();
                
            }
            catch (IOException e)
            {
                e.printStackTrace();
                //log.exception("CameraServer", e);
                log.error("CameraServer", "Error during normal operation");
                
                // try and force the socket to close on a failure
                try
                {
                    clientSocket.close();
                }
                catch(IOException ex)
                {
                    ex.printStackTrace();
                    log.error("CameraServer", "Unable to close socket");
                    //log.exception("CameraServer", ex);
                }
                
            }
        }
    }
}
