package com.xl.test;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.xl.test.app.ConstantsImageUrl;
import com.xl.test.databinding.ActivityLoadingBinding;
import com.xl.test.utils.CommonUtils;
import com.xl.test.utils.ImageUtils;

import java.util.Random;

public class LoadingActivity extends AppCompatActivity {
    private ActivityLoadingBinding binding;
    private boolean isIn;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_loading);
        binding.tvJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toMainActivity();
            }
        });
        int i = new Random().nextInt(ConstantsImageUrl.TRANSITION_URLS.length);
        binding.ivDefultPic.setImageDrawable(CommonUtils.getDrawable(R.drawable
                .img_transition_default));
        ImageUtils.getInstance().loadImageview(ConstantsImageUrl.TRANSITION_URLS[i],
                R.drawable.img_transition_default, R.drawable.img_transition_default, binding
                        .ivPic);
        handler.sendEmptyMessageDelayed(1, 1500);
        handler.sendEmptyMessageDelayed(2, 3500);
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                binding.ivDefultPic.setVisibility(View.GONE);
            } else if (msg.what == 2) {
                toMainActivity();
            }
        }
    };

    private void toMainActivity() {
        if (isIn) {
            return;
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        overridePendingTransition(R.anim.screen_zoom_in, R.anim.screen_zoom_out);
        finish();
        isIn = true;
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {


            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION

            );

        }
    }

}
