package com.pinyougou.pojo;

import java.io.Serializable;

public class TbInterest implements Serializable{
    private String id;

    private String intersetName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
    }

    public String getIntersetName() {
        return intersetName;
    }

    public void setIntersetName(String intersetName) {
        this.intersetName = intersetName == null ? null : intersetName.trim();
    }
}