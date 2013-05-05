package com.megadevs.savey.machinecommon.network;

import com.megadevs.savey.machinecommon.data.APIResponse;

public interface WebService {

    public void getMachineQrCode(final int machineId, final OnWebServiceResponse listener);
    public void getCredit(final int taskId, final OnWebServiceResponse listener);
    public void getTask(final int machineId, final int userId, final OnWebServiceResponse listener);
    public void sendTaskData(final int machineId, final int userId, final int taskId, final String result, final OnWebServiceResponse listener);

    public interface OnWebServiceResponse {
        public void onWebServiceResponse(APIResponse response);
    }

}
