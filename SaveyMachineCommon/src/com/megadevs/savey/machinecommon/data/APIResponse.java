package com.megadevs.savey.machinecommon.data;

import java.io.Serializable;

public class APIResponse implements Serializable {

    public int task_id;
    public int machine_id;
    public TaskType type;
    public String title;
    public String[] content;
    public String qr_code;
    public boolean valid;
    public double credit;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("QrCode: ").append(qr_code).append(" - ");
        builder.append("Valid: ").append(valid).append(" - ");
        builder.append("Credit: ").append(credit).append(" - ");
        return builder.toString();
    }

}
