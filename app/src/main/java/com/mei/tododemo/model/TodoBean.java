package com.mei.tododemo.model;

/**
 * created by meishenbo
 * 2018/12/13
 */
public class TodoBean  extends BaseBean{
    private Integer id;
    private String title;
    private String content;
    private String ctime;
    private String utime;

    public TodoBean() {
    }


    public TodoBean(String title, String content, String ctime, String utime) {
        this.title = title;
        this.content = content;
        this.ctime = ctime;
        this.utime = utime;
    }


    public TodoBean(Integer id, String title, String content, String ctime, String utime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.ctime = ctime;
        this.utime = utime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCtime() {
        return ctime;
    }

    public void setCtime(String ctime) {
        this.ctime = ctime;
    }

    public String getUtime() {
        return utime;
    }

    public void setUtime(String utime) {
        this.utime = utime;
    }

    @Override
    public String toString() {


        return "{" +
                "\"id\":" + id +
                ", \"title\":\"" + title + '\"' +
                ", \"content\":\"" + content + '\"' +
                ", \"ctime\":\"" + ctime + '\"' +
                ", \"utime\":\"" + utime + '\"' +
                '}';
    }
}
