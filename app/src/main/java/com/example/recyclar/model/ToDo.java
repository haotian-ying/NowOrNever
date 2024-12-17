package com.example.recyclar.model;

import java.util.UUID;

/**
 * 类 ToDo：表示一个任务的基础信息。
 */
public class ToDo {
    private String id;                // 唯一ID
    private String title;          // 任务标题
    private int hourDue;           // 到期时间的小时
    private int minuteDue;         // 到期时间的分钟
    private boolean repeatEveryDay;
    private boolean setNotification; // 是否设置提醒

    // 默认构造函数
    public ToDo() {

    }

    public ToDo(ToDo t) {
        this.id = t.getId();
        this.title = t.getTitle();
        this.hourDue = t.getHourDue();
        this.minuteDue = t.getMinuteDue();
        this.repeatEveryDay = t.getRepeat();
        this.setNotification = t.hasNotification();
    }

    // 带参数的构造函数
    public ToDo(String title, int hourDue, int minuteDue, boolean repeat,boolean flag) {
        this.title = title;
        this.hourDue = hourDue;
        this.minuteDue = minuteDue;
        this.repeatEveryDay = repeat;
        this.setNotification = flag;
        this.id = generateUniqueId();
    }

    // 给定ID的构造函数
    public ToDo(String id,String title, int hourDue, int minuteDue, boolean repeat,boolean flag) {
        this.id = id;
        this.title = title;
        this.hourDue = hourDue;
        this.minuteDue = minuteDue;
        this.repeatEveryDay = repeat;
        this.setNotification = flag;
        this.id = generateUniqueId();
    }

    // 获取任务ID
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getHourDue() {
        return hourDue;
    }

    public int getMinuteDue() {
        return minuteDue;
    }

    public boolean getRepeat() {
        return repeatEveryDay;
    }

    public boolean hasNotification(){
        return setNotification;
    }

    // 唯一ID生成方法
    /*
    private static int generateUniqueId() {
        int newId;
        do {
            newId = random.nextInt(9000) + 1000;
        } while (generatedIds.contains(newId));

        generatedIds.add(newId);
        return newId;
    }
     */

    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }
}
