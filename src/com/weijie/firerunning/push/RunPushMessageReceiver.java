package com.weijie.firerunning.push;

import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import cn.bmob.push.PushConstants;

import com.weijie.firerunning.util.ViewUtil;

public class RunPushMessageReceiver extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(PushConstants.ACTION_MESSAGE)){
            //Log.d("bmob", "客户端收到推送内容："+intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
        	//{"alert":"测试"}NewDiscuss
        	//ViewUtil.getInstance().showToast("客户端收到推送内容："+intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
        	try {
        		JSONObject js = new JSONObject(intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING));
            	if(js.optString("alert").equals("NewDiscuss")) {
            		ViewUtil.getInstance().showToast("您的讨论组收到新的消息！");
            	}
			} catch (Exception e) {
			}
        	
        }
    }

}