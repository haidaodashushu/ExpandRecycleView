package com.wzk.expand;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import static com.wzk.expand.Utils.taskQueue;

/**
 * Created by 政魁 on 2019/3/14 12:37
 * E-Mail Address：wangzhengkui@yingzi.com
 */
public abstract class AbsExpandRecycleViewAdapter<T extends ExpandEntity, H extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<H> implements ExpandAdapterInterface<T> {
    List<T> mData;
    OnExpandItemClickListener<T> mListener;

    @Override
    public void setData(List<T> data) {
        this.mData = data;
        notifyDataSetChanged();
    }

    public T getDataByPosition(int position) {
        if (position >= 0 && position < mData.size())
            return mData.get(position);
        else return null;
    }

    @Override
    public List<T> getData() {
        return mData;
    }

    @Override
    public void onBindViewHolder(@NonNull final H myHolder, int i) {
        final ExpandEntity expandEntity = mData.get(myHolder.getAdapterPosition());
        myHolder.itemView.setTag(expandEntity);
        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (expandEntity.isExpand()) {
                    collapse(myHolder);
                } else {
                    //展开：
                    expand(myHolder);
                }
            }
        });
    }

    /**
     * 展开
     *
     * @param myHolder
     */
    public void expand(H myHolder) {
        int position = myHolder.getAdapterPosition();
        T expandEntity = mData.get(position);
        expandEntity.setExpand(true);
        List<ExpandEntity> childEntity = expandEntity.getChildEntity();
        if (childEntity != null && childEntity.size() > 0) {
            //首先找到该entity在mData中的位置
            int index = position;
            for (ExpandEntity entity : childEntity) {
                mData.add(++index, (T) entity);
            }
            notifyItemRangeInserted(position + 1, childEntity.size());
        }

        if (mListener != null) {
            mListener.expand(position, expandEntity);
        }
    }

    /**
     * 合起
     *
     * @param myHolder
     */
    public void collapse(H myHolder) {
        //合起
        int position = myHolder.getAdapterPosition();
        T expandEntity = mData.get(position);
        expandEntity.setExpand(false);
        List<ExpandEntity> childEntity = taskQueue(expandEntity);
        if (childEntity != null && childEntity.size() > 0) {
            //首先找到该entity在mData中的位置
            for (ExpandEntity entity : childEntity) {
                entity.setExpand(false);
                mData.remove(entity);
            }
//            notifyItemRangeRemoved(position + 1, childEntity.size());
            notifyDataSetChanged();
        }

        if (mListener != null) {
            mListener.collapse(position, expandEntity);
        }
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public void setExpandItemClickListener(OnExpandItemClickListener<T> listener) {
        mListener = listener;
    }

    public interface OnExpandItemClickListener<T> {
        void expand(int position, T data);

        void collapse(int position, T data);
    }

}

