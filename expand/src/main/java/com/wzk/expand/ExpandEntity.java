package com.wzk.expand;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 政魁 on 2019/3/14 12:39
 * E-Mail Address：wangzhengkui@yingzi.com
 */

public class ExpandEntity {
    ExpandEntity mParentEntity;
    List<ExpandEntity> childEntity;
    int deep;
    boolean isExpand;

    public void addChild(ExpandEntity expandEntity) {
        if (childEntity == null) {
            childEntity = new ArrayList<>();
        }
        expandEntity.setParentEntity(this);
        childEntity.add(expandEntity);
    }

    public ExpandEntity getParentEntity() {
        return mParentEntity;
    }

    public void setParentEntity(ExpandEntity parentEntity) {
        this.mParentEntity = parentEntity;
    }

    public boolean isExpand() {
        return isExpand;
    }

    public void setExpand(boolean expand) {
        isExpand = expand;
    }

    public List<ExpandEntity> getChildEntity() {
        return childEntity;
    }

    public void setChildEntity(List<ExpandEntity> childEntity) {
        this.childEntity = childEntity;
    }

    public int getDeep() {
        return deep;
    }

    public void setDeep(int deep) {
        this.deep = deep;
    }

}
