package ru.bartex.jubelee_dialog;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Person  implements Serializable {

    private long person_id;
    private  String person_name;
    private  String person_day;
    private  String person_month;
    private  String person_year;
    private  String person_dr;
    private  String person_past_days;
    private  boolean person_choose;

    //пустой конструктор
    public Person(){    }

    //основной конструктор
    public Person(String name, String day, String month,String year){
        person_name = name;
        person_day = day;
        person_month = month;
        person_year = year;
        person_dr = get_dr(day, month, year);
        person_past_days = get_past_days(day, month, year);
        person_choose = false;
    }

    //конструктор, задающий id
    public Person(long id, String name, String day, String month,String year){
        person_id = id;
        person_name = name;
        person_day = day;
        person_month = month;
        person_year = year;
        person_dr = get_dr(day, month, year);
        person_past_days = get_past_days(day, month, year);
        person_choose = false;
    }

    //получение даты рождения в определнном формате
    public String get_dr (String day, String month,String year){
        //формируем строку даты в формате %s.%s.%s
        String dr = String.format("%s.%s.%s",day,month,year);
        return dr;
    }

    //получение числа прожитых дней  по дате рождения
    public String get_past_days(String day, String month,String year){
        //экземпляр календаря с данными из списка
        Calendar firstCalendar = new GregorianCalendar(Integer.parseInt(year),
                Integer.parseInt(month) - 1,Integer.parseInt(day));
        //получаем дату в милисекундах
        long firstCalendarMillis = firstCalendar.getTimeInMillis();
        long nowTimeMillis = System.currentTimeMillis();
        //количество прошедших дней с даты рождения
        long beenDays = (nowTimeMillis-firstCalendarMillis)/86400000;
        //количество прожитых дней как строка
        String past_days = Long.toString(beenDays);
        return past_days;
    }

    public long getPerson_id() {
        return person_id;
    }

    public void setPerson_id(long person_id) {
        this.person_id = person_id;
    }

    public  String getPerson_name() {
        return person_name;
    }

    public void setPerson_name(String person_name) {
        this.person_name = person_name;
    }

    public  String getPerson_day() {
        return person_day;
    }

    public void setPerson_day(String person_day) {
        this.person_day = person_day;
    }

    public  String getPerson_month() {
        return person_month;
    }

    public void setPerson_month(String person_month) {
        this.person_month = person_month;
    }

    public  String getPerson_year() {
        return person_year;
    }

    public void setPerson_year(String person_year) {
        this.person_year = person_year;
    }

    public  String getPerson_dr() {
        return person_dr;
    }

    public void setPerson_dr(String person_dr) {
        this.person_dr = person_dr;
    }

    public  String getPerson_past_days() {
        return person_past_days;
    }

    public void setPerson_past_days(String person_past_days) {
        this.person_past_days = person_past_days;
    }

    public  boolean isPerson_choose() {
        return person_choose;
    }

    public  void setPerson_choose(boolean person_choose) {
        this.person_choose = person_choose;
    }
}

