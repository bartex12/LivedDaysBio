package ru.bartex.jubelee_dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;

import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.GregorianCalendar;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class BioritmActivity extends AppCompatActivity {

    public static final String ID_SQL = "sqlBioritmActivity"; // id  строки даных
    public static final String ID_SQL_PREF = "sql_pref_BioritmActivity"; // id  строки даных в Preferences
    public static final String ID_SQL_PREF_NAME = "sql_pref_name_BioritmActivity";

    private Calendar firstCalendar;

    //количество точек на кривой графика
     static int n = 30;

    //Добавка в днях на графике (сдвиг графика)
    static int deltaPlus;

    //день месяца, месяц и год  расчётной даты
    int dayNumberNext,mounthNumberNext, yearNumberNext;

    // дата в миллисекундах
    long firstCalendarMillis;
    //текущее время в миллисекундах
    long nowTimeMillis;
    //количество прошедших дней с даты рождения
    long beenDays;

    static int levelF=0;
    static int levelA=0;
    static int levelI=0;

    public static final String TAG = "33333";
    Button mButtonPlus10;
    Button mButtonMinus10;
    Button mButtonPlus1;
    Button mButtonMinus1;
    static Button buttonDateForCalculate;
    Button buttonNameForBioritm;

    ImageButton mImageButtonRevert;

    //поля ввода для дня, месяца и года рождения

    static TextView mTextViewFiz;
    static  TextView mTextViewAmo;
    static TextView mTextViewInt;

    static GraphView graph;

    static String userName;
    static int dayNumber = 17;
    static int mounthNumber = 5;
    static int yearNumber = 1961;

    private SharedPreferences prefSetting;  //из файла настроек
    private SharedPreferences second_ID;//настройки этой активности - предыдущее ID

    long id_sql;  //id строки с данными в базе данных
    long id_sql_second;  //id строки при предыдущем вхождении

    File file;
    View main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED,
                WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED);

        setContentView(R.layout.activity_bioritm);


        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);

        Log.d(TAG, "BioritmActivity onCreate");
        //Чтобы работало,нужно в onOptionsItemSelected сделать case android.R.id.home:
        //Но всё равно BioritmActivity встаёт в onStop, а не в onDestroy, поэтому при выходе
        //из программы попадаем опять в BioritmActivity - видимо это особенность работы
        //через ActionBar - придётся стрелку пока убрать
        //если добавить в  case android.R.id.home: onBackPressed() -всё работает

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true );
        act.setHomeButtonEnabled(true);
        //act.setLogo(R.drawable.my_logo);
        graph =  findViewById(R.id.graph);

        main = findViewById(R.id.layout);

        Log.d(TAG, "imageView isHardwareAccelerated= " + graph.isHardwareAccelerated());
        //аппаратное ускорение отключено для отдельного представления(в соотв с реком Гугл надо вкл)
        //graph.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mTextViewFiz = findViewById(R.id.textViewFiz);
        mTextViewAmo =  findViewById(R.id.textViewAmo);
        mTextViewInt =  findViewById(R.id.textViewInt);

        //получаем интент
        Intent intent = getIntent();
        //получаем id строки с данными в базе
        id_sql = intent.getLongExtra(ID_SQL,0);

        //получаем Preferences с id предыдущего вхождения в этот экран
        second_ID = getSharedPreferences(ID_SQL_PREF_NAME, MODE_PRIVATE);
        //получаем id предыдущего вхождения в этот экран - если их нет, приравниваем к текущему id
        id_sql_second = second_ID.getLong(ID_SQL_PREF, id_sql);

        // проверяем, существует ли такая запись, так как
        //если на главном экране была удалена строка с id_sql_second, будет крах
        PersonDbHelper mDbHelper = new PersonDbHelper(this);
        boolean isPersonExist = mDbHelper.isPersonExist(id_sql_second);
        if (!isPersonExist){
            //если такой записи уже нет, то
            id_sql_second = id_sql;
        }
        Log.d(TAG, "mDbHelper.isPersonExist =  " + isPersonExist + "  id_sql_second = " + id_sql_second);

        //получаем экхземпляр PersonDbHelper
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        //получаем массив строк с данными по персоне
        //String[] data = mPersonDbHelper.getPersonData(id_sql);
        Person person = mPersonDbHelper.getPersonObjectData(id_sql);

        //присваиваем переменным значения из массива
        //userName = data[0];
        //dayNumber = Integer.parseInt(data[1]);
       // mounthNumber  = Integer.parseInt(data[2]);
        //yearNumber   = Integer.parseInt(data[3]);

        userName = person.getPerson_name();
        dayNumber = Integer.parseInt(person.getPerson_day());
        mounthNumber = Integer.parseInt(person.getPerson_month());
        yearNumber   = Integer.parseInt(person.getPerson_year());

        //обнуляем deltaPlus, чтобы при возврате из списка персон с другой персоной
        //на кнопке отражалась текущая дата и биоритмы начинались с нее
        deltaPlus = 0;
        Log.d(TAG, "deltaPlus = " + deltaPlus);

        //Пишем имя на кнопке
        buttonNameForBioritm =  findViewById(R.id.buttonNameForBioritm);
        buttonNameForBioritm.setText(userName);
        buttonNameForBioritm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //onBackPressed();  //так было, когда список был лаунчером
                Intent intent = new Intent(BioritmActivity.this, PersonsListActivity.class);
                intent.putExtra(P.FROM_MAIN, P.TO_BIORITM);
                startActivity(intent);
            }
        });

        //Вычисляем дату начала биоритмов
        buttonDateForCalculate =  findViewById(R.id.buttonDateForCalculate);
        buttonDateForCalculate.setText(showDateForCalculate());
        buttonDateForCalculate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager manager = getFragmentManager();
                DialogFragment dialogFragment = new DateBioritmFragment();
                dialogFragment.show(manager,"DatePicker");

            }
        });

        mButtonPlus10 =  findViewById(R.id.buttonPlus10);
        mButtonPlus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaPlus += 10;
                Log.d(TAG, "deltaPlus = " + deltaPlus);
                graph.removeAllSeries();
                showGraf(yearNumber,mounthNumber,dayNumber, deltaPlus, n);
                buttonDateForCalculate.setText(showDateForCalculate());
            }
        });

        mButtonPlus1 =  findViewById(R.id.buttonPlus1);
        mButtonPlus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaPlus += 1;
                Log.d(TAG, "deltaPlus = " + deltaPlus);
                graph.removeAllSeries();
                showGraf(yearNumber,mounthNumber,dayNumber, deltaPlus, n);
                buttonDateForCalculate.setText(showDateForCalculate());
            }
        });

        mButtonMinus10 =  findViewById(R.id.buttonMinus10);
        mButtonMinus10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaPlus += -10;
                Log.d(TAG, "deltaPlus = " + deltaPlus);
                graph.removeAllSeries();
                showGraf(yearNumber,mounthNumber,dayNumber, deltaPlus, n);
                buttonDateForCalculate.setText(showDateForCalculate());
            }
        });

        mButtonMinus1 =  findViewById(R.id.buttonMinus1);
        mButtonMinus1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaPlus += -1;
                Log.d(TAG, "deltaPlus = " + deltaPlus);
                graph.removeAllSeries();
                showGraf(yearNumber,mounthNumber,dayNumber, deltaPlus, n);
                buttonDateForCalculate.setText(showDateForCalculate());
            }
        });

        mImageButtonRevert =   findViewById(R.id.imageButtonRevert);
        mImageButtonRevert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deltaPlus = 0;
                Log.d(TAG, "deltaPlus = " + deltaPlus);
                graph.removeAllSeries();
                showGraf(yearNumber,mounthNumber,dayNumber, deltaPlus, n);
                buttonDateForCalculate.setText(showDateForCalculate());
            }
        });

        showGraf(yearNumber,mounthNumber,dayNumber, deltaPlus, n);

        //Подсветка нулевой линии
        //graph.getGridLabelRenderer ().setHighlightZeroLines(true);
        //Подпись оси Х
        //graph.getGridLabelRenderer ().setHorizontalAxisTitle("Январь");
        //Видимость метки оси Х
        //graph.getGridLabelRenderer ().setHorizontalLabelsVisible(true);
        //Отступ меток от линии оси
        // graph.getGridLabelRenderer ().setLabelsSpace(0);
        // enable scaling and scrolling
        //graph.getViewport().setScalable(true);
        //graph.getViewport().setScalableY(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "BioritmActivity onResume");
        n = Integer.parseInt(prefSetting.getString("List","15"));
        //перерисовываем график после изменения числа точек на графике
        showGraf(yearNumber,mounthNumber,dayNumber, deltaPlus, n);
        Log.d(TAG, "BioritmActivity n = " + n);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "BioritmActivity onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "BioritmActivity onStop");
        //this.onDestroy();
        //здесь можно сбросить расчётную дату в текущую
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //сохраняем id  персоны последнего вхождения
        SharedPreferences.Editor e = second_ID.edit();
        e.putLong(ID_SQL_PREF, id_sql);
        e.commit();
        Log.d(TAG, "BioritmActivity onDestroy");
    }



    //вызов диалогового окна при нажатии на кнопку Назад
    @Override
    public void onBackPressed() {
        Log.d(TAG, "BioritmActivity onBackPressed");
        //Intent intent = new Intent(BioritmActivity.this, PersonsListActivity.class);
        //startActivity(intent);
        //finish();
        super.onBackPressed();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bioritm,menu);
        /*
        //если делать через shareActionProvider, в 4.0.4 не работает,
        //придётся через меню case R.id.action_share: разница только в способе обработки
        MenuItem item = menu.findItem(R.id.action_share);
        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(item);
        setNewIntent("This is example text");
        */
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //чтобы работала стрелка Назад к экрану, указанному в манифесте
            case android.R.id.home:
                Log.d(TAG, "Домой");
                Intent intentHome = new Intent(this, MainActivity.class);
                intentHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                startActivity(intentHome);
                finish();
                return true;

            case R.id.action_settings:
                Log.d(TAG, "action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;

            case R.id.action_time:
                Log.d(TAG, "action_time");
                Intent intentDays = new Intent(this,TimeActivity.class);
                intentDays.putExtra(TimeActivity.ID_SQL,id_sql);
                startActivity(intentDays);
                return true;

            case R.id.action_table:
                Log.d(TAG, "action_table");
                Intent intent = new Intent(BioritmActivity.this,TableActivity.class);
                intent.putExtra(TableActivity.ID_SQL,id_sql);
                startActivity(intent);
                return true;

            case R.id.action_joint:
                Log.d(TAG, "OptionsItem = action_joint");
                Intent intentJoint = new Intent(this, JointActivity.class);
                intentJoint.putExtra(JointActivity.ID_SQL, id_sql);
                intentJoint.putExtra(JointActivity.ID_SQL_second, id_sql_second);
                startActivity(intentJoint);
                return true;

            case R.id.action_share:
                //так работает размер 37К
                Bitmap bitmap = takeScreenshot(main);
                Log.d(TAG, "OptionsItem = action_share getByteCount = " + bitmap.getByteCount());
                //так тоже работает размер 42К
                //Bitmap bm = screenShot(this);
                //file = saveBitmap(bm, "image1.png");

                file = takeScreenshot159(bitmap, "savedBitmap.png");
                Log.d(TAG, "AbsolutePath: " + file.getAbsolutePath());

                Uri uri = Uri.fromFile(new File(file.getAbsolutePath()));
                Log.d(TAG, "uri: " + uri);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Посмотрите ка на это!");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "share via"));
                return true;

            case R.id.action_help_bioritm:
                Log.d(TAG, "OptionsItem = action_action_bioritm");
                Intent intentbioritm = new Intent(this, HelpActivity.class);
                intentbioritm.putExtra(P.HELP_FROM, P.HELP_FROM_BIORITM);
                startActivity(intentbioritm);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    //=======================================================

    private File takeScreenshot159(Bitmap bitmap,String fileName ) {

        //так работает для андроид 7 но не для андроид 4 - крах
        File path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(!path.exists()) {
            path.mkdirs();
        }

        File file = new File(path, fileName);
        try {
            FileOutputStream fos = null;
            // Make sure the Pictures directory exists.

            Log.d(TAG, " До File file = " + file.length() + " isFile: " + file.isFile());
            try {
                fos = new FileOutputStream(file);
                Log.d(TAG, "FileOutputStream fos  = " + fos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Log.d(TAG, " После File file = " + file.length() +
                        "  isFile: " + file.isFile());
                return file;
            } finally {
                if (fos != null) fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Ошибка try");
        }
        Log.d(TAG, "return null");
        return null;
    }

    public Bitmap takeScreenshot(View v){
        v = v.getRootView();
        v.setDrawingCacheEnabled(true);
        v.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(v.getDrawingCache());
        v.setDrawingCacheEnabled(false);
        return bitmap;
    }

    //послать сообщение при нажатии на пункт меню экшнБара action_share
    private void setNewIntent(String text) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, text);
        //так как shareActionProvider в 4.0.4 работает не правильно, делаем через  case R.id.action_share:
        //shareActionProvider.setShareIntent(intent);
        startActivity(Intent.createChooser(intent, "Отправить через"));
    }

    public static class DateBioritmFragment extends NewActivity.DatePickerFragment{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            //return super.onCreateDialog(savedInstanceState);
            Calendar cal = Calendar.getInstance();
            int myYear = cal.get(Calendar.YEAR);
            int myMonth = cal.get(Calendar.MONTH);
            int myDay = cal.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, myYear,myMonth, myDay);
        }

        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            //super.onDateSet(view, year, month, dayOfMonth);
            //Обнуляем , чтобы не было ошибок при провторной установке даты на календаре
            deltaPlus = 0;
            //выводим дату на кнопку
            String namesDateShort = String.format("%s.%s.%s", dayOfMonth, month +1, year);
            buttonDateForCalculate.setText(namesDateShort);
            //Удаляем предыдущие графики
            graph.removeAllSeries();
            //строим графики и возвращаем разницу между выбранной датой и сейчас
            deltaPlus += showGrafFromBornToChoose(
                    yearNumber, mounthNumber,dayNumber,
                    year, month, dayOfMonth,
                    0,n);
        }
    }

    //расчёт текущей даты графика для отображения на кнопке выбора даты
    private String showDateForCalculate(){
        //экземпляр календаря с данными  интента
        firstCalendar = new GregorianCalendar(yearNumber, mounthNumber-1, dayNumber);
        //Log.d(TAG, "DAY_OF_MONTH firstCalendar = " + firstCalendar.get(Calendar.DAY_OF_MONTH));
        //получаем дату в милисекундах
        firstCalendarMillis = firstCalendar.getTimeInMillis();
        //текущее время
        nowTimeMillis = System.currentTimeMillis();
        //количество прошедших дней с даты рождения - в сутках 86400 секунд = 3600*24
        beenDays = (nowTimeMillis-firstCalendarMillis)/86400000 + deltaPlus;
        //количество долей текущего дня
        //beenDays1 = (nowTimeMillis-firstCalendarMillis)%86400000*10/864;

        //вычисляем дату Д.Р + кол прож дней, т.е. СЕГОДНЯ
        firstCalendar.add(Calendar.DAY_OF_YEAR, (int)beenDays);
        //получаем день месяца расчётной даты
        dayNumberNext = firstCalendar.get(Calendar.DAY_OF_MONTH);
        //получаем месяц расчётной даты
        mounthNumberNext = firstCalendar.get(Calendar.MONTH);
        //получаем год расчётной даты
        yearNumberNext = firstCalendar.get(Calendar.YEAR);

        return  String.format("%s.%s.%s",
                dayNumberNext, mounthNumberNext +1, yearNumberNext);
    }

    //метод расчёта графика на текущую дату
    private static void showGraf(int yearNumber, int mounthNumber, int dayNumber, int  deltaPlus, int n){
        //экземпляр календаря с данными интента
        Calendar firstCalendar = new GregorianCalendar(yearNumber, mounthNumber-1, dayNumber);
        //Log.d(TAG, "DAY_OF_MONTH firstCalendar = " + firstCalendar.get(Calendar.DAY_OF_MONTH));
        //получаем дату в милисекундах
        long firstCalendarMillis = firstCalendar.getTimeInMillis();
        //текущее время
        long nowTimeMillis = System.currentTimeMillis();
        //количество прошедших дней с даты рождения - в сутках 86400 секунд = 3600*24
        long beenDays = (nowTimeMillis-firstCalendarMillis)/86400000 + deltaPlus;
        //количество долей текущего дня
        //long beenDays1 = (nowTimeMillis-firstCalendarMillis)%86400000*10/864;

        //вычисляем дату Д.Р + кол прож дней, т.е. СЕГОДНЯ
        firstCalendar.add(Calendar.DAY_OF_YEAR, (int)beenDays);
        //получаем день месяца расчётной даты
        final int dayNumberNext = firstCalendar.get(Calendar.DAY_OF_MONTH);
        //получаем месяц расчётной даты
        final int mounthNumberNext = firstCalendar.get(Calendar.MONTH);
        //получаем год расчётной даты
        final int yearNumberNext = firstCalendar.get(Calendar.YEAR);
        Log.d(TAG, "Расчётная дата BioritmActivity = " + dayNumberNext + "." +
                (mounthNumberNext + 1) + "." + yearNumberNext);
        //создаём массив точек с координатами Х и Y
        DataPoint[] pointsF = new DataPoint[n];
        DataPoint[] pointsA = new DataPoint[n];
        DataPoint[] pointsI = new DataPoint[n];

        for (int i = 0; i < n; i++) {
            pointsF[i] = new DataPoint(dayNumberNext +i,
                    Math.sin(2*3.14159265358979*(beenDays+i)/23.688437) * 100);
            pointsA[i] = new DataPoint(dayNumberNext +i,
                    Math.sin(2*3.14159265358979*(beenDays+i)/28.426125) * 100);
            pointsI[i] = new DataPoint(dayNumberNext +i,
                    Math.sin(2*3.14159265358979*(beenDays+i)/33.163812) * 100);
        }

        //Пользовательские метки для оси Х (название месяца)
        final String[] numberMonth = {"янв","фев","мар","апр","май","июн","июл","авг",
                "сен","окт","ноя","дек",};
        // Создание метки для оси Х с учётом месяца
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    //если месяцы с 31 днём (месяцы в календаре начинатся с 0)
                    if ((mounthNumberNext == 0)||(mounthNumberNext == 2)||(mounthNumberNext == 4)||
                            (mounthNumberNext == 6)||(mounthNumberNext == 7)||(mounthNumberNext == 9)
                            ||(mounthNumberNext == 11)){
                        //если номер дня в ряду дней меньше 31
                        if (value <= 31) {
                            // то  значения по оси Х определяем так
                            return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                            //если номер дня в ряду дней больше 31, то следующий месяц определяем так
                        }else
                            return super.formatLabel(value - 31,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];
                        //если месяцы с 30 днями (месяцы в календаре начинатся с 0)
                    } else if ((mounthNumberNext == 3)||(mounthNumberNext == 5)||(mounthNumberNext == 8)||
                            (mounthNumberNext == 10)){
                        //если номер дня в ряду дней меньше 30
                        if (value <= 30) {
                            // то значения по оси Х определяем так
                            return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                            //если номер дня в ряду дней больше 30, то следующий месяц определяем так
                        }else
                            return super.formatLabel(value - 30,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];

                        //если февраль (месяцы в календаре начинатся с 0)
                    }else if (mounthNumberNext == 1) {
                        //Log.d(TAG, "mounthNumberNow= " + mounthNumberNext );
                        //если високосный год
                        if (((yearNumberNext % 4 == 0) && !(yearNumberNext % 100 == 0))||(yearNumberNext % 400 == 0)){
                            //если номер дня в ряду дней меньше 29
                            if (value <= 29) {
                               // Log.d(TAG, "value <= 29= " + value );
                                // то значения по оси Х определяем так
                                return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                            }else return super.formatLabel(value - 29,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];
                            //если НЕ високосный год
                        }else
                            //если номер дня в ряду дней меньше 28
                            if (value <= 28) {
                               // Log.d(TAG, "value <= 28= " + value );
                                // то значения по оси Х
                                return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                                //если номер дня в ряду дней больше 28, то следующий месяц определяем так
                            }else return super.formatLabel(value - 28,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];
                        //если не числа от 0 до 11, то
                    }else return super.formatLabel(value, isValueX);
                    //если НЕ числа по оси Х
                } else
                    return super.formatLabel(value, isValueX);
            }
        });

        //Создаём ряды данных
        LineGraphSeries<DataPoint> seriesF = new LineGraphSeries<>(pointsF);
        LineGraphSeries<DataPoint> seriesA = new LineGraphSeries<>(pointsA);
        LineGraphSeries<DataPoint> seriesI = new LineGraphSeries<>(pointsI);
        //Создаём метку в точке "Сегодня"
        LineGraphSeries<DataPoint> seriesNow = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(dayNumberNext, -100),
                new DataPoint(dayNumberNext, 100)
        });

        //Заголовки в легенде - чтобы были видны, её надо показать)
        // seriesF.setTitle("Curve 1");
        //seriesA.setTitle("Curve 2");
        //seriesI.setTitle("Curve 3");
        //graph.getLegendRenderer () . setVisible ( true );

        //Цвет линий
        seriesF.setColor(Color.BLUE);
        seriesA.setColor(Color.RED);
        seriesI.setColor(Color.GREEN);
        seriesNow.setColor(Color.BLACK);
        //ручная установка параметров
        seriesF.setDrawDataPoints(true);
        seriesA.setDrawDataPoints(true);
        seriesI.setDrawDataPoints(true);
        seriesNow.setDrawDataPoints(true);
        //радиус точки
        seriesF.setDataPointsRadius(8);
        seriesA.setDataPointsRadius(8);
        seriesI.setDataPointsRadius(8);
        seriesNow.setDataPointsRadius(5);
        // толщина линии
        seriesF.setThickness(5);
        seriesA.setThickness(5);
        seriesI.setThickness(5);
        seriesNow.setThickness(5);
        // добавление на график порции данных
        graph.addSeries(seriesF);
        graph.addSeries(seriesA);
        graph.addSeries(seriesI);
        graph.addSeries(seriesNow);

        // Уровни в % для каждого цикла на сегодня
        levelF = (int) Math.round(pointsF[0].getY());
        levelA = (int) Math.round(pointsA[0].getY());
        levelI = (int) Math.round(pointsI[0].getY());
        //показываем уровни на экране
        mTextViewFiz.setText("" + levelF);
        mTextViewAmo.setText("" + levelA);
        mTextViewInt.setText("" + levelI);

        //Log.d(TAG, "Сегодня " + "Физ =" + levelF + " Эмоц = " +
          //      levelA + "  Интел =" + levelI);
        //Название графика
        //graph.setTitle ("Биоритмы");


        //количество меток на оси Х
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        //название оси х
       // graph.getGridLabelRenderer().setHorizontalAxisTitle("djdfmvmf");
        //расстояние надписей от оси х
        graph.getGridLabelRenderer().setLabelsSpace(0);
        //размер текста надписей
        graph.getGridLabelRenderer().setTextSize(50);
        // ручная установка верт пределов
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-120);
        graph.getViewport().setMaxY(120);
        //  ручная установка горизонт пределов
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(dayNumberNext);
        graph.getViewport().setMaxX(dayNumberNext + n);
        // разрешение горизонт прокрутки
        //graph.getViewport().setScrollable(true);
    }

    //метод расчёта графика биоритмов на выбранную в календаре дату
    private static long showGrafFromBornToChoose(
            int yearBornNumber, int mounthBornNumber, int dayBornNumber,
            int yearChooseDateNumber, int mounthChooseDateNumber, int dayChooseDateNumber,
            int  deltaPlus, int n){
        //экземпляр календаря с данными интента (mounthBornNumber-1)!!!так как в интенте месяцы с 1
        Calendar firstCalendar = new GregorianCalendar(
                yearBornNumber, mounthBornNumber-1, dayBornNumber);
        //получаем дату в милисекундах
        long firstCalendarMillis = firstCalendar.getTimeInMillis();
        //экземпляр календаря с данными из календаря (mounthChooseDateNumber)!!!
        Calendar secondCalendar = new GregorianCalendar(
                yearChooseDateNumber, mounthChooseDateNumber, dayChooseDateNumber);
        //получаем дату в милисекундах
        long secondCalendarMillis = secondCalendar.getTimeInMillis();
        //количество прошедших дней с даты рождения - в сутках 86400 секунд = 3600*24
        long beenDays = (secondCalendarMillis-firstCalendarMillis)/86400000 + deltaPlus;
        //количество долей текущего дня
       // long beenDays1 = (secondCalendarMillis-firstCalendarMillis)%86400000*10/864;

        //Вспомогательный календарь
        Calendar tempCalendar = new GregorianCalendar();
        int dayNumberNow = tempCalendar.get(Calendar.DAY_OF_MONTH);
        int mounthNumberNow = tempCalendar.get(Calendar.MONTH);
        int yearNumberNow = tempCalendar.get(Calendar.YEAR);
        //Календарь по текущей дате без учёта времени
        //экземпляр календаря с данными из календаря (mounthNumberNow)!!!
        Calendar nowCalendar = new GregorianCalendar(yearNumberNow,mounthNumberNow,dayNumberNow);
        //переводим в миллисекунды
        long nowCalendarMillis = nowCalendar.getTimeInMillis();
        //разница в днях между текущей и выбранной датой
        long  deltaDays =(secondCalendarMillis - nowCalendarMillis)/86400000;
        Log.d(TAG, "deltaDays second-now = " + deltaDays );

        //вычисляем дату Д.Р + кол прож дней
        firstCalendar.add(Calendar.DAY_OF_YEAR, (int)beenDays);
        //получаем день месяца расчётной даты
        final int dayNumberNext = firstCalendar.get(Calendar.DAY_OF_MONTH);
        //получаем месяц расчётной даты
        final int mounthNumberNext = firstCalendar.get(Calendar.MONTH);
        //получаем год расчётной даты
        final int yearNumberNext = firstCalendar.get(Calendar.YEAR);
        Log.d(TAG, "Расчётная дата BioritmActivity = " + dayNumberNext + "." +
                (mounthNumberNext + 1) + "." + yearNumberNext);
        //создаём массив точек с координатами Х и Y
        DataPoint[] pointsF = new DataPoint[n];
        DataPoint[] pointsA = new DataPoint[n];
        DataPoint[] pointsI = new DataPoint[n];

        for (int i = 0; i < n; i++) {
            pointsF[i] = new DataPoint(dayNumberNext +i,
                    Math.sin(2*3.14159265358979*(beenDays+i)/23.688437) * 100);
            pointsA[i] = new DataPoint(dayNumberNext +i,
                    Math.sin(2*3.14159265358979*(beenDays+i)/28.426125) * 100);
            pointsI[i] = new DataPoint(dayNumberNext +i,
                    Math.sin(2*3.14159265358979*(beenDays+i)/33.163812) * 100);
        }

        //Пользовательские метки для оси Х (название месяца)
        final String[] numberMonth = {"янв","фев","мар","апр","май","июн","июл","авг",
                "сен","окт","ноя","дек",};
        // Создание метки для оси Х с учётом месяца
        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    //если месяцы с 31 днём (месяцы в календаре начинатся с 0)
                    if ((mounthNumberNext == 0)||(mounthNumberNext == 2)||(mounthNumberNext == 4)||
                            (mounthNumberNext == 6)||(mounthNumberNext == 7)||(mounthNumberNext == 9)
                            ||(mounthNumberNext == 11)){
                        //если номер дня в ряду дней меньше 31
                        if (value <= 31) {
                            // то  значения по оси Х определяем так
                            return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                            //если номер дня в ряду дней больше 31, то следующий месяц определяем так
                        }else
                            return super.formatLabel(value - 31,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];
                        //если месяцы с 30 днями (месяцы в календаре начинатся с 0)
                    } else if ((mounthNumberNext == 3)||(mounthNumberNext == 5)||(mounthNumberNext == 8)||
                            (mounthNumberNext == 10)){
                        //если номер дня в ряду дней меньше 30
                        if (value <= 30) {
                            // то значения по оси Х определяем так
                            return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                            //если номер дня в ряду дней больше 30, то следующий месяц определяем так
                        }else
                            return super.formatLabel(value - 30,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];

                        //если февраль (месяцы в календаре начинатся с 0)
                    }else if (mounthNumberNext == 1) {
                        //Log.d(TAG, "mounthNumberNow= " + mounthNumberNext );
                        //если високосный год
                        if (((yearNumberNext % 4 == 0) && !(yearNumberNext % 100 == 0))||(yearNumberNext % 400 == 0)){
                            //если номер дня в ряду дней меньше 29
                            if (value <= 29) {
                                // Log.d(TAG, "value <= 29= " + value );
                                // то значения по оси Х определяем так
                                return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                            }else return super.formatLabel(value - 29,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];
                            //если НЕ високосный год
                        }else
                            //если номер дня в ряду дней меньше 28
                            if (value <= 28) {
                                // Log.d(TAG, "value <= 28= " + value );
                                // то значения по оси Х
                                return super.formatLabel(value, isValueX) + numberMonth[mounthNumberNext];
                                //если номер дня в ряду дней больше 28, то следующий месяц определяем так
                            }else return super.formatLabel(value - 28,
                                    isValueX) + numberMonth[getNextMonth(mounthNumberNext)];
                        //если не числа от 0 до 11, то
                    }else return super.formatLabel(value, isValueX);
                    //если НЕ числа по оси Х
                } else
                    return super.formatLabel(value, isValueX);
            }
        });

        //Создаём ряды данных
        LineGraphSeries<DataPoint> seriesF = new LineGraphSeries<>(pointsF);
        LineGraphSeries<DataPoint> seriesA = new LineGraphSeries<>(pointsA);
        LineGraphSeries<DataPoint> seriesI = new LineGraphSeries<>(pointsI);
        //Создаём метку в точке "Сегодня"
        LineGraphSeries<DataPoint> seriesNow = new LineGraphSeries<>(new DataPoint[]{
                new DataPoint(dayNumberNext, -100),
                new DataPoint(dayNumberNext, 100)
        });

        //Заголовки в легенде - чтобы были видны, её надо показать)
        // seriesF.setTitle("Curve 1");
        //seriesA.setTitle("Curve 2");
        //seriesI.setTitle("Curve 3");
        //graph.getLegendRenderer () . setVisible ( true );

        //Цвет линий
        seriesF.setColor(Color.BLUE);
        seriesA.setColor(Color.RED);
        seriesI.setColor(Color.GREEN);
        seriesNow.setColor(Color.BLACK);
        //ручная установка параметров
        seriesF.setDrawDataPoints(true);
        seriesA.setDrawDataPoints(true);
        seriesI.setDrawDataPoints(true);
        seriesNow.setDrawDataPoints(true);
        //радиус точки
        seriesF.setDataPointsRadius(8);
        seriesA.setDataPointsRadius(8);
        seriesI.setDataPointsRadius(8);
        seriesNow.setDataPointsRadius(5);
        // толщина линии
        seriesF.setThickness(5);
        seriesA.setThickness(5);
        seriesI.setThickness(5);
        seriesNow.setThickness(5);
        // добавление на график порции данных
        graph.addSeries(seriesF);
        graph.addSeries(seriesA);
        graph.addSeries(seriesI);
        graph.addSeries(seriesNow);

        // Уровни в % для каждого цикла на сегодня
        levelF = (int) Math.round(pointsF[0].getY());
        levelA = (int) Math.round(pointsA[0].getY());
        levelI = (int) Math.round(pointsI[0].getY());
        //показываем уровни на экране
        mTextViewFiz.setText("" + levelF);
        mTextViewAmo.setText("" + levelA);
        mTextViewInt.setText("" + levelI);

        //Log.d(TAG, "Сегодня " + "Физ =" + levelF + " Эмоц = " +
        //      levelA + "  Интел =" + levelI);
        //Название графика
        //graph.setTitle ("Биоритмы");


        //количество меток на оси Х
        graph.getGridLabelRenderer().setNumHorizontalLabels(4);
        //название оси х
        // graph.getGridLabelRenderer().setHorizontalAxisTitle("djdfmvmf");
        //расстояние надписей от оси х
        graph.getGridLabelRenderer().setLabelsSpace(0);
        //размер текста надписей
        graph.getGridLabelRenderer().setTextSize(50);
        // ручная установка верт пределов
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-120);
        graph.getViewport().setMaxY(120);
        //  ручная установка горизонт пределов
        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(dayNumberNext);
        graph.getViewport().setMaxX(dayNumberNext + n);
        // разрешение горизонт прокрутки
        //graph.getViewport().setScrollable(true);
        return deltaDays;
    }

    private static int getNextMonth(int mounthNumberNext){
        if (mounthNumberNext == 11){
            return 0;
        }
        return mounthNumberNext+1;
    }
}
