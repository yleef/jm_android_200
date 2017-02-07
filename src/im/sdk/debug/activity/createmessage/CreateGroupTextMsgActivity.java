package im.sdk.debug.activity.createmessage;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import cn.jpush.im.android.api.JMessageClient;
import cn.jpush.im.android.api.model.Message;
import cn.jpush.im.api.BasicCallback;
import  com.wepinche.jmus.R;

/**
 * Created by ${chenyn} on 16/3/31.
 *
 * @desc :创建群聊文本信息
 */
public class CreateGroupTextMsgActivity extends Activity {

    private EditText mEt_id;
    private EditText mEt_text;
    private Button   mBt_send;
    public static final String GROUP_NOTIFICATION      = "group_notification";
    public static final String GROUP_NOTIFICATION_LIST = "group_notification_list";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initView();
        initData();
    }

    //创建群聊文本消息.
    private void initData() {
        mBt_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = mEt_id.getText().toString();
                String text = mEt_text.getText().toString();
                if (!TextUtils.isEmpty(id)) {
                    long gid = Long.parseLong(id);
                    Message message = JMessageClient.createGroupTextMessage(gid, text);
                    message.setOnSendCompleteCallback(new BasicCallback() {
                        @Override
                        public void gotResult(int i, String s) {
                            if (i == 0) {
                                Log.i("CreateGroupTextMsgActivity", "JMessageClient.createGroupTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
                                Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
                            }else {
                                Log.i("CreateGroupTextMsgActivity", "JMessageClient.createGroupTextMessage" + ", responseCode = " + i + " ; LoginDesc = " + s);
                                Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                    JMessageClient.sendMessage(message);
                }else {
                    Toast.makeText(getApplicationContext(), "请输入群组id", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void initView() {
        setContentView(R.layout.activity_create_group_text_message);

        mEt_id = (EditText) findViewById(R.id.et_id);
        mEt_text = (EditText) findViewById(R.id.et_text);
        mBt_send = (Button) findViewById(R.id.bt_send);
    }
}
