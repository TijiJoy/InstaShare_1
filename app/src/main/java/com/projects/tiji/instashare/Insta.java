package com.projects.tiji.instashare;

/**
 * Created by Tg on 11-11-2017.
 */

public class Insta {
    private String  title,desc,image,username;
    public Insta(){

    }
    public Insta(String title,String desc,String image,String username)
    {
        this.title=title;
        this.desc=desc;
        this.image=image;
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTitle()
    {
        return title;

    }
    public String  getImage()
    {
        return  image;
    }
    public String  getDesc()
    {
        return desc;
    }
    public void setTitle(String title)
    {
        this.title=title;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
