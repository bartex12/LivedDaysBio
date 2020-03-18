package ru.bartex.jubileedates;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    public final String TAG = "33333";

    TextView willBe, lastDays;
    EditText day, mounth, year, days;
    Button findDate;

    int dayNumber,mounthNumber, yearNumber, daysNumber;

    int dayNumberNext,mounthNumberNext, yearNumberNext;

    private Calendar firstCalendar;

    private Timer mTimer;
    private TimerTask mTimerTask;

    long nowTimeMillis;
    long firstCalendarMillis;
    long beenDays;
    long beenDays1;
    private long mKvant = 100;//время в мс между срабатываниями TimerTask

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        willBe = (TextView)findViewById(R.id.textViewWillBe);
        lastDays = (TextView)findViewById(R.id.textViewLastDays);
        day = (EditText)findViewById(R.id.editTextDay);
        mounth = (EditText)findViewById(R.id.editTextMonth);
        year = (EditText)findViewById(R.id.editTextYear);
        days = (EditText)findViewById(R.id.editTextDays);
        findDate = (Button)findViewById(R.id.buttonFind);

        //Слушатель на кнопку
        findDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean temp1 = getDay();
                boolean temp2 = getMounth();
                boolean temp3 = getYear();
                boolean temp4 = getDays();

                Log.d(TAG, "Введённая дата = " + dayNumber + "." +
                        (mounthNumber) + "." + yearNumber + "." + daysNumber);

                if (temp1 && temp2 && temp3 && temp4) {
                    //экземпляр календаря с данными с экрана
                    firstCalendar = new GregorianCalendar(yearNumber, mounthNumber - 1, dayNumber);
                    //получаем дату в милисекундах
                    firstCalendarMillis = firstCalendar.getTimeInMillis();
                    //Добавляем к указанной на экране дате указанное количество дней
                    firstCalendar.add(Calendar.DAY_OF_YEAR, daysNumber);
                    //получаем день месяца расчётной даты
                    dayNumberNext = firstCalendar.get(Calendar.DAY_OF_MONTH);
                    //получаем месяц расчётной даты
                    mounthNumberNext = firstCalendar.get(Calendar.MONTH);
                    //получаем год расчётной даты
                    yearNumberNext = firstCalendar.get(Calendar.YEAR);
                    Log.d(TAG, "Расчётная дата = " + dayNumberNext + "." +
                            (mounthNumberNext + 1) + "." + yearNumberNext);

                    if (mTimer != null) mTimer.cancel();
                    mTimer = new Timer();
                    mTimerTask = new myTimerTask();
                    //запускаем задачу по расписанию = контроль каждые mKvant (100мс)
                    mTimer.scheduleAtFixedRate(mTimerTask, mKvant, mKvant);
                }
            }
        });
    }

    public class myTimerTask extends TimerTask{
        @Override
        public void run() {
            //текущеевремя
            nowTimeMillis = System.currentTimeMillis();
            //количество прошедших дней с даты рождения
            beenDays = (nowTimeMillis-firstCalendarMillis)/86400000;
            //количество долей текущего дня
            beenDays1 = (nowTimeMillis-firstCalendarMillis)%86400000*10/864;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    //показываем результаты в пользовательском потоке каждые 100мс
                    String s = String.format("%05d.%06d",beenDays,beenDays1);
                    lastDays.setText(s);
                    String s1 = String.format("%02d.%02d.%04d",
                            dayNumberNext, mounthNumberNext + 1, yearNumberNext);
                    willBe.setText(s1);
                }
            });
        }
    }

//=================================Функции====================================//

    boolean getDay(){
        boolean d;
        if (day.getText().toString().equals("")) {
            dayNumber = 0;
            d = false;
            Toast.makeText(MainActivity.this,
                    "Введите день рождения", Toast.LENGTH_SHORT).show();
        }else {
            int i = Integer.parseInt(day.getText().toString());
            if (i>0 && i<=31) {
                dayNumber = i;
                d = true;
            }else {
                d = false;
                Toast.makeText(MainActivity.this,
                        "Число в диапазоне 1-31", Toast.LENGTH_SHORT).show();
            }
        }
        return d;
    }

    boolean getMounth(){
        boolean m;
        if (mounth.getText().toString().equals("")) {
            mounthNumber = 0;
            m = false;
            Toast.makeText(MainActivity.this,
                    "Введите месяц рождения", Toast.LENGTH_SHORT).show();
        }else {
            int i = Integer.parseInt(mounth.getText().toString());
            if (i>=1 && i<=12) {
                mounthNumber = i;
                m = true;
            }else {
                m = false;
                Toast.makeText(MainActivity.this,
                        "Число в диапазоне 1-12", Toast.LENGTH_SHORT).show();
            }
        }
        return m;
    }

    boolean getYear(){
        boolean y;
        if (year.getText().toString().equals("")) {
            yearNumber = 0;
            y = false;
            Toast.makeText(MainActivity.this,
                    "Введите год рождения", Toast.LENGTH_SHORT).show();
        }else {

            int i = Integer.parseInt(year.getText().toString());
            if (i>=1900 && (i<= (new GregorianCalendar()).get(Calendar.YEAR))) {
                //Log.d(TAG,"YEAR = " + (new GregorianCalendar()).get(Calendar.YEAR));
                yearNumber = i;
                y = true;
            }else {
                y = false;
                Toast.makeText(MainActivity.this,
                        "Число от 1900 до " + (new GregorianCalendar()).get(Calendar.YEAR),
                        Toast.LENGTH_SHORT).show();
            }
        }
        return y;
    }

    boolean getDays (){
        boolean ds;
        if (days.getText().toString().equals("")) {
            daysNumber = 0;
            ds = false;
            Toast.makeText(MainActivity.this,
                    "Введите желаемое число прожитых дней", Toast.LENGTH_SHORT).show();
        }else {

            int i = Integer.parseInt(days.getText().toString());
            if (i>=0 && i<= 50000){
                daysNumber = i;
                ds = true;
            }else {
                ds = false;
                Toast.makeText(MainActivity.this,
                        "Число от 0 до 50000",Toast.LENGTH_SHORT).show();
            }
        }
        return ds;
    }


}
