package com.megadevs.savey.machinecommon.data;

import java.io.Serializable;

public class APIRequest implements Serializable {

    public int user_id;
    public int machine_id;
    public int task_id;
    public int user_task_id;


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("User Id: ").append(user_id).append(" - ");
        builder.append("Machine Id: ").append(machine_id).append(" - ");
        builder.append("Task Id: ").append(task_id).append(" - ");
        builder.append("User Task Id: ").append(user_task_id).append(" - ");
        return builder.toString();
    }
}
