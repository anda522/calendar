package com.example.calendar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarLayout;
import com.haibin.calendarview.CalendarView;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private int mYear;
//    阴历，年份，月日，当前天数(右上角)
    TextView mTextLunar, mTextYear, mTextMonthDay, mTextCurrentDay;
    CalendarView calendarView;
    CalendarLayout calendarLayout;
    // 红 黄 蓝 橙 紫 绿 灰 粉 黄 褐
    int[] colors = {0xFFcc0000, 0xFFFF8C00, 0xFF3333ff, 0xFFFF7F00, 0xFF9900ff, 0xFF00ff33, 0xFF663300, 0xFFff0099, 0xFFFFD700, 0xFFCDAD00};

    EditText editText;
    TextView textView;
    Button edit_btn;
    Button del_btn;
    Button cancel_btn;
    Button save_btn;
    //  数据库对象
    SQLiteDatabase db;
    MyHelper myHelper;
    //  选中日期是否已经保存了备忘
    boolean flag;
    //  点击取消时闪回原有文本
    String flash_back;
    //  标注用map
    Map<String, Calendar> map;
    //  当前日期的标注
    String type;
    //  上个日期
    Calendar pre_calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.calendar_view);

        flag = false;
        flash_back = "";

        myHelper = new MyHelper(this);
        db = myHelper.getReadableDatabase();

        init_view();
        init_theme();
        init_listen();

        //获取当前日期
        Calendar calendar = calendarView.getSelectedCalendar();
        display(calendar);
        pre_calendar = calendar;
    }

    private void init_view() {
        calendarView = findViewById(R.id.calendarView);
        calendarLayout = findViewById(R.id.calendarLayout);

        mTextLunar = findViewById(R.id.tv_lunar);
        mTextYear = findViewById(R.id.tv_year);
        mTextMonthDay = findViewById(R.id.tv_month_day);
        mTextCurrentDay = findViewById(R.id.tv_current_day);

        editText = findViewById(R.id.edit_content);
        editText.setVisibility(View.GONE);
        textView = findViewById(R.id.show_content);

        edit_btn = findViewById(R.id.edit);
        del_btn = findViewById(R.id.del);
        cancel_btn = findViewById(R.id.cancel);
        save_btn = findViewById(R.id.save);

//        界面初始化显示
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

                //如果用户在上个日期设置标注后，突然切换日期，需要把上个日期的标注删掉
                //该操作需要在display之前，因为此时的flag还在记录上个日期
                if(!flag) {
                    Calendar d1 = getSchemeCalendar(pre_calendar.getYear(), pre_calendar.getMonth(), pre_calendar.getDay(), 0xFFcc0000, "");
                    calendarView.removeSchemeDate(d1);
                }
                //刷新显示
                display(calendar);
                //更新pre_calendar
                pre_calendar = calendar;
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
                Log.e("Action:", "---- expand -----" + calendarLayout.isExpand());
                if (!calendarLayout.isExpand()) {
                    edit_btn.setVisibility(View.GONE);
                    del_btn.setVisibility(View.GONE);
                    cancel_btn.setVisibility(View.GONE);
                    save_btn.setVisibility(View.GONE);
                    editText.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    calendarLayout.expand();
                    return;
                }
                edit_btn.setVisibility(View.GONE);
                del_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.GONE);
                save_btn.setVisibility(View.GONE);
                editText.setVisibility(View.GONE);
                textView.setVisibility(View.GONE);

                calendarView.showYearSelectLayout(mYear);
//                显示不可见
                mTextLunar.setVisibility(View.GONE);
                mTextYear.setVisibility(View.GONE);
                mTextMonthDay.setText(String.valueOf(mYear));
            }
        });

        edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //切换到编辑界面
                edit_btn.setVisibility(View.GONE);
                del_btn.setVisibility(View.GONE);
                cancel_btn.setVisibility(View.VISIBLE);
                save_btn.setVisibility(View.VISIBLE);
                flash_back = String.valueOf(editText.getText());
                editText.setVisibility(View.VISIBLE);
                textView.setVisibility(View.GONE);

                if(!flag) {
                    //打开弹窗
                    Intent intent = new Intent(MainActivity.this, com.example.calendar.DialogActivity.class);
                    startActivityForResult(intent,1);
                }
                //弹窗方法2
//                RelativeLayout dialog = (RelativeLayout) getLayoutInflater().inflate(R.layout.dialog, null);
//                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                builder.setView(dialog).create().show();
            }
        });

        del_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flag = false;
                flash_back = "";
                //获取当前选中日期
                Calendar calendar = calendarView.getSelectedCalendar();
                String date = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
                //从数据库删除
                if(db.delete("note","date=?",new String[]{date})>0)
                    Log.e("note","删除成功");
                //刷新显示和标注
                display(calendar);
            }
        });

        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前选中日期
                Calendar calendar = calendarView.getSelectedCalendar();
                //根据数据库还原
                display(calendar);
            }
        });

        save_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //获取当前文本
                String content = String.valueOf(editText.getText());
                //按钮显示切换
                edit_btn.setVisibility(View.VISIBLE);
                cancel_btn.setVisibility(View.GONE);
                save_btn.setVisibility(View.GONE);
                //获取当前选中日期
                Calendar calendar = calendarView.getSelectedCalendar();
                String date = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();

                if(!content.equals("")) { //若编辑后不为空
                    del_btn.setVisibility(View.VISIBLE); //删除键显示
                    flag = true;
                    //存储到数据库
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("date",date);
                    contentValues.put("content",content);
                    contentValues.put("type",type);
                    if(db.replace("note",null,contentValues) > 0) {
                        Log.e("note","插入成功");
                    }
                } else { //若编辑后为空
                    del_btn.setVisibility(View.GONE); //不显示删除
                    flag = false;
                    //从数据库删除
                    if(db.delete("note","date=?",new String[]{date}) > 0)
                        Log.e("note","删除成功");
                }

                //刷新显示,标注也会在此更新
                display(calendar);
            }
        });
    }

    //      初始化日期右上角标注
    private void init_theme() {
//        week 栏的背景和颜色
        calendarView.setWeeColor(0xFF0099cc, 0xFFccffcc);
        map = new HashMap<>();
        //设置已有标注
        String order = "select date, type from note";
        Cursor cursor = db.rawQuery(order,null);
        if(cursor.moveToFirst()) {
            do {
                String date = cursor.getString(0);
                String type = cursor.getString(1);
                String year = "", month = "", day = "";
                //获取年月日
                int k = 0;
                for(int i = 0; i < date.length(); i++) {
                    char ch = date.charAt(i);
                    if(ch == '-') {
                        k++;
                    } else {
                        if(k == 0) year += ch;
                        if(k == 1) month += ch;
                        if(k == 2) day +=ch;
                    }
                }
                Log.e("year month day",year + month + day);
                //放入map
                Calendar d1 = getSchemeCalendar(Integer.valueOf(year), Integer.valueOf(month), Integer.valueOf(day), random_color(), type);
                map.put(d1.toString(), d1);
            } while (cursor.moveToNext());
        }
        //设置标注
        calendarView.setSchemeDate(map);
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

//    文本和按钮显示
    private void display(Calendar calendar)
    {
        String date = calendar.getYear() + "-" + calendar.getMonth() + "-" + calendar.getDay();
        Cursor cursor = db.rawQuery("select content,type from note where date='" + date + "'",null);

        if(cursor.moveToFirst()) {
            flag = true;
            String content = cursor.getString(0);
            type = cursor.getString(1);
            Log.e("content",content);
            Log.e("type",type);
            editText.setText(content);
            textView.setText(content);
            textView.setTextColor(0xFF000000); // 设置为黑色
            del_btn.setVisibility(View.VISIBLE);
            //满足刷新标注需要
            Calendar d1 = getSchemeCalendar(calendar.getYear(), calendar.getMonth(), calendar.getDay(), random_color(), type);
            map.put(d1.toString(), d1);
            calendarView.setSchemeDate(map);
        } else {
            Log.e("content","为空");
            flag = false;
//            flash_back = "";
            editText.setText("");
            textView.setText("准备做什么？");
            textView.setTextColor(0xFF808080); // 设置为灰色
            del_btn.setVisibility(View.GONE);
            //满足刷新标注需要
            Calendar d1 = getSchemeCalendar(calendar.getYear(), calendar.getMonth(), calendar.getDay(), 0xFFcc0000, "");
            calendarView.removeSchemeDate(d1);
        }
        editText.setVisibility(View.GONE);
        textView.setVisibility(View.VISIBLE);
        edit_btn.setVisibility(View.VISIBLE);
        cancel_btn.setVisibility(View.GONE);
        save_btn.setVisibility(View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1) {
            //获取当前选中日期
            Calendar calendar = calendarView.getSelectedCalendar();
            //用户点击取消
            if(resultCode == 0) {
                display(calendar);
                return;
            }
            switch(resultCode) {
                case R.id.work:
                    type = "工作"; break;
                case R.id.study:
                    type = "学习"; break;
                case R.id.activity:
                    type = "活动"; break;
                default:
                    type = "其他"; break;
            }
            Calendar d1 = getSchemeCalendar(calendar.getYear(), calendar.getMonth(), calendar.getDay(), random_color(), type);
            map.put(d1.toString(), d1);
            calendarView.setSchemeDate(map);
        }
    }

    private int random_color() {
        int index = (int)(Math.random() * colors.length);
        return colors[index];
    }
}