package com.megadevs.savey.machineserver.data;

public class APIResponse {

    public String qrcode;
    public boolean valid;
    public double credit;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QrCode: ").append(qrcode).append(" - ");
        builder.append("Valid: ").append(valid).append(" - ");
        builder.append("Credit: ").append(credit).append(" - ");
        return builder.toString();
    }

}
