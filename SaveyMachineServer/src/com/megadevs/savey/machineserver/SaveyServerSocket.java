package com.megadevs.savey.machineserver;

import com.megadevs.savey.machinecommon.Logg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SaveyServerSocket extends Thread {

    private static final int PORT = 9876;

    private static SaveyServerSocket instance;

    public static SaveyServerSocket getInstance() {
        return new SaveyServerSocket();
    }

    private List<HandleClient> connectedClients = new CopyOnWriteArrayList<HandleClient>();
    private ServerSocket mSocket;
    private boolean running = true;
    private boolean started = false;

    private SaveyServerSocket() {}

    @Override
    public synchronized void start() {
        if (!started) {
            super.start();
            started = true;
        }
    }

    @Override
    public void run() {
        try {
            if (mSocket != null) {
                try {
                    mSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            while (mSocket == null) {
                try {
                    mSocket = new ServerSocket(PORT);
                } catch (Exception e) {
                    Logg.e("Couldn't create server socket: %s", e.getMessage());
                    e.printStackTrace();
                    Thread.sleep(200);
                }
            }
            listen();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void listen() {
        while (running && !isInterrupted()) {
            try {
                Logg.d("Listening...");
                Socket client = mSocket.accept();
                Logg.d("Incoming connection accepted!");
                HandleClient handler = new HandleClient(this, client);
                connectedClients.add(handler);
                handler.start();
                Logg.d("Handler started");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        for (HandleClient client : connectedClients) {
            client.close();
        }
        try {
            mSocket.close();
        } catch (IOException e) {}
        mSocket = null;
        Logg.d("Server socket dead");
    }

    public void remove(HandleClient client) {
        connectedClients.remove(client);
    }

    public void onDestroy() {
        Logg.d("onDestroy server socket");
        running = false;
        interrupt();
    }

}
