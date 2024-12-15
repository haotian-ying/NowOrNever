package com.example.recyclar;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class Task implements Serializable {
    private String id;
    private String title;
    private String description;
    private int priority;
    private int status;
    private String date;

    // 用于保存已生成的ID
    // ToDo 写入 SharedPereference
    private static Set<Integer> generatedIds = new HashSet<>();
    private static Random random = new Random();

    // 构造函数
    public Task(String t, String desc, int p, String date, int s) {
        this.title = t;
        this.description = desc;
        this.priority = p;
        this.date = date;
        this.status = s;

        // 生成唯一ID
        this.id = generateUniqueId();
    }

    public Task(String id,String t, String desc, int p, String date, int s) {
        this.id = id;
        this.title = t;
        this.description = desc;
        this.priority = p;
        this.date = date;
        this.status = s;
    }

    // 生成唯一ID
    /*
    private static int generateUniqueId() {
        int newId;
        do {
            // 生成一个随机ID，假设ID的范围是 1000 到 9999
            newId = random.nextInt(9000) + 1000;
        } while (generatedIds.contains(newId));  // 如果ID已存在，重新生成

        // 将新生成的ID加入集合
        generatedIds.add(newId);
        return newId;
    }
     */

    private static String generateUniqueId() {
        return UUID.randomUUID().toString();
    }

    // 获取任务的ID
    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public int getPriority() {
        return this.priority;
    }

    public String getDate() {
        return this.date;
    }

    public int getStatus() {
        return this.status;
    }
}
