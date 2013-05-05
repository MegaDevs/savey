package com.megadevs.savey.machinecommon.data;

import java.io.Serializable;

public class APIResponse implements Serializable {

    public int task_id;
    public int machine_id;
    public TaskType type;
    public String title;
    public String[] content;
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
