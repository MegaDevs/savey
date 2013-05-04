package com.megadevs.savey.machineserver;

import com.megadevs.savey.machinecommon.Logg;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SaveyServerSocket extends Thread {

    private static final int PORT = 9876;

    private List<HandleClient> connectedClients = new CopyOnWriteArrayList<HandleClient>();
    private ServerSocket mSocket;
    private boolean running = true;

    @Override
    public void run() {
        try {
            mSocket = new ServerSocket(PORT);
        } catch (Exception e) {
            throw new IllegalStateException("Cannot create socket");
        }
        listen();
    }

    private void listen() {
        while (running) {
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
        try {
            mSocket.close();
        } catch (IOException e) {}
        for (HandleClient client : connectedClients) {
            client.close();
        }
    }

    public void remove(HandleClient client) {
        connectedClients.remove(client);
    }

    public void onDestroy() {
        running = false;
        interrupt();
    }

}
