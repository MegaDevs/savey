package com.megadevs.savey.machineserver;

import com.megadevs.savey.machineserver.data.APIResponse;

public interface WebService {

    public void getMachineQrCode(int id, OnWebServiceResponse listener);
    public void getCredit(int taskId, OnWebServiceResponse listener);

    public interface OnWebServiceResponse {
        public void onWebServiceResponse(APIResponse response);
    }

}
