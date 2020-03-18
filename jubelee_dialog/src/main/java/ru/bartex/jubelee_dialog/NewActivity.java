package ru.bartex.jubelee_dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.app.FragmentManager;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

public class NewActivity extends AppCompatActivity  {

    public final String TAG = "33333";
    public static final String REQUEST_CODE = "request_codeNewActivity";
    public static final String ID_SQL = "sqlNewActivity";

    static EditText etName, etDay, etMounth, etYear;

    int dayNumber,mounthNumber, yearNumber;
    int requestCode;
    long id_sql;
    long newRowId;
    boolean change;

    Button btnOK;
    Button buttonCancel;
    Button buttonClear;
    ImageButton btnDateDialog;

    PersonDbHelper mDbHelper = new PersonDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);

        //только портретная ориентация
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        etName =  findViewById(R.id.etName);
        etDay =  findViewById(R.id.etDay);
        etMounth =  findViewById(R.id.etMonth);
        etYear =  findViewById(R.id.etYear);


        Bundle extras = getIntent().getExtras();
        if(extras != null) {
            requestCode = extras.getInt(REQUEST_CODE);
            //если пришло из изменить запись
            if (requestCode == 111) {
                id_sql = extras.getLong(ID_SQL);
                //получаем экхземпляр PersonDbHelper
                PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
                //получаем курсор с данными строки с id
                Cursor mCursor = mPersonDbHelper.getPerson(id_sql);

                // Узнаем индекс каждого столбца
                int nameColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_NAME);
                int dayColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_DAY);
                int monthColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_MONTH);
                int yearColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_YEAR);

                String currentName = mCursor.getString(nameColumnIndex);
                String currentDay = mCursor.getString(dayColumnIndex);
                String currentMonth = mCursor.getString(monthColumnIndex);
                String currentYear = mCursor.getString(yearColumnIndex);

                //закрываем курсор
                mCursor.close();

                //заполняем поля экрана
                etName.setText(currentName);
                etDay.setText(currentDay);
                etMounth.setText(currentMonth);
                etYear.setText(currentYear);
            }
        }

        buttonClear  =  findViewById(R.id.buttonClear);
        buttonClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText("");
                //Устанавливаем фокус ввода в поле etName
                etName.requestFocus();
                //Вызываем экранную клавиатуру -метод работает как в 4.0.3, так и в 6.0
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etName, 0);
                //так тоже работает
                //imm.showSoftInput(etName, InputMethodManager.SHOW_IMPLICIT);
                //вызов диалога выбора клавиатуры
                //imm.showInputMethodPicker();

            }
        });
        btnOK =  findViewById(R.id.btnOK);
        //Слушатель на кнопку ОК
        btnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Calendar firstCalendar;
                boolean tempDay = getDay();
                boolean tempMounth = getMounth();
                boolean tempYear = getYear();
                //boolean temp4 = getDays();
                boolean tempName = getName();

                if (tempDay && tempMounth && tempYear && tempName) {

                    String name = etName.getText().toString();
                    String day = etDay.getText().toString();
                    String mounth = etMounth.getText().toString();
                    String year = etYear.getText().toString();
                    //формируем строку даты в формате %s.%s.%s
                    String dr = String.format("%s.%s.%s",day,mounth,year);
                    //экземпляр календаря с данными из списка
                    firstCalendar = new GregorianCalendar(Integer.parseInt(year),
                            Integer.parseInt(mounth) - 1,Integer.parseInt(day));
                    //получаем дату в милисекундах
                    long firstCalendarMillis = firstCalendar.getTimeInMillis();
                    long nowTimeMillis = System.currentTimeMillis();
                    //количество прошедших дней с даты рождения
                    long beenDays = (nowTimeMillis-firstCalendarMillis)/86400000;
                    //количество прожитых дней как строка
                    String past_days = Long.toString(beenDays);

                    //строка даты в формате SQLite %s-%s-%s
                    String drSQL = String.format("%s-%s-%s",year,mounth,day);
                    Log.d(TAG, "Др: "  + drSQL + "   beenDays " + beenDays);

                    //если изменить запись
                    if (requestCode ==111){
                        //изменяем строку в базе
                        change = mDbHelper.updatePerson(id_sql, name,day,mounth,year, dr, past_days);
                        // Выводим сообщение в успешном случае или при ошибке
                        if (!change) {
                            Log.d(TAG, "Ошибка при редактировании персоны " + id_sql);
                        } else {
                            Log.d(TAG, "Отредактирована персона  под номером: "  + id_sql);
                        }
                        Intent intent = new Intent();
                        intent.putExtra("id_sql", id_sql);
                        setResult(RESULT_OK, intent);
                        finish();

                        //если создать запись из главного меню по плавающей кнопке
                    }else if (requestCode ==11){
                        //пишем в таблицу базы новую строку
                        newRowId = mDbHelper.addPerson(name,day,mounth,year,dr,past_days);
                        // Выводим сообщение в успешном случае или при ошибке
                        if (newRowId == -1) {
                            // Если ID  -1, значит произошла ошибка
                            Log.d(TAG, "Ошибка при заведении новой персоны ");
                        } else {
                            Log.d(TAG, "Персона заведена под номером: "  + newRowId );
                        }
                        Intent intent = new Intent(NewActivity.this, PersonsListActivity.class);
                        intent.putExtra(P.FROM_MAIN, P.TO_DIALOG);
                        startActivity(intent);
                        finish();

                        //если создать из PersonsListActivity по плюсику в тулбаре
                    }else if (requestCode ==333){
                        //пишем в таблицу базы новую строку
                        newRowId = mDbHelper.addPerson(name,day,mounth,year,dr,past_days);
                        // Выводим сообщение в успешном случае или при ошибке
                        if (newRowId == -1) {
                            // Если ID  -1, значит произошла ошибка
                            Log.d(TAG, "Ошибка при заведении новой персоны ");
                        } else {
                            Log.d(TAG, "Персона заведена под номером: "  + newRowId );
                        }
                        Intent intent = new Intent(NewActivity.this, PersonsListActivity.class);
                        intent.putExtra(P.FROM_MAIN, P.TO_DIALOG);
                        startActivity(intent);
                        finish();
                    }
                }
            }
        });

        buttonCancel =  findViewById(R.id.buttonCancel);
        //Слушатель на кнопку Cancel
        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        btnDateDialog =  findViewById(R.id.buttonDateDialog1);
        //Слушатель на кнопку ввода диалога DatePickerFragment
        btnDateDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager manager = getFragmentManager();
                DialogFragment dialogFragment = new DatePickerFragment();
                dialogFragment.show(manager,"DatePicker");
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.new_person,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            case R.id.action_help_new_person:
                Log.d(TAG, "OptionsItem = action_help_time");
                Intent intentNew = new Intent(this, HelpActivity.class);
                intentNew.putExtra(P.HELP_FROM, P.HELP_FROM_NEW_PERSON);
                startActivity(intentNew);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    public static class DatePickerFragment extends DialogFragment implements
                             DatePickerDialog.OnDateSetListener{
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new DatePickerDialog(getActivity(),this,1980,6,19);
        }
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            etDay.setText(Integer.toString(dayOfMonth));
            etMounth.setText(Integer.toString(month+1));
            etYear.setText(Integer.toString(year));
        }
    }

    boolean getName(){
        boolean n;
        String s = etName.getText().toString();
        if (s.length() == 0) {
            n = false;
            myToast ("Введите имя");
        } else if (s.equals("Новое имя")){
            Random rnd = new Random(System.currentTimeMillis());
            int q = rnd.nextInt(1000);
            n = true;
            s =s + " " + q;
            etName.setText(s);
        }else n = true;
        return n;
    }

    boolean getDay(){
        boolean d;
        if (etDay.getText().toString().equals("")) {
            dayNumber = 0;
            d = false;
            myToast ("Введите день месяца");
        }else {
            int i = Integer.parseInt(etDay.getText().toString());
            if (i>0 && i<=31) {
                dayNumber = i;
                d = true;
            }else {
                d = false;
                myToast ("День месяца\nВведите число в диапазоне 1-31");
            }
        }
        return d;
    }

    boolean getMounth(){
        boolean m;
        if (etMounth.getText().toString().equals("")) {
            mounthNumber = 0;
            m = false;
            myToast ("Введите месяц рождения");
        }else {
            int i = Integer.parseInt(etMounth.getText().toString());
            if (i>=1 && i<=12) {
                mounthNumber = i;
                m = true;
            }else {
                m = false;
                myToast ("Месяц:\nВведите число в диапазоне 1-12");
            }
        }
        return m;
    }

    boolean getYear(){
        boolean y;
        if (etYear.getText().toString().equals("")) {
            yearNumber = 0;
            y = false;
            myToast ("Введите год рождения");
        }else {

            int i = Integer.parseInt(etYear.getText().toString());
            if (i>=1900 && (i<= (new GregorianCalendar()).get(Calendar.YEAR))) {
                //Log.d(TAG,"YEAR = " + (new GregorianCalendar()).get(Calendar.YEAR));
                yearNumber = i;
                y = true;
            }else {
                y = false;
                myToast ("Год рождения:\nЧисло от 1900 до  " + (new GregorianCalendar()).get(Calendar.YEAR));
            }
        }
        return y;
    }

    void myToast (String s){
        Toast mToast = Toast.makeText(NewActivity.this,s, Toast.LENGTH_SHORT);
        mToast.setGravity(Gravity.CENTER,0,0);
        mToast.show();
    }

}
