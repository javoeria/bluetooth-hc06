package com.javier.bluetooth_hc06.util;

import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.javier.bluetooth_hc06.DeviceActivity;

public class MyHandler extends Handler {

    public static final int UPDATE_CNT = 0;
    public static final int UPDATE_MSG = 1;
    private DeviceActivity parent;

    public MyHandler(DeviceActivity parent) {
        super();
        this.parent = parent;
    }

    @Override
    public void handleMessage(Message msg) {

        switch (msg.what){
            case UPDATE_CNT:
                int c = (int)msg.obj;
                //parent.updateCnt(c);
                msg("update_cnt");
                break;
            case UPDATE_MSG:
                String m = (String)msg.obj;
                //parent.updateMsg(m);
                msg(m);
                break;
            default:
                super.handleMessage(msg);
        }

    }

    private void msg(String s) {
        Toast.makeText(parent, s, Toast.LENGTH_SHORT).show();
    }
}
