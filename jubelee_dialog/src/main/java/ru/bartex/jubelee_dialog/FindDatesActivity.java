package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class FindDatesActivity extends AppCompatActivity {

    public final String TAG = "33333";

    final static String LINE_CHECKED = "line_checked";
    public static final String REQUEST_FIND = "request_find"; //риквест код
    public static final int REQUEST_CHOOSE = 4; //риквест код
    int request;
    ListView mListView;

    ArrayList<Map<String, Object>> data = new ArrayList<>();
    Map mMap;

    //получаем экземпляр PersonDbHelper для работы с базой данных
    PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
    final String NAME1 = "ru.bartex.jubelee_dialog.currentName1";
    final String NAME2 = "ru.bartex.jubelee_dialog.currentName2";
    final String PAST_DAYS = "ru.bartex.jubelee_dialog.currentPastDays";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_dates);

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true);
        act.setHomeButtonEnabled(true);

        mListView = (ListView) findViewById(R.id.listViewDialogJoint);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //получаем имя из данных адаптера
                Map<String, Object> v = data.get(position);

                String name1 = (String) v.get(NAME1);
                String name2 = (String) v.get(NAME2);

                long id_from_name1 = mPersonDbHelper.getIdFromName(name1);
                long id_from_name2 = mPersonDbHelper.getIdFromName(name2);

                Intent intent1 = new Intent(FindDatesActivity.this,JointActivity.class);
                //передаём id выбранной пары сначала в ListDialog_CheckBoxБ потом в JointActivity
                intent1.putExtra(JointActivity.ATTR_ID1, id_from_name1);
                intent1.putExtra( JointActivity.ATTR_ID2, id_from_name2);
                intent1.putExtra(JointActivity.REQUEST_JOINT_CHOOSE, REQUEST_CHOOSE );
                startActivity(intent1);
                Log.d(TAG, "onItemClick name1 = " +name1 +
                        "  onItemClick name2 = " + name2 +
                        "  onItemClick id_from_name1 = " + id_from_name1+
                        "  onItemClick id_from_name2 = " + id_from_name2);
                //finish();
            }
        });

        //вывод данных из сериализованного списка объектов
        ArrayList<Person> mArrayListChecked = (ArrayList<Person>) getIntent().
                getSerializableExtra(LINE_CHECKED);
        request = getIntent().getIntExtra(REQUEST_FIND,3);

        int size = mArrayListChecked.size();

        if (size == 1){
            ArrayList<Person> mArrayListAll =  mPersonDbHelper.getAllContactsChoose();

            long id_1 = mArrayListChecked.get(0).getPerson_id();
            String name1 = mArrayListChecked.get(0).getPerson_name();

            for (int k = 0; k < mArrayListAll.size(); k++) {
                long id_2 = mArrayListAll.get(k).getPerson_id();
                String name2 = mArrayListAll.get(k).getPerson_name();
                long forTwo_Days_1 = (mPersonDbHelper.getMillisForTwo(id_1, id_2)) / 86400000;
                Log.d(TAG, "FindDatesActivity  " +
                        "  id_1 = " + id_1 +
                        "  name1 = " + name1 +
                        "  id_2 = " + id_2 +
                        "  name2 = " + name2 +
                        "  forTwo_Days = " + forTwo_Days_1);
                if (!name1.equals(name2)) {
                    mMap = new HashMap();
                    mMap.put(NAME1, name1);
                    mMap.put(NAME2, name2);
                    mMap.put(PAST_DAYS, forTwo_Days_1);
                    data.add(mMap);
                }
        }
        }else if (size > 1){
            for (int i = 0; i < size; i++) {
                for (int k = i+1; k < size; k++) {
                    long id_1 = mArrayListChecked.get(i).getPerson_id();
                    String name1 = mArrayListChecked.get(i).getPerson_name();
                    long id_2 = mArrayListChecked.get(k).getPerson_id();
                    String name2 = mArrayListChecked.get(k).getPerson_name();
                    long forTwo_Days_1 = (mPersonDbHelper.getMillisForTwo(id_1, id_2)) / 86400000;
                    Log.d(TAG, "FindDatesActivity  " +
                            "  id_1 = " + id_1 +
                            "  name1 = " + name1 +
                            "  id_2 = " + id_2 +
                            "  name2 = " + name2 +
                            "  forTwo_Days = " + forTwo_Days_1);
                    if (!name1.equals(name2)) {
                        mMap = new HashMap();
                        mMap.put(NAME1, name1);
                        mMap.put(NAME2, name2);
                        mMap.put(PAST_DAYS, forTwo_Days_1);
                        data.add(mMap);
                    }
                }
            }
        }
            // формируем столбцы сопоставления
            String[] from = new String[]{NAME1, PAST_DAYS, NAME2};
            int[] to = new int[]{R.id.name_list_one, R.id.past_Days_joint, R.id.name_list_two};
            // создааем адаптер и настраиваем список
            SimpleAdapter scAdapter = new SimpleAdapter(this, data, R.layout.list_name_for_two, from, to);
            mListView.setAdapter(scAdapter);
        }

        @Override
        public boolean onCreateOptionsMenu (Menu menu){
            getMenuInflater().inflate(R.menu.find_dates, menu);
            return super.onCreateOptionsMenu(menu);
        }

        @Override
        public boolean onOptionsItemSelected (MenuItem item){

            switch (item.getItemId()) {
                case android.R.id.home:
                    Log.d(TAG, "Домой");
                    //onBackPressed();
                    Intent intent = new Intent(this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP );
                    startActivity(intent);
                    finish();
                    return true;

                case R.id.action_settings:
                    Log.d(TAG, "OptionsItem = action_settings");
                    Intent intentSettings = new Intent(this, PrefActivity.class);
                    startActivity(intentSettings);
                    return true;

                case R.id.action_help_find_date:
                    Log.d(TAG, "OptionsItem = action_help_time");
                    Intent intentTime = new Intent(this, HelpActivity.class);
                    intentTime.putExtra(P.HELP_FROM, P.HELP_FROM_FIND_DATE);
                    startActivity(intentTime);
                    return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        protected void onDestroy () {
            super.onDestroy();
            mPersonDbHelper.close();
        }
    }

