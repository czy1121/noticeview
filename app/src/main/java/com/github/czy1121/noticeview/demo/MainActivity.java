package com.github.czy1121.noticeview.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.czy1121.view.NoticeView;

import java.util.Arrays;


public class MainActivity extends AppCompatActivity implements View.OnClickListener  {

    public static String[] notices = new String[]{
            "伪装者:胡歌演绎'痞子特工'",
            "无心法师:生死离别!月牙遭虐杀",
            "花千骨:尊上沦为花千骨",
            "综艺饭:胖轩偷看夏天洗澡掀波澜",
            "碟中谍4:阿汤哥高塔命悬一线,超越不可能",
            };

    NoticeView vNotice;
    NoticeView vNotice2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vNotice = (NoticeView) findViewById(R.id.notice);
        vNotice.start(Arrays.asList(notices));
        vNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, notices[vNotice.getIndex()], Toast.LENGTH_SHORT).show();
            }
        });


        vNotice2 = (NoticeView) findViewById(R.id.notice2);
        vNotice2.start(Arrays.asList(notices));
        vNotice2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, notices[vNotice.getIndex()], Toast.LENGTH_SHORT).show();
            }
        });


     }


    @Override
    public void onClick(View v) {
    }
}
