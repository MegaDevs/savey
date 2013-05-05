package com.megadevs.savey.machineserver;

import com.megadevs.savey.machinecommon.GsonWrapper;
import com.megadevs.savey.machinecommon.Logg;
import com.megadevs.savey.machinecommon.data.Request;
import com.megadevs.savey.machinecommon.data.Response;

import java.io.*;
import java.lang.ref.WeakReference;
import java.net.Socket;

public class HandleClient extends Thread {

    public static final int COFFE_DURATION = 10; //Duration in seconds

    private Socket mClient;
    private BufferedReader reader;
    private BufferedWriter writer;
    private boolean running = true;
    private WeakReference<SaveyServerSocket> serverSocket;

    public HandleClient(SaveyServerSocket socket, Socket client) throws IOException {
        mClient = client;
        serverSocket = new WeakReference<SaveyServerSocket>(socket);
        reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
        writer = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
        //writer = new BufferedOutputStream(mClient.getOutputStream(), 500 * 1024);
    }

    @Override
    public void run() {
        while (running && !isInterrupted()) {
            if (mClient.isInputShutdown() || mClient.isClosed() || !mClient.isConnected() || !mClient.isBound() || mClient.isOutputShutdown()) {
                close();
                break;
            }
            Request request = readRequest();
            if (!running || isInterrupted()) break;
            if (request == null) {
                Logg.e("Couldn't read request");
                //continue;
                break;
            }
            Response response = processRequest(request);
            if (response != null) {
                sendResponse(response);
            }
        }
        try {
            reader.close();
            writer.close();
            SaveyServerSocket socket = serverSocket.get();
            if (socket != null) {
                socket.remove(this);
            }
        } catch (IOException e) {
            Logg.e("Exception while closing streams");
            e.printStackTrace();
        }
    }

    private Request readRequest() {
        try {
            Logg.d("Reading request...");
            String json = reader.readLine();
            Logg.d("Json: " + json);
            Request request = GsonWrapper.getRequest(json);
            Logg.d("Read request: " + request);
            return request;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private Response processRequest(Request request) {
        Response response = null;
        switch (request.type) {
            case GET_MACHINE_QRCODE:
                response = new Response(request);
                response.qrcode = "iVBORw0KGgoAAAANSUhEUgAAAOYAAADmCAIAAABOCG7sAAAABmJLR0QA/wD/AP+gvaeTAAAELElEQVR4nO3dwW7jNhRA0bjo//9yusimgDCARiOS77bn7GM5xgUB0jT5+f7+/oKOv06/Afg9kiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLjGSJkSwxkiVGssRIlhjJEiNZYiRLzN+nHvz5fE49+uvr63rb2Vvv59k9aneevu49P3PqxjijLDGSJUayxEiWGMkSc2zF4GrdDHTnasDZlYf5n+GfM8oSI1liJEuMZImRLDGDVgyuns1Sz86a33r69Vnrdi9cndo/cIdRlhjJEiNZYiRLjGSJGb1isNPZOfJb6wP/B0ZZYiRLjGSJkSwxkiXGisEvrduHf10NuHNGgTWEH0ZZYiRLjGSJkSwxkiVm9IrBzjnyzvWBnf576wxGWWIkS4xkiZEsMZIlZtCKwZyz9X48+97/zn+xbv/AtM9wBaMsMZIlRrLESJYYyRJzbMWg+N33zrMH7yh+hn/OKEuMZImRLDGSJUayxAzaY3D11jn/0+4duL7Os50Jz56+85VXMMoSI1liJEuMZImRLDGfU/O+1iz1x7rbFafd2zj53EWjLDGSJUayxEiWGMkSc2zF4I635tFv/S7g2bOenXWw7lnP3s+VPQZwi2SJkSwxkiVGssTE9hhc7fxO/9k37zv3D9x5+uQ1ojuMssRIlhjJEiNZYiRLzOhzDK52nj+w7tv5t7z1f11N/p2CUZYYyRIjWWIkS4xkiRm9YrBu//y6+e+zXQc7fxfw7H+fszPBKEuMZImRLDGSJUayxAz6VcLOnfl3rDsToHiGoT0G8JBkiZEsMZIlRrLEjN5j8NYs9a25/871gWnnPNhjAA9JlhjJEiNZYiRLzOgVg2nnBlxNm4/Xzyi4wyhLjGSJkSwxkiVGssSMvl1xp533IMw/tWDyGoJRlhjJEiNZYiRLjGSJGXSOwU47Z83rfl9w9jYH5xjALZIlRrLESJYYyRIz6FcJ808+vPPKO+fRb53qcDXt1x//ZpQlRrLESJYYyRIjWWIGrRhcTdu9v+7OhTvemsXv/FRXMMoSI1liJEuMZImRLDGjVwzO2jmv37nKMe20xt9llCVGssRIlhjJEiNZYqwY/NJbs+91f3XndebsDXiLUZYYyRIjWWIkS4xkiRm9YnD2TICzJxbe8eymhjqjLDGSJUayxEiWGMkSM2jFYNppe2dPPnxr/8DZ+xRWMMoSI1liJEuMZImRLDHHbleEZ4yyxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDGSJUayxEiWGMkSI1liJEuMZImRLDH/AFOfeMRlEe8sAAAAAElFTkSuQmCC";
                break;
            case ADD_CREDIT:
                ArduinoHandler.getInstance().addCredit(request.credit);
                break;
            case SELECT_PRODUCT:
                response = new Response(request);
                Product product = Product.fromOrdinal(request.product);
                if (product != null && product.getCost() <= ArduinoHandler.getInstance().getCurrentCredit()) {
                    ArduinoHandler.getInstance().removeCredit(product.getCost());
                    ArduinoHandler.getInstance().startCoffe(COFFE_DURATION);
                    MusicPlayer.getInstance().play();
                    response.success = true;
                } else {
                    response.success = false;
                }
                break;
        }
        return response;
    }

    private void sendResponse(Response response) {
        try {
            Logg.d("Sending response: %s", response);
            String json = GsonWrapper.toJson(response);
            Logg.d("Json: %s", json);

            writer.write(json);
            writer.flush();

            /*writer.write(json.getBytes());
            writer.flush();*/

            Logg.d("Response sent");
        } catch (IOException e) {
            Logg.e("Error while sending response");
            e.printStackTrace();
        }
    }

    public void close() {
        running = false;
        interrupt();
    }

}
