package com.example.calendar;

import androidx.appcompat.app.AppCompatActivity;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;


import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int mYear;
//    阴历，年份，月日，当前天数(右上角)
    TextView mTextLunar, mTextYear, mTextMonthDay, mTextCurrentDay;
    CalendarView calendarView;
    CalendarLayout calendarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);

        init_view();
        init_theme();
        init_listen();
    }

    private void init_view() {
        calendarView = findViewById(R.id.calendarView);
        calendarLayout = findViewById(R.id.calendarLayout);

        mTextLunar = findViewById(R.id.tv_lunar);
        mTextYear = findViewById(R.id.tv_year);
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextCurrentDay = findViewById(R.id.tv_current_day);

//        左右上角初始化显示
        mTextYear.setText(String.valueOf(calendarView.getCurYear()));
        mTextMonthDay.setText(calendarView.getCurMonth() + "月" + calendarView.getCurDay() + "日");
        mTextLunar.setText("今日");
        mTextCurrentDay.setText(String.valueOf(calendarView.getCurDay())); //右上角
        mYear = calendarView.getCurYear();
    }

    private void init_listen() {
//      监听日期改变
        calendarView.setOnCalendarSelectListener(new CalendarView.OnCalendarSelectListener() {
            @Override
            public void onCalendarOutOfRange(Calendar calendar) {

            }

            @Override
            public void onCalendarSelect(Calendar calendar, boolean isClick) {
                int year = calendar.getYear();
                int month = calendar.getMonth();
                int day = calendar.getDay();
                Log.e("Date change:", year + " -- " + month + " -- " + day);
                mTextLunar.setVisibility(View.VISIBLE);
                mTextYear.setVisibility(View.VISIBLE);
                mTextMonthDay.setText(month + "月" + day + "日");
                mTextYear.setText(String.valueOf(calendar.getYear()));
                mTextLunar.setText(calendar.getLunar());
                mYear = calendar.getYear();
            }
        });
//      监听月份改变
        calendarView.setOnMonthChangeListener(new CalendarView.OnMonthChangeListener() {
            @Override
            public void onMonthChange(int year, int month) {
                Calendar calendar = calendarView.getSelectedCalendar();
                Log.e("Month change:", year + " -- " + month);
                mTextLunar.setVisibility(View.VISIBLE);
                mTextYear.setVisibility(View.VISIBLE);
                mTextMonthDay.setText(month + "月" + calendar.getDay() + "日");
                mTextYear.setText(String.valueOf(calendar.getYear()));
                mTextLunar.setText(calendar.getLunar());
                mYear = calendar.getYear();
            }
        });
//        年份改变
        calendarView.setOnYearChangeListener(new CalendarView.OnYearChangeListener() {
            @Override
            public void onYearChange(int year) {
                mTextMonthDay.setText(String.valueOf(year));
                Log.e("Year change", String.valueOf(year));
            }
        });

        mTextMonthDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!calendarLayout.isExpand()) {
                    calendarLayout.expand();
                    return;
                }
                calendarView.showYearSelectLayout(mYear);
//                显示不可见
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });
    }
    private void init_theme() {
//        week 栏的背景和颜色
        calendarView.setWeeColor(0xFF0099cc, 0xFFccffcc);
//        calendarView.setTextColor(0xFF40db25, 0xFF40db25, 0xFF40db25, 0xFF40db25, 0xFF40db25);

//        (标点，右上角标注)
        Map<String, Calendar> map = new HashMap<>();
        String s = getSchemeCalendar(2022, 12, 10, 0xFFFFFFFF, "放假").toString();
        Log.e("Color: ", s);
        map.put(getSchemeCalendar(2022, 12, 10, 0xFFcc0000, "放假").toString(),
                getSchemeCalendar(2022, 12, 10, 0xFFcc0000, "放假"));
        map.put(getSchemeCalendar(2022, 12, 11, 0xFFcc0000, "放假").toString(),
                getSchemeCalendar(2022, 12, 11, 0xFFcc0000, "放假"));
        map.put(getSchemeCalendar(2022, 12, 12, 0xFFcc0000, "放假").toString(),
                getSchemeCalendar(2022, 12, 12, 0xFFcc0000, "放假"));
        calendarView.setSchemeDate(map);
        calendarView.removeSchemeDate(getSchemeCalendar(2022, 12, 11, 0xFFcc0000, "放假"));
    }

    private Calendar getSchemeCalendar(int year, int month, int day, int color, String text) {
        Calendar calendar = new Calendar();
        calendar.setYear(year);
        calendar.setMonth(month);
        calendar.setDay(day);
        calendar.setSchemeColor(color);//如果单独标记颜色、则会使用这个颜色
        calendar.setScheme(text);
        return calendar;
    }
}