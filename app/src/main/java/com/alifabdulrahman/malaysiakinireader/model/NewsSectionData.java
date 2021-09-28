package com.alifabdulrahman.malaysiakinireader.model;

public class NewsSectionData {

    private String sectionName;
    private String sectionLink;

    public NewsSectionData(String sectionName, String sectionLink){
        this.sectionName = sectionName;
        this.sectionLink = sectionLink;
    }

    public void setSectionLink(String sectionLink) {
        this.sectionLink = sectionLink;
    }

    public void setSectionName(String sectionName) {
        this.sectionName = sectionName;
    }

    public String getSectionName(){
        return sectionName;
    }

    public String getSectionLink(){
        return sectionLink;
    }

    @Override
    public String toString(){
        return this.sectionName;
    }
}
