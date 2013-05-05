package com.megadevs.savey.machinecommon.data;

import java.io.Serializable;

public class QrCodeData implements Serializable {

    public String savey;

    @Override
    public String toString() {
        return "QrCodeData - Savey: " + savey;
    }
}
