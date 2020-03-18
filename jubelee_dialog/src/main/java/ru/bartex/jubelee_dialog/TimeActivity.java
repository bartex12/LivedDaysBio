package ru.bartex.jubelee_dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class TimeActivity extends AppCompatActivity implements
        TextWatcher {

    public final String TAG = "33333";
    public static final String ID_SQL = "sqlTimeActivity";
    final String FILENAME_SD = "savedBitmap.png";

    TextView willBe, lastDays, userName, dataBorn, prefix;
    EditText  days;
    Button findDate;//кнопка Рассчитать

    int dayNumber,mounthNumber, yearNumber, daysNumber;
    int dayNumberNext,mounthNumberNext, yearNumberNext;

    long id_sql;  // id строки из базы данных

    private Calendar firstCalendar;
    private Timer mTimer;
    private TimerTask mTimerTask;

    Toast mToast;

    long nowTimeMillis;
    long firstCalendarMillis;
    long beenDays;
    long beenDays1;
    private long mKvant = 100;//время в мс между срабатываниями TimerTask

    File fileScreenShot;
    View main;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time);
        //только портретная ориентация
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        ActionBar act = getSupportActionBar();
        if (act!=null){
            act.setDisplayHomeAsUpEnabled(true );
            act.setHomeButtonEnabled(true);
        }

        main = findViewById(R.id.time_main);


        findDate = findViewById(R.id.buttonFind);
        findDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "findDate.setOnClickListener ");
                onFindDateSimple();
                onFindDateKey();
            }
        });

        willBe = findViewById(R.id.textViewWillBe);
        lastDays = findViewById(R.id.textViewLastDays);
        userName   = findViewById(R.id.textViewUserName);
        dataBorn = findViewById(R.id.textViewDataBorn);
        prefix = findViewById(R.id.textViewPrefix);

        days = findViewById(R.id.editTextDays);
        days.addTextChangedListener(this);

        //Загружаем данные из интента
        Intent intent = getIntent();
        id_sql = intent.getLongExtra(ID_SQL,0);

        //получаем экхземпляр PersonDbHelper
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        //получаем  данные для строки с id
        Person person = mPersonDbHelper.getPersonObjectData(id_sql);

        //рассчитываем дату рождения
        dayNumber = Integer.parseInt(person.getPerson_day());
        mounthNumber = Integer.parseInt(person.getPerson_month());
        yearNumber = Integer.parseInt(person.getPerson_year());

        //пишем имя
        userName.setText(person.getPerson_name());
        //пишем дату рождения
        dataBorn.setText(person.getPerson_dr());

        days.setText("10000");
        //устанавливаем фокус в конце строки
        days.requestFocus();

        mToast = new Toast(this);
        mToast.setGravity(Gravity.CENTER,0,0);

        //вызов обработчика кнопки Рассчитать без скрытия клавиатуры
        //здесь запускаем myTimerTask
        onFindDateSimple();
    }

    //==================================Конец onCreate====================================//


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Сохраняем текст
        //saveText();
        if (mTimer != null) mTimer.cancel();

    }

    //вызов диалогового окна при нажатии на кнопку Назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "MainActivity onBackPressed");
        //Intent intent = new Intent(MainActivity.this, BioritmActivity.class);
        //startActivity(intent);
        //finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.time,menu);
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

            //чтобы работала стрелка Назад, а не происходил крах приложения
            case android.R.id.home:
                Log.d(TAG, "Домой");
                onBackPressed();
                return true;

            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;

            case R.id.action_table:
                Log.d(TAG, "action_table");
                Intent intent = new Intent(TimeActivity.this,TableActivity.class);
                intent.putExtra(TableActivity.ID_SQL,id_sql);
                startActivity(intent);
                return true;

            case R.id.action_share:

                //так тоже работает размер 42К
                //Bitmap bm = screenShot(this);
                //file = saveBitmap(bm, "image1.png");

                //для 4-0-0 вроде бы без краха, но в файле ничего нет
                //file = takeScreenshot159For400(bitmap, FILENAME_SD);

                //метод на два варианта- 7-0 и 4-0  Здесь bitmap создается внутри метода
               //fileScreenShot = takeScreenshotInFile(FILENAME_SD);

                //так работает размер 37К
                Bitmap bitmap = takeScreenshot(main);
                Log.d(TAG, "OptionsItem = action_share getByteCount = " + bitmap.getByteCount());

                fileScreenShot = takeScreenshot159(bitmap, FILENAME_SD);
                Log.d(TAG, "AbsolutePath: " + fileScreenShot.getAbsolutePath());

                Uri uri = Uri.fromFile(new File(fileScreenShot.getAbsolutePath()));
                Log.d(TAG, "uri: " + uri);

                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Посмотрите ка на это!");
                shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                shareIntent.setType("image/*");
                shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(Intent.createChooser(shareIntent, "Отправить с помощью"));
                return true;

            case R.id.action_help_time:
                Log.d(TAG, "OptionsItem = action_help_time");
                Intent intentTime = new Intent(this, HelpActivity.class);
                intentTime.putExtra(P.HELP_FROM, P.HELP_FROM_TIME);
                startActivity(intentTime);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //=================================Функции====================================//

    // метод на 2 варианта MEDIA_MOUNTED и MEDIA_SHARED
    private File takeScreenshotInFile(String fileName) {

        // Создаём bitmap скриншот
        View v1 = getWindow().getDecorView().getRootView();
        v1.setDrawingCacheEnabled(true);
        Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
        v1.setDrawingCacheEnabled(false);

        File path = null;
        File imageFile = null;

        //если нет sd карты, то не работает - нужно знать состояние SD карты
        //File filesDir = getExternalFilesDir( Environment.DIRECTORY_PICTURES);
        // Запись и считывание зависят от состояния SD карты а также от того,
        // подключен ли кабель между телефоном и компьютером для передачи данных (например, файлов APK) .
        // Если кабель подключен, то для Андроид 7 проблем не возникает, а для Андроид 4 работа с файлами
        // будет затруднена или невозможна, особенно, если телефон подключен как модуль USB
        // а не как HI Suite для хуавея, например. Это происходит из-за смены состояния
        // в строке String sdState = android.os.Environment.getExternalStorageState();
        //С Environment.MEDIA_MOUNTED на Environment.MEDIA_SHARED, в результате чего для Андроид 4
        // перестаёт работать метод
        // File dir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //И нужно использовать другой каталог File dir = getFilesDir();
        // Данная ситуация отработана в модуле screenshot_transmit проекта
        // JubeleeDatesBioritm, где используются разные каталоги в зависимости от состояния SD карты

        //Получаем состояние SD карты
        String sdState = android.os.Environment.getExternalStorageState();
        //если sdState == MEDIA_MOUNTED
        if (sdState.equals(android.os.Environment.MEDIA_MOUNTED)){
            Log.d(TAG, "sdState equals(android.os.Environment.MEDIA_MOUNTED) = " + sdState);
            try {
                path = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
                Log.d(TAG, " takeScreenshotInFile:  path.getAbsolutePath() =  " + path.getAbsolutePath());
                if(!path.exists()) {
                    path.mkdirs();
                    Log.d(TAG, "path не существует - создаём ");
                }
            } catch (Throwable e) {
                Log.d(TAG, " takeScreenshotInFile: Ошибка в try для MEDIA_MOUNTED");
                e.printStackTrace();
            }
            //если sdState != MEDIA_MOUNTEDБ а , например, shared ,  то
        }else {
            Log.d(TAG, "sdState NOT equals(android.os.Environment.MEDIA_MOUNTED) = " + sdState);
            try {
                path = getFilesDir();
                if(!path.exists()) {
                    path.mkdirs();
                    Log.d(TAG, "path не существует - создаём ");
                }
                Log.d(TAG, " takeScreenshotInFile:  path.getAbsolutePath() =  " + path.getAbsolutePath());
            } catch (Throwable e) {
                Log.d(TAG, " takeScreenshotInFile: Ошибка в try для НЕ  MEDIA_MOUNTED");
                e.printStackTrace();
            }
        }
        //создаём файл по имени директории и имени файла
        imageFile = new File(path, fileName);
        Log.d(TAG, " takeScreenshotInFile:  imageFile.getAbsolutePath() =  " + imageFile.getAbsolutePath());

        try {
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.flush();
            outputStream.close();
        } catch (Throwable e) {
            Log.d(TAG, " takeScreenshotInFile: Ошибка в try внутр");
            e.printStackTrace();
        }

        Log.d(TAG, " takeScreenshotInFile Перед return:   imageFile.length = : " +
                imageFile.length() + " imageFile isFile: = " + imageFile.isFile() +
                "  imageFile.isDirectory() = " + imageFile.isDirectory());
        return imageFile;
    }

    //метод на один вариант MEDIA_MOUNTED
    private File takeScreenshot159(Bitmap bitmap,String fileName ) {

        // Запись и считывание зависят от состояния SD карты а также от того,
        // подключен ли кабель между телефоном и компьютером для передачи данных (например, файлов APK) .
        // Если кабель подключен, то для Андроид 7 проблем не возникает, а для Андроид 4 работа с файлами
        // будет затруднена или невозможна, особенно, если телефон подключен как модуль USB
        // а не как HI Suite для хуавея, например. Это происходит из-за смены состояния
        // в строке String sdState = android.os.Environment.getExternalStorageState();
        //С Environment.MEDIA_MOUNTED на Environment.MEDIA_SHARED, в результате чего для Андроид 4
        // перестаёт работать метод
        // File dir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //И нужно использовать другой каталог File dir = getFilesDir();
        // Данная ситуация отработана в модуле screenshot_transmit проекта
        // JubeleeDatesBioritm, где используются разные каталоги в зависимости от состояния SD карты

        //так работает для андроид 7 но не для андроид 4 - крах, если подключен шнурком
        File pathDir = getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);

        if(!pathDir.exists()) {
        Log.d(TAG, "takeScreenshot159 SD-карта путь к файлу: ");
            pathDir.mkdirs();
        }
        File fileScr = new File(pathDir, fileName);
        // Make sure the Pictures directory exists.
        if (fileScr.isFile()){
            Log.d(TAG, "takeScreenshot159 SD-карта путь к файлу: " + fileScr.getAbsolutePath());
        }else {
            Log.d(TAG, "takeScreenshot159 SD-карта путь к файлу: ФАЙЛ НЕ СУЩЕСТВУЕТ");
        }

        try {
            FileOutputStream fos = null;
            Log.d(TAG, " До File file = " + fileScr.length() + " isFile: " + fileScr.isFile());
            try {
                fos = new FileOutputStream(fileScr);
                Log.d(TAG, "FileOutputStream fos  = " + fos);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                Log.d(TAG, " После File file = " + fileScr.length() +
                        "  isFile: " + fileScr.isFile());
                return fileScr;
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

    private Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    private  File saveBitmap(Bitmap bm, String fileName){

        final String path = Environment.getExternalStorageDirectory() + "/Screenshots";

        Log.d(TAG, "isExternalStorageEmulated = " + Environment.isExternalStorageEmulated() +
                "    getExternalStorageState = " + Environment.getExternalStorageState());
        Log.d(TAG, "path: " + path);

        File dir = new File(path);
        if(!dir.exists()) {
            dir.mkdirs();
        }
        // запись в файл по пути : /storage/emulated/0/Screenshots/image1.png
        File file = new File(dir, fileName);
        Log.d(TAG, "До file.length: " + file.length() + "  getFreeSpace: "+ file.getFreeSpace());
        Log.d(TAG, "getAbsolutePath: " + file.getAbsolutePath());

        try {
            FileOutputStream fOut = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "После file.length: " + file.length() + "  getFreeSpace: "+ file.getFreeSpace());
        return file;
    }

    boolean getDays (String s){
        boolean ds;
        if (s.equals("")) {
            daysNumber = 0;
            ds = false;
            //ds = true;
            myToast ("Введите желаемое число прожитых дней");
        }else {
            int i = Integer.parseInt(s);
            if (i>=0 && i<= 50000){
                daysNumber = i;
                ds = true;
            }else {
                ds = false;
                myToast ("Количество прожитых дней\nЧисло от 0 до 50000");
            }
        }
        return ds;
    }

    //обработка нажатий на кнопку Рассчитать для метода onCreate
    public void onFindDateSimple(){

        String ss = days.getText().toString();
        if (getDays(ss)) {
            daysNumber = Integer.parseInt(ss);

            Log.d(TAG, "daysNumber " + daysNumber);
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
            Log.d(TAG, "Расчётная дата MainActivity onFindDateSimple= " + dayNumberNext + "." +
                    (mounthNumberNext + 1) + "." + yearNumberNext);

            if (mTimer != null) mTimer.cancel();
            mTimer = new Timer();
            mTimerTask = new myTimerTask();
            //запускаем задачу по расписанию = контроль каждые mKvant (100мс)
            mTimer.scheduleAtFixedRate(mTimerTask, mKvant, mKvant);

            findDate.setEnabled(false);
        }
    }

    public void onFindDateKey(){
        //Прячем экранную клавиатуру
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                InputMethodManager.HIDE_NOT_ALWAYS);
    }

    //действия в случае ручного обновления текстовых полей на экране
    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        findDate.setEnabled(false);
    }
    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {}
    @Override
    //Запрещаем вывод после обновления информации до нажатия на кнопку Рассчитать
    public void afterTextChanged(Editable s) {
        //buttonShowTable.setEnabled(false);
        if (mTimer != null) mTimer.cancel();
        willBe.setText(R.string.better_late);
        //Делаем доступной кнопку Рассчитать, если изменили данные в любом EditText
        findDate.setEnabled(true);
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
                    String s = String.format("%d.%06d",beenDays,beenDays1);
                    lastDays.setText(s);
                    String s1;
                    String s2;
                    //если текущая дата в милисекундах больше расчётной, то расчётная уже была
                    if ((nowTimeMillis-firstCalendar.getTimeInMillis())>0){
                        s1 = String.format("%02d.%02d.%04d",
                                dayNumberNext, mounthNumberNext + 1, yearNumberNext);
                        s2 = "Это было  ";
                        //если текущая дата в милисекундах меньше расчётной, то расчётная будет
                    }else {
                        s1 = String.format("%02d.%02d.%04d",
                                dayNumberNext, mounthNumberNext + 1, yearNumberNext);
                        s2 = "Это будет  ";
                    }
                    willBe.setText(s1);
                    prefix.setText(s2);
                }
            });
        }
    }

    void myToast (String s){
        Toast mToast = Toast.makeText(TimeActivity.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }

}
