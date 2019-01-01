package cn.vove7.ctassistant;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;

import cn.vove7.ctassistant.cthelper.utils.UrlUtils;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ApplyAdapterActivity extends AppCompatActivity {
    private EditText schoolName, schoolWebsite, testAccount, testPassword;
    private Button submit;
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_adapter);
        initView();
    }

    private void initView() {
        schoolName = findViewById(R.id.apply_adapter_school_name);
        schoolWebsite = findViewById(R.id.apply_adapter_school_website);
        submit = findViewById(R.id.apply_adapter_school_submit);
        testAccount = findViewById(R.id.apply_adapter_school_test_account);
        testPassword = findViewById(R.id.apply_adapter_school_test_password);
        toolbar = findViewById(R.id.bottom_toolbar);
        submit.setOnClickListener(v -> {
            if (!schoolName.getText().toString().isEmpty() && !schoolWebsite.getText().toString().isEmpty() && !testAccount.getText().toString().isEmpty() && !testPassword.getText().toString().isEmpty()) {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    //Form表单格式的参数传递
                    FormBody formBody = new FormBody
                            .Builder()
                            .add("schoolName", schoolName.getText().toString())
                            .add("schoolWebsite", schoolWebsite.getText().toString())
                            .add("testAccount", testAccount.getText().toString())
                            .add("testPassword", testPassword.getText().toString())
                            //设置参数名称和参数值
                            .build();
                    Request request = new Request.Builder()
                            .url(UrlUtils.INSTANCE.getURL_POST_APPLY_ADAPTER())
                            .post(formBody)
                            //Post请求的参数传递
                            .build();
                    okHttpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            new Handler(Looper.getMainLooper()).post(() -> {
                                if (BuildConfig.DEBUG) {
                                    Toast.makeText(ApplyAdapterActivity.this, e.toString(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ApplyAdapterActivity.this, "网络或服务器异常！", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ResponseResult responseResult = new Gson().fromJson(response.body().toString(), ResponseResult.class);
                            if (responseResult.isStatus()) {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(ApplyAdapterActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();
                                });
                            } else {
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    Toast.makeText(ApplyAdapterActivity.this, "服务器异常，请稍后再试或联系开发人员！", Toast.LENGTH_SHORT).show();
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                new Handler(Looper.getMainLooper()).post(() -> {
                    Toast.makeText(ApplyAdapterActivity.this, "请完善信息后再次提交！", Toast.LENGTH_SHORT).show();
                });
            }
        });
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    public class ResponseResult {
        public boolean isStatus() {
            return status;
        }

        public void setStatus(boolean status) {
            this.status = status;
        }

        boolean status;

    }
}
