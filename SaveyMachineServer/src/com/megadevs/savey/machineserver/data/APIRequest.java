package com.megadevs.savey.machineserver.data;

import java.io.Serializable;

public class APIRequest implements Serializable {

    public int id;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Id: ").append(id).append(" - ");
        return builder.toString();
    }
}
