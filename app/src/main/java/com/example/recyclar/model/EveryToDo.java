package com.example.recyclar.model;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EveryToDo extends ToDo {
    private String every_id;                 // 唯一ID
    //private int idToDo;             // 所属 ToDo 的ID
    private String date;            // 日期 (格式: yyyy-MM-dd)
    private boolean isDone;         // 是否完成

    // 默认构造函数
    public EveryToDo() {

    }
    // 给定ID
    public EveryToDo(ToDo t,String i, String d, boolean isD) {
        super(t);
        this.every_id = i;
        this.date = d;
        this.isDone = isD;
    }

    // 带参数的构造函数
    public EveryToDo(ToDo t, String date, boolean isDone) {
        super(t);
        this.every_id = generateUniqueId();
        // this.idToDo = idToDo;
        this.date = date;
        this.isDone = isDone;
    }

    // 获取 ID
    public String getEveryId() {
        return every_id;
    }

    /*
    public int getIdToDo() {
        return ;
    }
    */

    public String getDate() {
        return date;
    }

    public boolean isDone() {
        return isDone;
    }

    public void toggleDoneStatus(){
        isDone = !isDone;
    }

    // 唯一ID生成方法
    /*
    private static int generateUniqueId() {
        int newId;
        do {
            newId = new Random().nextInt(9000) + 1000;
        } while (generatedIds.contains(newId));

        generatedIds.add(newId);
        return newId;
    }
     */



    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    // 静态集合保存已生成的ID
    private static Set<Integer> generatedIds = new HashSet<>();
}