package com.megadevs.savey.machineserver.data;

import java.io.Serializable;

public class Response implements Serializable {

    public RequestType type;
    public String qrcode;
    public boolean success;

    public Response(RequestType type) {
        this.type = type;
    }

    public Response(Request request) {
        this.type = request.type;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Type: ").append(type).append(" - ");
        builder.append("Qrcode: ").append(qrcode).append(" - ");
        builder.append("Success: ").append(success);
        return builder.toString();
    }
}
