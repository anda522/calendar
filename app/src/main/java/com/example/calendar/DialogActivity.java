package com.example.calendar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;

public class DialogActivity extends AppCompatActivity {

    Button cancel, ok;
    RadioGroup radioGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog);

        cancel = findViewById(R.id.tp_cancel);
        ok = findViewById(R.id.tp_ok);
        radioGroup = findViewById(R.id.rg);

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //返回并传回答复”0“
                Intent intent = new Intent();
                intent.setClassName("com.example.calendar","com.example.calendar.MainActivity");
                setResult(0,intent);
                finish();
            }
        });

        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //找出被选中选项的id
                int id = 0;
                for(int i = 0; i < radioGroup.getChildCount(); i++) { //获得被选中的id
                    RadioButton r = (RadioButton)radioGroup.getChildAt(i);
                    if(r.isChecked()) {
                        id = r.getId();
                        break;
                    }
                }
                //返回并传回答复id
                Intent intent = new Intent();
                intent.setClassName("com.example.calendar","com.example.calendar.MainActivity");
                setResult(id, intent);
                finish();
            }
        });

    }
}
