package com.alifabdulrahman.malaysiakinireader.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;

public class ArticleData implements Parcelable {
    private String title;
    private ArrayList<String> contentSentence;
    private String link, author;
    private boolean paidNews;
    private boolean readNews;
    private Date publishDate;

    public ArticleData(String link){ this.link = link; }

    public ArticleData(ArticleData articleData){
        this.title = articleData.title;
        this.contentSentence = new ArrayList<>(articleData.getContent());
        this.link = articleData.link;
        this.author = articleData.author;
        this.paidNews = articleData.paidNews;
        this.readNews = articleData.readNews;
        this.publishDate = articleData.publishDate;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setLink(String link) { this.link = link; }

    public void setContent(ArrayList<String> content) { this.contentSentence = content; }

    public void setPaidNews(boolean paidNews) { this.paidNews = paidNews; }

    public String getTitle(){
        return title;
    }

    public ArrayList<String> getContent() { return contentSentence; }

    public String getLink(){ return link; }

    public boolean getPaidNews(){ return paidNews; }

    public boolean getReadNews(){ return readNews; }

    public void removeReadNews(int i) { }

    public void setReadNews(boolean readNews) { this.readNews = readNews; }

    public void setPublishDate(Date publishDate) { this.publishDate = publishDate; }

    public Date getPublishDate() { return publishDate; }

    public void setAuthor(String author) { this.author = author; }

    public String getAuthor() { return author; }

    @Override
    public String toString(){
        return this.title + this.contentSentence;
    }

    @Override
    public boolean equals(Object o){
        if(this == o)
            return true;
        if(o == null)
            return false;
        if(getClass() != o.getClass())
            return false;
        ArticleData data = (ArticleData) o;

        return Objects.equals(link, data.link);
//        return Objects.equals(link, data.link) && Objects.equals(title, data.title) && Objects.equals(contentSentence, data.contentSentence);
    }

    //write object values to parcel for storage
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(title);
        dest.writeList(contentSentence);
        dest.writeString(link);
        dest.writeValue(paidNews);
        dest.writeValue(readNews);
        dest.writeValue(publishDate);
        dest.writeString(author);
    }

    public ArticleData(Parcel parcel){
        //read and set saved values from parcel
        title = parcel.readString();
        contentSentence = parcel.readArrayList(null);
        link = parcel.readString();
        paidNews = (boolean) parcel.readValue(null);
        readNews = (boolean) parcel.readValue(null);
        publishDate = (Date) parcel.readValue(null);
        author = parcel.readString();
    }

    //creator - used when un-parceling our parcle (creating the object)
    public static final Parcelable.Creator<ArticleData> CREATOR = new Parcelable.Creator<ArticleData>(){

        @Override
        public ArticleData createFromParcel(Parcel parcel) {
            return new ArticleData(parcel);
        }

        @Override
        public ArticleData[] newArray(int size) {
            return new ArticleData[0];
        }
    };

    //return hashcode of object
    public int describeContents() {
        return hashCode();
    }

    public ArrayList<ArticleData> removeDuplicates(ArrayList<ArticleData> list){
        ArrayList<ArticleData> newList = new ArrayList<>();
        for(ArticleData element : list){
            if(!newList.contains(element)){
                newList.add(element);
            }
        }

        return newList;
    }



}
