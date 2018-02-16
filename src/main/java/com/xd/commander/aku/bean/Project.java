package com.xd.commander.aku.bean;

import cn.bmob.v3.BmobObject;

/**
 * @author Administrator 21:12 2017/12/29
 */
public class Project extends BmobObject{
    @Override
    public String toString() {
        return "Project{" +
                "Created='" + Created + '\'' +
                ", Description='" + Description + '\'' +
                ", Forks=" + Forks +
                ", Id=" + Id +
                ", Issues=" + Issues +
                ", Language='" + Language + '\'' +
                ", License='" + License + '\'' +
                ", Link='" + Link + '\'' +
                ", Min_SDK='" + Min_SDK + '\'' +
                ", Name='" + Name + '\'' +
                ", Owner='" + Owner + '\'' +
                ", Repo='" + Repo + '\'' +
                ", Stargazers=" + Stargazers +
                ", Tag='" + Tag + '\'' +
                ", Updated='" + Updated + '\'' +
                ", Version='" + Version + '\'' +
                ", Watchers=" + Watchers +
                '}';
    }

    private String Created;
    private String Description;
    private Integer Forks;
    private Integer Id;
    private Integer Issues;
    private String Language;
    private String License;
    private String Link;
    private String Min_SDK;
    private String Name;
    private String Owner;
    private String Repo;
    private Integer Stargazers;
    private String Tag;
    private String Updated;
    private String Version;
    private Integer Watchers;

    public String getCreated() {
        return Created;
    }

    public void setCreated(String Created) {
        this.Created = Created;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String Description) {
        this.Description = Description;
    }

    public int getForks() {
        return Forks;
    }


    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    public int getIssues() {
        return Issues;
    }

    public String getLanguage() {
        return Language;
    }


    public String getLicense() {
        return License;
    }


    public String getLink() {
        return Link;
    }


    public String getMin_SDK() {
        return Min_SDK;
    }


    public String getName() {
        return Name;
    }

    public void setName(String Name) {
        this.Name = Name;
    }

    public String getOwner() {
        return Owner;
    }

    public void setOwner(String Owner) {
        this.Owner = Owner;
    }

    public String getRepo() {
        return Repo;
    }


    public int getStargazers() {
        return Stargazers;
    }


    public String getTag() {
        return Tag;
    }

    public void setTag(String Tag) {
        this.Tag = Tag;
    }

    public String getUpdated() {
        return Updated;
    }


    public String getVersion() {
        return Version;
    }

    public void setVersion(String Version) {
        this.Version = Version;
    }

    public int getWatchers() {
        return Watchers;
    }

}
