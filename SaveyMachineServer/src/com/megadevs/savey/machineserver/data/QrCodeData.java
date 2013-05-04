package com.megadevs.savey.machineserver.data;

import java.io.Serializable;

public class QrCodeData implements Serializable {

    public String savey;

    @Override
    public String toString() {
        return "QrCodeData - Savey: " + savey;
    }
}
