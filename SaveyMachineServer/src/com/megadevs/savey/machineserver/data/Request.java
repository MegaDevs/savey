package com.megadevs.savey.machineserver.data;

import java.io.Serializable;

public class Request implements Serializable {

    public RequestType type;
    public double credit;
    public int product;

    public Request(RequestType type) {
        this.type = type;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Type: ").append(type).append(" - ");
        builder.append("Id: ").append(product).append(" - ");
        builder.append("Credit: ").append(credit);
        return builder.toString();
    }
}
