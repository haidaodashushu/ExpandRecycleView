package com.wzk.expand;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by 政魁 on 2019/3/14 14:57
 * E-Mail Address：wangzhengkui@yingzi.com
 */
class Utils {
    static LinkedList<ExpandEntity> taskQueue(ExpandEntity expandEntity) {
        if (expandEntity == null) {
            return null;
        }
        LinkedList<ExpandEntity> taskQueue = new LinkedList<>();
        //对树做广度优先遍历
        LinkedList<ExpandEntity> tempQueue = new LinkedList<>();
        tempQueue.offer(expandEntity);
        while (!tempQueue.isEmpty()) {
            ExpandEntity temp = tempQueue.poll();
            List<ExpandEntity> childList = temp.getChildEntity();
            if (childList != null && childList.size() > 0) {
                for (ExpandEntity childTask : childList) {
                    tempQueue.offer(childTask);
                }
            }
            taskQueue.offer(temp);
        }
        taskQueue.remove(expandEntity);
        return taskQueue;
    }
}
