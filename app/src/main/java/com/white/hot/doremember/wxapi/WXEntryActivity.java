package com.white.hot.doremember.wxapi;


import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * Created by ntop on 15/9/4.
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    @Override
    public void onReq(BaseReq baseReq)
    {
        Toast.makeText(this, "请求",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResp(BaseResp baseResp)
    {
        Toast.makeText(this, "成功",Toast.LENGTH_SHORT).show();
    }
}
