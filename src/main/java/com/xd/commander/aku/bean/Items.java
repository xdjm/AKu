package com.xd.commander.aku.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author Administrator 0:05 2018/1/1
 */
public class Items extends BmobObject{
    @Override
    public String toString() {
        return "Items{" +
                "Tag='" + Tag + '\'' +
                ", Name='" + Name + '\'' +
                ", Author='" + Author + '\'' +
                ", Time='" + Time + '\'' +
                ", Desc='" + Desc + '\'' +
                ", Id=" + Id +
                '}';
    }

    public Items(String tag, String name, String author, String time, String desc, Integer id) {
        Tag =tag;
        Name = name;
        Author = author;
        Time = time;
        Desc = desc;
        Id = id;
    }

    public String Tag,Name,Author,Time,Desc;
    public Integer Id;
}
