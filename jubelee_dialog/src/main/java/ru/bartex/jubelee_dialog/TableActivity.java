package ru.bartex.jubelee_dialog;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class TableActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String ID_SQL = "sqlTableActivity";

    TextView mTextViewNameTwoAct;

    CheckBox mCheckBox100;
    CheckBox mCheckBox1000;
    CheckBox mCheckBox5000;

    ListView mListView;

    private Calendar secondCalendar;

    ArrayList<String> mArrayListDMY;
    ArrayAdapter mAdapter;

    public final String TAG = "33333";

    String name;

    int i= 0;
    int j = 0;
    int k = 0;

    int day, month, year;
    int dayNumberNext2,mounthNumberNext2, yearNumberNext2;

    long id_sql; //id строки с данными из базы данных

    File file;
    View main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table);

        //Стрелка Назад в ActionBar
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true );
        act.setHomeButtonEnabled(true);

        main = findViewById(R.id.table_main);

        mTextViewNameTwoAct = findViewById(R.id.textViewNameTwoAct);
        mListView = findViewById(R.id.listViewTwo);

        mCheckBox100 =  findViewById(R.id.checkBox100);
        mCheckBox100.setChecked(false);
        mCheckBox100.setOnClickListener(this);

        mCheckBox1000 =  findViewById(R.id.checkBox1000);
        mCheckBox1000.setChecked(false);
        mCheckBox1000.setOnClickListener(this);

        mCheckBox5000 =  findViewById(R.id.checkBox5000);
        mCheckBox5000.setChecked(true);
        mCheckBox5000.setEnabled(false);

        Intent intent = getIntent();
        id_sql = intent.getLongExtra(ID_SQL,0);
        //ss = TimeActivity.getDataFromString(dataString);

        //получаем экхземпляр PersonDbHelper
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        //получаем курсор с данными строки с id
        Cursor mCursor = mPersonDbHelper.getPerson(id_sql);

        // Узнаем индекс каждого столбца
        int nameColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_NAME);
        int drColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_DR);
        int dayColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_DAY);
        int monthColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_MONTH);
        int yearColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_YEAR);

        String currentName = mCursor.getString(nameColumnIndex);
        String currentDr = mCursor.getString(drColumnIndex);
        String currentDay = mCursor.getString(dayColumnIndex);
        String currentMonth = mCursor.getString(monthColumnIndex);
        String currentYear = mCursor.getString(yearColumnIndex);

        //закрываем курсор
        mCursor.close();

        name = currentName;
        day = Integer.parseInt(currentDay);
        month = Integer.parseInt(currentMonth);
        year = Integer.parseInt(currentYear);

        //показываем имя
        mTextViewNameTwoAct.setText(name);
        //новый список
        mArrayListDMY = new ArrayList<>();

        //заполняем список данными
        buildAllList();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d(TAG, "Дата рождения = " + day + "." +
                        (month) + "." + year);

            }
        });

        mAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,mArrayListDMY);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.table,menu);
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
                startActivity(Intent.createChooser(shareIntent, "Отправить с помощью"));
                return true;

            case R.id.action_help_table:
                Log.d(TAG, "OptionsItem = action_help_table");
                Intent intentTable = new Intent(this, HelpActivity.class);
                intentTable.putExtra(P.HELP_FROM, P.HELP_FROM_TABLE);
                startActivity(intentTable);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //=================================Функции====================================//

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

    //Сделать Bitmap по Activity
    private Bitmap screenShot(Activity activity) {
        View view = activity.getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    //сохранить на SD картинку - скриншот экрана
    private  File saveBitmap(Bitmap bm, String fileName){
        final String path = Environment.getExternalStorageDirectory() + "/Screenshots";
        Log.d(TAG, "isExternalStorageEmulated = " + Environment.isExternalStorageEmulated() +
                "    getExternalStorageState = " + Environment.getExternalStorageState());
        Log.d(TAG, "path: " + path);

        File dir = new File(path);
        if(!dir.exists())
            dir.mkdirs();
        File file = new File(dir, fileName);
        try {
            FileOutputStream fOut = new FileOutputStream(file);
            //FileOutputStream fOut = openFileOutput(fileName, Context.MODE_PRIVATE);
            bm.compress(Bitmap.CompressFormat.PNG, 90, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "file.length: " + file.length() + "  getFreeSpace: "+ file.getFreeSpace());
        return file;
    }

    @Override
    public void onClick(View v) {
        mArrayListDMY.clear();
        buildAllList();
        mAdapter.notifyDataSetChanged();
    }

    private void buildPartList(int i){
        secondCalendar = new GregorianCalendar(year, month - 1, day);
        secondCalendar.add(Calendar.DAY_OF_YEAR, i);
        dayNumberNext2 = secondCalendar.get(Calendar.DAY_OF_MONTH);
        mounthNumberNext2 = secondCalendar.get(Calendar.MONTH);
        yearNumberNext2 = secondCalendar.get(Calendar.YEAR);
        long temp = secondCalendar.getTimeInMillis() - System.currentTimeMillis();
        long result = temp / 86400000;
        String s3;

        Log.d(TAG, "temp = " + temp + "  result = " + result);

        if ((day == dayNumberNext2) && (month == mounthNumberNext2 + 1) && (year == yearNumberNext2)) {
            s3 = String.format("Д.р. - %02d.%02d.%04d",
                    dayNumberNext2, mounthNumberNext2 + 1, yearNumberNext2);

        } else if (result > 0) {
            s3 = String.format("%d - %02d.%02d.%04d ост %d дн",
                    i, dayNumberNext2, mounthNumberNext2 + 1, yearNumberNext2, result);

        } else if ((result == 0) && (temp < 86400000)&&(temp > 0)) {
            s3 = String.format("%d - %02d.%02d.%04d  завтра",
                    i, dayNumberNext2, mounthNumberNext2 + 1, yearNumberNext2);

        } else if ((result == 0) && (temp > -86400000)&&(temp < 0)) {
            s3 = String.format("%d - %02d.%02d.%04d  сегодня",
                    i, dayNumberNext2, mounthNumberNext2 + 1, yearNumberNext2);

        } else {
            s3 = String.format("%d - %02d.%02d.%04d",
                    i, dayNumberNext2, mounthNumberNext2 + 1, yearNumberNext2);
        }
        mArrayListDMY.add(s3);
        Log.d(TAG, "Введённая дата = " + dayNumberNext2 + "." +
                (mounthNumberNext2 + 1) + "." + yearNumberNext2);
    }

    private void buildAllList(){

        if (mCheckBox100.isChecked()) {
            for (int i = 0; i <= 1000; i += 100) {
                buildPartList(i);
                j = 2000;
            }
        } else j = 0;

        if (mCheckBox1000.isChecked()) {
            for (int i = j; i <= 5000; i += 1000) {
                buildPartList(i);
                k = 10000;
            }
        }else {
            if (mCheckBox100.isChecked()){
                k = 5000;
            } else k = 0;
        }

        if (mCheckBox5000.isChecked()) {
            for (int i = k; i <= 35000; i += 5000) {
                buildPartList(i);
            }
        }
    }

}
