package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class ListDialog extends AppCompatActivity {

    public final String TAG = "33333";
    public static final String REQUEST_PERSON = "request_person"; //риквест код
    ListView mListView;
    int request;
    private SharedPreferences prefSetting;
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_dialog);

        request = getIntent().getIntExtra(REQUEST_PERSON,0);

        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);
        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        mListView = (ListView)findViewById(R.id.listViewDialog);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Log.d(TAG," ListDialog onCreate position = " + i + "   id = " + l);
                Intent intent = new Intent();

                if (request == JointActivity.REQUEST_JOINT_PERSON1){
                    intent.putExtra(JointActivity.ID_SQL, l);

                }else if (request == JointActivity.REQUEST_JOINT_PERSON2){
                    intent.putExtra(JointActivity.ID_SQL_second, l);
                }

                setResult(RESULT_OK, intent);
                finish();
            }
        });

        //получаем экземпляр PersonDbHelper для работы с базой данных
        PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
        //получаем курсор с отсортированными в соответствии с настройками данными
        Cursor mCursor = mPersonDbHelper.getCursorWithSort(isSort,sort);
        // формируем столбцы сопоставления
        String[] from = new String[]{PersonTable.COLUMN_NAME,
                PersonTable.COLUMN_DR, PersonTable.COLUMN_PAST_DAYS};
        int[] to = new int[]{R.id.name_list, R.id.was_born, R.id.past_Days};
        // создааем адаптер и настраиваем список
        SimpleCursorAdapter scAdapter = new SimpleCursorAdapter(
                this, R.layout.list_name_date, mCursor, from, to);
        mListView.setAdapter(scAdapter);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
