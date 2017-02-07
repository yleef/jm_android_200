package com.wepinche.jmus.activity;


import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;


import com.wepinche.jmus.R;
import com.wepinche.jmus.adapter.DocumentAdapter;
import com.wepinche.jmus.controller.SendFileController;
import com.wepinche.jmus.view.SendFileView;

public class SendFileActivity extends FragmentActivity {

    private SendFileView mView;
    private SendFileController mController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_file);
        mView = (SendFileView) findViewById(R.id.send_file_view);
        mView.initModule();
        mController = new SendFileController(this, mView);
        mView.setOnClickListener(mController);
        mView.setOnPageChangeListener(mController);
    }

    public FragmentManager getSupportFragmentManger() {
        // TODO Auto-generated method stub
        return getSupportFragmentManager();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
