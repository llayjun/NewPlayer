package com.millet.androidlib.Function.SystemContact;

/**
 * Created by llay on 2017/3/21.
 * 联系人界面实体类
 */

public class ContactEntity {
    //名字
    private String name;
    //排序字母
    private String sortKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSortKey() {
        return sortKey;
    }

    public void setSortKey(String sortKey) {
        this.sortKey = sortKey;
    }
}
