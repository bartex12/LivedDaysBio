package ru.bartex.jubelee_dialog;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;

import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.P;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable;
import ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonDbHelper;

import static ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable.COLUMN_CHOOSE;
import static ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable.TABLE_NAME;

public class PersonsListActivity extends AppCompatActivity implements DialogSelectAction.SelectAction {

    private static final int DELETE_ID = 1;
    private static final int CHANGE_ID = 2;
    private static final int CANCEL_ID = 3;

    public final String TAG = "33333";
    final int NEW_ACTIVITY_ADD_REQUEST = 1;
    final int NEW_ACTIVITY_CHANGE_REQUEST = 2;
    static final int request_code = 111;
    static final int request_code_add = 333;
    static final int SEARCH_ACTIVITY = 2222;

    ListView mListView;

    //Виджет поиска
    SearchView searchView = null;

    int pos; // первый видимый элемент списка
    int offset; // для точного позиционирования, вдруг он виден не полностью
    int sort = 1;  //Сортировка: 1-поимени возр, 2- по имени убыв,3-по дате возр, 4 - по дате убыв
    boolean isSort = false; //Список отсортирован?
    private static final String KEY_POS = "POS";
    private static final String KEY_OFFSET = "OFFSET";

    private SharedPreferences shp;
    private SharedPreferences prefSetting;

    PersonDbHelper mPersonDbHelper = new PersonDbHelper(this);
    Cursor mCursor;
    SimpleCursorAdapter scAdapter;

    int from_to;

    @Override
    public void NumberOfAction(int i, long id) {
        Log.d(TAG, "PersonsListActivity NumberOfAction");
        // 0-Личные  даты
        // 1- Совместные даты
        // 2 - Биоритмы
        // 3 - совместимость биоритмов (резерв)
        if (i == 0) {
            Log.d(TAG, "PersonsListActivity NumberOfAction = 1");
            Intent intent = new Intent(PersonsListActivity.this, TimeActivity.class);
            intent.putExtra(TimeActivity.ID_SQL, id);
            startActivity(intent);
        }else if(i == 1){
            Log.d(TAG, "PersonsListActivity NumberOfAction = 2");
            Intent intent = new Intent(PersonsListActivity.this, ListDialog_CheckBox.class);
            startActivity(intent);
        }else if(i == 2){
            Log.d(TAG, "PersonsListActivity NumberOfAction = 3");
            Intent intent = new Intent(PersonsListActivity.this, BioritmActivity.class);
            intent.putExtra(BioritmActivity.ID_SQL, id);
            startActivity(intent);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persons_list);
        Log.d(TAG, "PersonsListActivity onCreate");

        ActionBar act = getSupportActionBar();
        act.setDisplayHomeAsUpEnabled(true );
        act.setHomeButtonEnabled(true);

        from_to = getIntent().getIntExtra(P.FROM_MAIN, 0);

        //обработка интента для поиска, посылаемого системой -  если он есть
        handleIntent(getIntent());

        //получаем файл с настройками для приложения
        prefSetting = PreferenceManager.getDefaultSharedPreferences(this);

        mListView = findViewById(R.id.listView);
        //находим View, которое выводит текст Список пуст
        View empty = findViewById(R.id.emptyList);
        mListView.setEmptyView(empty);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (from_to == P.TO_ONE_DATE){
                    Log.d(TAG, "PersonsListActivity onItemClick TO_ONE_DATE from_to = " + from_to);
                    Intent intent = new Intent(PersonsListActivity.this, TimeActivity.class);
                    intent.putExtra(TimeActivity.ID_SQL, id);
                    startActivity(intent);
                }else if (from_to == P.TO_BIORITM){
                    Log.d(TAG, "PersonsListActivity onItemClick TO_BIORITM from_to = " + from_to);
                    //посылаем в интенте id строки с данными в базе данных
                    Log.d(TAG, "position = " + position + " id = " + id);
                    Intent intent = new Intent(PersonsListActivity.this, BioritmActivity.class);
                    intent.putExtra(BioritmActivity.ID_SQL, id);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(intent);
                }else if(from_to == P.TO_DIALOG){
                    //здесь будет код вызова диалога дальнейших дейтвий
                    Log.d(TAG, "PersonsListActivity onItemClick from_to = " + from_to);

                    DialogFragment dialogSelectAction = DialogSelectAction.newInstance(id);
                    dialogSelectAction.show(getSupportFragmentManager(), "dialogSelectAction");

                }else {
                    Log.d(TAG, "PersonsListActivity onItemClick from_to = " + from_to);
                }
                //finish();
            }
        });
        //объявляем о регистрации контекстного меню
        registerForContextMenu(mListView);
    }

    //Если в манифесте установить для android:launchMode значение "singleTop" ,
    // то поисковая активность получает намерение ACTION_SEARCH с вызовом onNewIntent(Intent) ,
    // передавая здесь новое намерение ACTION_SEARCH
    //Алгоритм  в этом случае: onNewIntent-->handleIntent-->doMySearch
    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "PersonsListActivity onNewIntent");
        setIntent(intent);
        handleIntent(intent);
    }

    //обработка интента для поиска, посылаемого системой
    private void handleIntent(Intent intent) {
        Log.d(TAG, "PersonsListActivity handleIntent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "PersonsListActivity handleIntent equals");
            doMySearch(query);

        } else Log.d(TAG, "PersonsListActivity handleIntent not equals");
    }

    //здесь производим поиск по поисковому запросу
    public void doMySearch(String query) {
        Log.d(TAG, "PersonsListActivity doMySearch: String query = " + query);
        //отправляем интент в SeachActivity в составе с  query
        //чтобы затем в onActivityResult запустить Biorytmactivity с правильными данными
        Intent intent = new Intent(PersonsListActivity.this, SeachActivity.class);
        intent.putExtra(SeachActivity.LIST_DATA_QUERY, query);
        startActivityForResult(intent, SEARCH_ACTIVITY);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "PersonsListActivity onStart");
/*
                //пишем новый столбец COLUMN_CHOOSE
                SQLiteDatabase mDb = mPersonDbHelper.getWritableDatabase();
                mDb.execSQL("ALTER TABLE " + TABLE_NAME +
                        " ADD COLUMN " + COLUMN_CHOOSE + " INTEGER");
*/

        //Пишем во все строки столбца COLUMN_CHOOSE значение 0
        SQLiteDatabase mDb = mPersonDbHelper.getWritableDatabase();
        mDb.execSQL("update " + TABLE_NAME +
                " set " + COLUMN_CHOOSE + " =0");
/*
        //Выводим в лог данные нового столбца - пока оставить для памяти
        Cursor mCursor = mDb.query(TABLE_NAME,
                null, null, null, null, null, null);
        int chooseColumnIndex = mCursor.getColumnIndex(COLUMN_CHOOSE);
        try {
            // Проходим через все ряды
            while (mCursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                String choose = mCursor.getString(chooseColumnIndex);
                // Выводим построчно значения  столбца
                Log.d(TAG, "\n" + choose );
            }
        } finally {
            // Всегда закрываем курсор после чтения
            mCursor.close();
        }
        */
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "PersonsListActivity onResume");

        sort = Integer.parseInt(prefSetting.getString("ListSort", "1"));
        isSort = prefSetting.getBoolean("cbSort", false);

        //если была развёрнута строка поиска
        if ((searchView != null) && (searchView.getQuery().toString().length() > 0)) {
            Log.d(TAG, "PersonsListActivity Query " + searchView.getQuery());

            //поиск в базе данных из строки поиска по поисковому запросу query
            mCursor = mPersonDbHelper.searchInSQLite(searchView.getQuery().toString());

            //Выводим список данных на экран с использованием SimpleCursorAdapter
            //showSQLitePersonList(mCursor);

            //если НЕ была развёрнута строка поиска и НЕ был сформирован список результатов поиска
        } else {
            Log.d(TAG, "PersonsListActivity Query = null");

            //получить курсор с отсортированными в соответствии с настройками данными
            mCursor = mPersonDbHelper.getCursorWithSort(isSort,sort);
            //Выводим список данных на экран с использованием SimpleCursorAdapter
            showSQLitePersonList(mCursor);
            //Загружаем сохранённую позицию списка
            loadPos();
            //устанавливаем список в позицию
            mListView.setSelectionFromTop(pos, offset);
            Log.d(TAG, "PersonsListActivity onCreate   pos = " + pos + "  offset = " + offset);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "PersonsListActivity onPause");
        //запоминаем позицию выбранной строки списка
        pos = mListView.getFirstVisiblePosition();
        View v = mListView.getChildAt(0);
        if (v != null) {
            offset = v.getTop() - mListView.getPaddingTop();
        }
        savePos();
        Log.d(TAG, "PersonsListActivity onPause" + "KEY_POS = " +
                pos + "KEY_OFFSET" + offset);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "PersonsListActivity onStop");
    }

    //вызов диалогового окна при нажатии на кнопку Назад
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "PersonsListActivity onBackPressed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "PersonsListActivity onDestroy");
        mCursor.close();
        mPersonDbHelper.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.person, menu);

        // Получите SearchView и настройте настраиваемую для поиска конфигурацию
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        //до SearchView можно добраться так (см menu person.xml)
        searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
        // А можно так - через MenuItem
        // MenuItem searchItem = menu.findItem(R.id.menu_search);
        //SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);

        //Вызов getSearchableInfo() получает объект SearchableInfo который создается из файла XML
        // с возможностью поиска. Когда поисковая конфигурация правильно связана с вашим SearchView
        // SearchView запускает действие с намерением ACTION_SEARCH когда пользователь отправляет запрос
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        //сворачиваем строку поиска (true)
        searchView.setIconifiedByDefault(true);
        //пишем подсказку в строке поиска
        searchView.setQueryHint("Поиск");
        //устанавливаем в панели действий кнопку ( > )для отправки поискового запроса
        searchView.setSubmitButtonEnabled(true);

        //иконка закрытия поиска
        int searchCloseId = searchView.getContext().getResources().
                getIdentifier("android:id/search_close_btn", null, null);
        ImageView searchClose =  searchView.findViewById(searchCloseId);
        searchClose.setImageResource(R.drawable.ic_clear_white_24dp);

        //иконка  поиска
        int searchIconId = searchView.getContext().getResources().
                getIdentifier("android:id/search_button", null, null);
        ImageView searchIcon =  searchView.findViewById(searchIconId);
        searchIcon.setImageResource(R.drawable.ic_search_white_36dp);

        //иконка голосового ввода поиска
        int searchMicId = searchView.getContext().getResources().
                getIdentifier("android:id/search_voice_btn", null, null);
        ImageView searchMic =  searchView.findViewById(searchMicId);
        searchMic.setImageResource(R.drawable.ic_mic_white_36dp);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (!newText.equalsIgnoreCase("")) {
                    //получаем курсор с данными по совпадению в строке поиска с именем персоны
                    mCursor = mPersonDbHelper.searchInSQLite(newText);
                    //Выводим список на экран
                    showSQLitePersonList(mCursor);
                } else {
                    onResume();
                }
                return true;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            //чтобы работала стрелка Назад, а не происходил крах приложения
            case android.R.id.home:
                Log.d(TAG, "Домой");
                onBackPressed();
                finish();
                return true;
            case R.id.action_add:
                Log.d(TAG, "OptionsItem = action_add");
                Intent intentAdd = new Intent(this, NewActivity.class);
                intentAdd.putExtra(NewActivity.REQUEST_CODE, request_code_add);
                startActivity(intentAdd);
                return true;
            case R.id.menu_search:
                Log.d(TAG, "OptionsItem = menu_search");
                //вызываем строку поиска - работает и без этого при щелчке на лупе в панели действий!!?
                //onSearchRequested();
                //Видимо, функция startSearch, зашитая в onSearchRequested срабатывает для виджета автоматически?
                // Или дело в том, что так как в манифесте есть строка android:launchMode="singleTop"
                //всё идёт через onNewIntent
                return true;
            case R.id.action_settings:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentSettings = new Intent(this, PrefActivity.class);
                startActivity(intentSettings);
                return true;

            case R.id.action_help_list_persons:
                Log.d(TAG, "OptionsItem = action_settings");
                Intent intentHelp = new Intent(this, HelpActivity.class);
                intentHelp.putExtra(P.HELP_FROM, P.HELP_FROM_LIST_PERSONS);
                startActivity(intentHelp);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Реализовано два варианта:
    // 1 - если вернулось из NEW_ACTIVITY по нажатию в PersonsListActivity кнопки  Добавить
    // 2 - если вернулось из NEW_ACTIVITY по выбору в PersonsListActivity из контексного меню строки Изменить
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == SEARCH_ACTIVITY) {
                long dataSearch = data.getLongExtra(BioritmActivity.ID_SQL, 0);
                Intent intent = new Intent(PersonsListActivity.this, BioritmActivity.class);
                intent.putExtra(BioritmActivity.ID_SQL, dataSearch);
                startActivity(intent);
                finish();
            }
        }
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, DELETE_ID, 0, "Удалить запись");
        menu.add(0, CHANGE_ID, 0, "Изменить запись");
        menu.add(0, CANCEL_ID, 0, "Отмена");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        // получаем инфу о пункте списка
        final AdapterView.AdapterContextMenuInfo acmi =
                (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        //если выбран пункт Удалить запись
        if (item.getItemId() == DELETE_ID) {
            Log.d(TAG, "PersonsListActivity CM_DELETE_ID");

            AlertDialog.Builder deleteDialog = new AlertDialog.Builder(PersonsListActivity.this);
            deleteDialog.setTitle("Удалить: Вы уверены?");
            deleteDialog.setPositiveButton("Нет", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            deleteDialog.setNegativeButton("Да", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //Удаление записи из базы данных
                    mPersonDbHelper.deletePerson(acmi.id);
                    Log.d(TAG, "PersonsListActivity удалена позиция с ID " + acmi.id);
                    //получаем данные в курсоре
                    // mCursor = mPersonDbHelper.getAllData();
                    //показываем список на экране - тогда не булет сортировки
                    //showSQLitePersonList(mCursor);
                    //чтобы сохранить сортировку, вызываем onResume
                    onResume();
                    //выводим список в лог
                    //mPersonDbHelper.displayDatabaseInfo();
                }
            });
            deleteDialog.show();
            return true;

            //если выбран пункт Изменить запись
        } else if (item.getItemId() == CHANGE_ID) {
            Log.d(TAG, "PersonsListActivity CM_CHANGE_ID");

            Intent intent = new Intent(PersonsListActivity.this, NewActivity.class);
            intent.putExtra(NewActivity.REQUEST_CODE, request_code);
            intent.putExtra(NewActivity.ID_SQL, acmi.id);
            startActivityForResult(intent, NEW_ACTIVITY_CHANGE_REQUEST);

            return true;
        }
        //если ничего не выбрано
        return super.onContextItemSelected(item);
    }

//****************************ФУНКЦИИ***********************//

    void savePos() {
        shp = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor edit = shp.edit();
        edit.putInt(KEY_POS, pos);
        edit.apply();
        edit.putInt(KEY_OFFSET, offset);
        edit.apply();
    }

    void loadPos() {
        shp = getPreferences(MODE_PRIVATE);
        pos = shp.getInt(KEY_POS, 0);
        offset = shp.getInt(KEY_OFFSET, 0);
    }

    //Выводим список данных на экран с использованием SimpleCursorAdapter
    private void showSQLitePersonList(Cursor mCursor) {

        //поручаем активности присмотреть за курсором -вызывает крах если
        // 1-удалить запись 2-добавить запись
        //startManagingCursor(mCursor);

        // формируем столбцы сопоставления
        String[] from = new String[]{PersonTable.COLUMN_NAME,
                PersonTable.COLUMN_DR, PersonTable.COLUMN_PAST_DAYS};
        int[] to = new int[]{R.id.name_list, R.id.was_born, R.id.past_Days};

        // создааем адаптер и настраиваем список
        scAdapter = new SimpleCursorAdapter(this, R.layout.list_name_date, mCursor, from, to);
        mListView.setAdapter(scAdapter);
    }
}