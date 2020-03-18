package ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import ru.bartex.jubelee_dialog.Person;

import static ru.bartex.jubelee_dialog.ru.bartex.jubelee_dialog.data.PersonTable.TABLE_NAME;

/**
 * Created by Андрей on 11.08.2018.
 */
public class PersonDbHelper extends SQLiteOpenHelper{

    public static final String TAG = "33333";

    //Имя файла базы данных
    private static final String DATABASE_NAME = "bioritmDataBase2.db";
     // Версия базы данных. При изменении схемы увеличить на единицу
    private static final int DATABASE_VERSION = 1;



     //Конструктор
    public PersonDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    /**
     * Вызывается при создании базы данных
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Строка для создания таблицы
        String SQL_CREATE_PERSONS_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + PersonTable._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PersonTable.COLUMN_NAME + " TEXT NOT NULL, "
                + PersonTable.COLUMN_DAY + " TEXT NOT NULL, "
                + PersonTable.COLUMN_MONTH + " TEXT NOT NULL, "
                + PersonTable.COLUMN_YEAR + " TEXT NOT NULL, "
                + PersonTable.COLUMN_DR + " TEXT NOT NULL, "
                + PersonTable.COLUMN_PAST_DAYS + " TEXT NOT NULL, "
                + PersonTable.COLUMN_CHOOSE + " TEXT NOT NULL DEFAULT 0);";

        // Запускаем создание таблицы
        db.execSQL(SQL_CREATE_PERSONS_TABLE);
        Log.d(TAG, "Создана база данных  " + DATABASE_NAME);
    }

    /**
     * Вызывается при обновлении СХЕМЫ  базы данных
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Запишем в журнал
        Log.d(TAG, "Обновляемся с версии " + oldVersion + " на версию " + newVersion);
/*
        // Удаляем старую таблицу и создаём новую
        db.execSQL("DROP TABLE IF IT EXISTS " + PersonContract.PersonEntry.TABLE_NAME);
        // Создаём новую таблицу
        onCreate(db);

        @Override
public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    String upgradeQuery = "ALTER TABLE mytable ADD COLUMN mycolumn TEXT;"
    if (oldVersion == 1 && newVersion == 2)
         db.execSQL(upgradeQuery);
}
*/

        String upgradeQuery = "ALTER TABLE persons ADD COLUMN choose INTEGER;";
        if (oldVersion == 1 && newVersion == 2){
            db.execSQL(upgradeQuery);
        }
         String   upgradeChoose = "UPDATE persons SET choose = 0;";


    }

    // Если записей в базе нет, вносим запись
    public void createDefaultPersonIfNeed()  {
        int count = this.getPersonsCount();
        if(count ==0 ) {
            Person person1 = new Person("Анжелина Джоли","4","06", "1975");
            this.addPerson(person1);
            Log.d(TAG, "MyDatabaseHelper.createDefaultPersonIfNeed ... count = " + this.getPersonsCount());
            Person person2 = new Person("Арнольд Шварценеггер","30","07", "1947");
            this.addPerson(person2);
            Log.d(TAG, "MyDatabaseHelper.createDefaultPersonIfNeed ... count = " + this.getPersonsCount());
        }
    }

    //Метод для добавления нового человека в список
    public void addPerson(Person person) {
        Log.d(TAG, "MyDatabaseHelper.addPerson ... " + person.getPerson_name());

        SQLiteDatabase db = getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put(PersonTable.COLUMN_NAME, person.getPerson_name());
        cv.put(PersonTable.COLUMN_DAY, person.getPerson_day());
        cv.put(PersonTable.COLUMN_MONTH, person.getPerson_month());
        cv.put(PersonTable.COLUMN_YEAR, person.getPerson_year());
        cv.put(PersonTable.COLUMN_DR, person.getPerson_dr());
        cv.put(PersonTable.COLUMN_PAST_DAYS, person.getPerson_past_days());
        // вставляем строку
        db.insert(TABLE_NAME, null, cv);
        // закрываем соединение с базой
        db.close();
    }

    //получаем количество записей в базе
    public int getPersonsCount() {
        Log.i(TAG, "MyDatabaseHelper.getPersonsCount ... " );
        String countQuery = "SELECT  * FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    /**
    * Метод для добавления нового человека в список
    */
    public long addPerson(String name,
                          String day,String month,String year, String dr, String pastDays) {
        // создаём объект ContentValues
        ContentValues cv = new ContentValues();
        cv.put(PersonTable.COLUMN_NAME, name);
        cv.put(PersonTable.COLUMN_DAY, day);
        cv.put(PersonTable.COLUMN_MONTH, month);
        cv.put(PersonTable.COLUMN_YEAR, year);
        cv.put(PersonTable.COLUMN_DR, dr);
        cv.put(PersonTable.COLUMN_PAST_DAYS, pastDays);
        // получаем базу данных для записи и пишем
        SQLiteDatabase sd = getWritableDatabase();
        //the row ID of the newly inserted row, or -1 if an error occurred
        long row_id = sd.insert(TABLE_NAME, null, cv);
        // закрываем соединение с базой
        sd.close();
        return  row_id;
    }

    /**
     * Метод обновления строки списка
     */
    public boolean updatePerson(long rowId, String name,
                                String day,String month,String year, String dr, String pastDays) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues updatedValues = new ContentValues();
        updatedValues.put(PersonTable.COLUMN_NAME, name);
        updatedValues.put(PersonTable.COLUMN_DAY, day);
        updatedValues.put(PersonTable.COLUMN_MONTH, month);
        updatedValues.put(PersonTable.COLUMN_YEAR, year);
        updatedValues.put(PersonTable.COLUMN_DR, dr);
        updatedValues.put(PersonTable.COLUMN_PAST_DAYS, pastDays);

        long result = db.update(TABLE_NAME, updatedValues, PersonTable._ID + "=" + rowId, null);
        //db.close();
        return  result > 0;
    }

    /**
     * Удаляет элемент списка
     */
    public void deletePerson(long rowId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, PersonTable._ID + "=" + rowId, null);
        db.close();
    }

    //проверка на существование записи с заданным id
    public boolean isPersonExist(long rowId){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME,
                new String[] { PersonTable._ID},
                PersonTable._ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor.getCount() == 0) {
            Log.d(TAG, "PersonDbHelper  isPersonExist mCursor.getCount() = " + mCursor.getCount() );
            return false;
        }
        mCursor.close();
        return true;
    }

    /**
     * Возвращает курсор с указанной записи
     */
    public Cursor getPerson(long rowId) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME,
                new String[] { PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS },
                PersonTable._ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

      //Возвращает объект Person с данными (для простоты записи)
    public Person getPersonObjectData(long rowId) throws SQLException {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor mCursor = db.query(true, TABLE_NAME,
                new String[] { PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS },
                PersonTable._ID + "=" + rowId,
                null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        // Узнаем индекс каждого столбца
        int nameColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_NAME);
        int dayColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_DAY);
        int monthColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_MONTH);
        int yearColumnIndex = mCursor.getColumnIndex(PersonTable.COLUMN_YEAR);

        Person person = new Person(rowId, mCursor.getString(nameColumnIndex),
                mCursor.getString(dayColumnIndex),
                mCursor.getString(monthColumnIndex),
                mCursor.getString(yearColumnIndex) );
        //закрываем курсор
        mCursor.close();
        return person;
    }

        //список Person с отмеченными (или нет) галками выбора
    public ArrayList<Person> getAllContactsChoose() {
        ArrayList<Person> contactList = new ArrayList<Person>();
        String selectQuery = "SELECT  * FROM " + TABLE_NAME;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        Log.i(TAG, "MyDatabaseHelper.getAllPersons IN");

        if (cursor.moveToFirst()) {
            do {
                Person contact = new Person();
                // Используем индекс для получения строки или числа
                contact.setPerson_id(cursor.getLong(0));
                contact.setPerson_name(cursor.getString(1));
                contact.setPerson_day(cursor.getString(2));
                contact.setPerson_month(cursor.getString(3));
                contact.setPerson_year(cursor.getString(4));
                contact.setPerson_dr(cursor.getString(5));
                contact.setPerson_past_days(cursor.getString(6));
                if (cursor.getInt(7) ==0 ){
                    contact.setPerson_choose(false);
                }else contact.setPerson_choose(true);
                contactList.add(contact);

                Log.i(TAG, "  id = " + contact.getPerson_id()+
                        "  name = " + contact.getPerson_name() +
                        "  choose  = " + contact.isPerson_choose());

            } while (cursor.moveToNext());
        }
        Log.i(TAG, "MyDatabaseHelper.getAllPersons OUT");
        for (int i = 0; i< contactList.size(); i++){
            Log.i(TAG, "  id = " + contactList.get(i).getPerson_id()+
                    "  name = " + contactList.get(i).getPerson_name() +
                    "  choose  = " + contactList.get(i).isPerson_choose());
        }
        Log.d(TAG, "MyDatabaseHelper.getAllPersons ... размер списка = " + contactList.size());

        cursor.close();
        return contactList;
    }

    // Обновить данные в столбце Количество прожитых дней
    public void updatePastDays() {
        SQLiteDatabase sd = this.getWritableDatabase();
        Cursor cursor =  getAllData();
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(PersonTable._ID);
            int dayColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_DAY);
            int monthColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_MONTH);
            int yearColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_YEAR);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentDay = cursor.getString(dayColumnIndex);
                String currentMonth = cursor.getString(monthColumnIndex);
                String currentYear = cursor.getString(yearColumnIndex);

                //экземпляр календаря с данными из списка
                GregorianCalendar firstCalendar = new GregorianCalendar(Integer.parseInt(currentYear),
                        Integer.parseInt(currentMonth) - 1,Integer.parseInt(currentDay));
                //получаем дату в милисекундах
                long firstCalendarMillis = firstCalendar.getTimeInMillis();
                long nowTimeMillis = System.currentTimeMillis();
                //количество прошедших дней с даты рождения
                long beenDays = (nowTimeMillis-firstCalendarMillis)/86400000;
                //количество прожитых дней как строка
                String past_days = Long.toString(beenDays);

                ContentValues updatedValues = new ContentValues();
                updatedValues.put(PersonTable.COLUMN_PAST_DAYS, past_days);

                sd.update(TABLE_NAME,
                        updatedValues,
                        "_id = ?",
                        new String[] {Integer.toString(currentID)});
            }
        //закрываем курсор
        cursor.close();
    }

    // получить курсор с данными из таблицы TABLE_NAME
    public Cursor getAllData() {
        SQLiteDatabase sd = this.getReadableDatabase();
        return sd.query(TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, null);
    }

    // получить курсор с данными из таблицы TABLE_NAME, сортировка прямая по COLUMN_NAME
    public Cursor getAllDataSortNameUp() {
        SQLiteDatabase sd = this.getReadableDatabase();
        return sd.query(TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_NAME);
    }

    // получить курсор с данными из таблицы TABLE_NAME, сортировка обратная по COLUMN_NAME
    public Cursor getAllDataSortNameDown() {
        SQLiteDatabase sd = this.getReadableDatabase();
        return sd.query(TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_NAME + " DESC");
    }

    // получить курсор с данными из таблицы TABLE_NAME, сортировка по прожитым дням прямая
    public Cursor getAllDataSortDateUp() {
        SQLiteDatabase sd = this.getReadableDatabase();
        return sd.query(TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_PAST_DAYS);
    }

    // получить курсор с данными из таблицы TABLE_NAME, сортировка по прожитым дням обратная
    public Cursor getAllDataSortDateDown() {
        SQLiteDatabase sd = this.getReadableDatabase();
        return sd.query(TABLE_NAME,
                new String[]{PersonTable._ID,PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DAY,PersonTable.COLUMN_MONTH,PersonTable.COLUMN_YEAR,
                        PersonTable.COLUMN_DR,PersonTable.COLUMN_PAST_DAYS},
                null, null, null, null, PersonTable.COLUMN_PAST_DAYS + " DESC");
    }


    //метод поиска в базе данных из строки поиска по поисковому запросу query
    public Cursor searchInSQLite (String query){

        query = query.toLowerCase();

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                TABLE_NAME,  //имя таблицы, к которой передается запрос
                new String[] {          //список имен возвращаемых полей
                        PersonTable._ID,
                        PersonTable.COLUMN_NAME,
                        PersonTable.COLUMN_DR,
                        PersonTable.COLUMN_PAST_DAYS },
                PersonTable.COLUMN_NAME + " LIKE" + "'%" + query + "%'", // условие выбора
                null,  //значения аргументов фильтра
                null,//фильтр для группировки
                null, //фильтр для группировки, формирующий выражение HAVING
                PersonTable.COLUMN_NAME ); //порядок сортировки

        if (cursor != null) {
            Log.d(TAG, "cursor1.getCount() = " + cursor.getCount() );
            while (cursor.moveToNext()) {
                String s = cursor.getString(cursor.getColumnIndex(PersonTable.COLUMN_NAME));
                Log.d(TAG, "Найдена строка " + s);
            }
        }else Log.d(TAG, "cursor1 = " + "null");

        return cursor;
    }

    //вывод в лог всех строк базы
    public void displayDatabaseInfo() {
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = this.getReadableDatabase();

        // Зададим условие для выборки - список столбцов
        String[] projection = {
                PersonTable._ID,
                PersonTable.COLUMN_NAME,
                PersonTable.COLUMN_DR,
                PersonTable.COLUMN_PAST_DAYS};

        // Делаем запрос
        Cursor cursor = db.query(
                TABLE_NAME,   // таблица
                projection,            // столбцы
                null,                  // столбцы для условия WHERE
                null,                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки


        try {

            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(PersonTable._ID);
            int nameColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_NAME);
            int drColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_DR);
            int pastDaysColumnIndex = cursor.getColumnIndex(PersonTable.COLUMN_PAST_DAYS);

            // Проходим через все ряды
            while (cursor.moveToNext()) {
                // Используем индекс для получения строки или числа
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentDr = cursor.getString(drColumnIndex);
                int currentPastDays = cursor.getInt(pastDaysColumnIndex);

                // Выводим построчно значения каждого столбца
                Log.d(TAG, "\n" + currentID + " - " +
                        currentName + " - " +
                        currentDr + " - " +
                        currentPastDays);
            }
        } finally {
            // Всегда закрываем курсор после чтения
            cursor.close();
        }
    }

    //расчёт количества совместно прожитых миллисекунд
    public long getMillisForTwo(long id_first, long id_second){

        Person person;
        person = this.getPersonObjectData(id_first);
        Log.d(TAG, "person1 past_days = " + person.getPerson_past_days()+
                "  person1 year" + person.getPerson_year()+
                "  person1 month" + person.getPerson_month()+
                "  person1 day" + person.getPerson_day());

        //экземпляр календаря с данными персоны1
        GregorianCalendar firstCalendar = new GregorianCalendar(
                Integer.parseInt(person.getPerson_year()),
                Integer.parseInt(person.getPerson_month()) - 1,
                Integer.parseInt(person.getPerson_day()));
        //получаем дату в милисекундах
        long firstCalendarMillis = firstCalendar.getTimeInMillis();

        person = this.getPersonObjectData(id_second);
        Log.d(TAG, "person2 past_days = " + person.getPerson_past_days()+
                "  person2 year" + person.getPerson_year()+
                "  person2 month" + person.getPerson_month()+
                "  person2 day" + person.getPerson_day());

        //экземпляр календаря с данными персоны2
        GregorianCalendar secondCalendar = new GregorianCalendar(
                Integer.parseInt(person.getPerson_year()),
                Integer.parseInt(person.getPerson_month()) - 1,
                Integer.parseInt(person.getPerson_day()));
        //получаем дату в милисекундах
        long secondCalendarMillis = secondCalendar.getTimeInMillis();

        //текущее время в миллисекундах
        long nowTimeMillis = System.currentTimeMillis();

        //количество совместно прожитых миллисекунд
        long beenMillis = nowTimeMillis -firstCalendarMillis +
                nowTimeMillis - secondCalendarMillis;

        Log.d(TAG, "firstDays = " + (nowTimeMillis -firstCalendarMillis)/86400000  +
                "  secondDays = " + (nowTimeMillis -secondCalendarMillis)/86400000 +
                "  days = " + ((nowTimeMillis -firstCalendarMillis)/86400000 +
                (nowTimeMillis -secondCalendarMillis)/86400000));
        return beenMillis;
    }

    //получить курсор с отсортированными в соответствии с настройками данными
    public Cursor getCursorWithSort(boolean isSort, int sort){

        Cursor mCursor;
        //проводим сортировку списка
        if (isSort) {
            switch (sort) {
                case 1:
                    Log.d(TAG, "PersonsListActivity Сортировка по имени по возрастанию");
                    //получаем данные в курсоре
                    mCursor = this.getAllDataSortNameUp();
                    break;
                case 2:
                    Log.d(TAG, "PersonsListActivity Сортировка по имени по убыванию");
                    //получаем данные в курсоре
                    mCursor = this.getAllDataSortNameDown();
                    break;
                case 3:
                    Log.d(TAG, "PersonsListActivity Сортировка по дате по возрастанию ");
                    //получаем данные в курсоре
                    mCursor = this.getAllDataSortDateUp();
                    break;
                case 4:
                    Log.d(TAG, "PersonsListActivity Сортировка по дате по убыванию ");
                    //получаем данные в курсоре
                    mCursor = this.getAllDataSortDateDown();
                    break;
                default:
                    Log.d(TAG, "PersonsListActivity default: сортировка по имени вверх");
                    //получаем данные в курсоре
                    mCursor = this.getAllDataSortNameUp();
                    break;
            }
        } else {
            Log.d(TAG, "PersonsListActivity Без сортировки");
            //получаем данные в курсоре
            mCursor = this.getAllData();
        }
        return mCursor;
    }

    //получаем ID по имени
    public long getIdFromName(String name){
        long currentID;
        // Создадим и откроем для чтения базу данных
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                TABLE_NAME,   // таблица
                new String[] { PersonTable._ID},            // столбцы
                PersonTable.COLUMN_NAME + "=?" ,                  // столбцы для условия WHERE
                new String[] {name},                  // значения для условия WHERE
                null,                  // Don't group the rows
                null,                  // Don't filter by row groups
                null);                   // порядок сортировки

        if ((cursor != null) && (cursor.getCount()!=0)){
            cursor.moveToFirst();
            // Узнаем индекс каждого столбца
            int idColumnIndex = cursor.getColumnIndex(PersonTable._ID);
            // Используем индекс для получения строки или числа
            currentID = cursor.getLong(idColumnIndex);
        }else {
            currentID = -1;
        }
        Log.d(TAG, "getIdFromName currentID = " + currentID);
        cursor.close();
        return currentID;
    }

}
