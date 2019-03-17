package com.wzk.expand;

import java.util.List;

/**
 * Created by 政魁 on 2019/3/15 10:55
 * E-Mail Address：wangzhengkui@yingzi.com
 */
public interface ExpandAdapterInterface<T extends ExpandEntity> {
    T getDataByPosition(int position);
    void setData(List<T> data);
    List<T> getData();
}
