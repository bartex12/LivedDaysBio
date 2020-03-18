package ru.bartex.jubelee_dialog;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;

public class SeachActivity extends AppCompatActivity {

    public final String TAG = "33333";
    public static final String LIST_DATA_QUERY = "ru.bartex.jubelee_dialog.list_data_query";

    TextView tvSlovo;
    TextView tvPositions;
    ListView mListViewSearch;

    PersonDbHelper mDbHelper;
    Cursor mCursor;
    SimpleCursorAdapter scAdapter;
    String query;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seach);
        Log.d(TAG, "SeachActivity onCreate");

        //Делаем стрелку Назад на панели действий
        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true );
        act.setHomeButtonEnabled(true);

        Intent intent = getIntent();

        tvSlovo = (TextView)findViewById(R.id.textViewSearchSlovo) ;
        tvPositions = (TextView)findViewById(R.id.textViewSearchPositions) ;
        mListViewSearch = (ListView)findViewById(R.id.listViewSearch);

        //получаем из интента поисковый запрос
        query = intent.getStringExtra(LIST_DATA_QUERY);

        mDbHelper = new PersonDbHelper(this);
        //получаем курсор с результатами поиска
        mCursor = mDbHelper.searchInSQLite (query);
        //Выводим список данных на экран с использованием SimpleCursorAdapter
        showSQLitePersonList(mCursor);

        // показываем на экране поисковый запрос
        if (mListViewSearch.getCount()>0){
            tvSlovo.setText(query);
            tvPositions.setText(Integer.toString(mListViewSearch.getCount()));
        }else {
            tvSlovo.setText(intent.getStringExtra(LIST_DATA_QUERY));
            tvPositions.setText("0");
        }
        //при щелчке на списке отправляем выбранную строку списка в интенте на BioritmActivity
        mListViewSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Intent intentSearch = new Intent();
                intentSearch.putExtra(BioritmActivity.ID_SQL, id);
                setResult(RESULT_OK,intentSearch);
                finish();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){

            //чтобы работала стрелка Назад, а не происходил крах приложения
            case android.R.id.home:
                Log.d(TAG, "Домой");
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "SeachActivity onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "SeachActivity onResume");
    }

    //Выводим список данных на экран с использованием SimpleCursorAdapter
    private void showSQLitePersonList(Cursor mCursor) {

        //поручаем активности присмотреть за курсором
       // startManagingCursor(mCursor);

        // формируем столбцы сопоставления
        String[] from = new String[] {PersonTable.COLUMN_NAME,
                PersonTable.COLUMN_DR, PersonTable.COLUMN_PAST_DAYS };
        int[] to = new int[] { R.id.name_list, R.id.was_born, R.id.past_Days };

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this,
                R.layout.list_name_date, mCursor, from, to);
        mListViewSearch.setAdapter(scAdapter);
    }

}
