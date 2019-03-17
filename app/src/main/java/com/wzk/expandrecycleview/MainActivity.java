package com.wzk.expandrecycleview;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wzk.expand.AbsExpandRecycleViewAdapter;
import com.wzk.expand.ExpandEntity;
import com.wzk.expand.ExpandItemDecoration;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    RecyclerView mRecycleView;
    ExpandRecycleAdapterImpl mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecycleView = findViewById(R.id.recycleView);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new ExpandRecycleAdapterImpl();
        ExpandItemDecoration<TestEnpandEntity> itemDecoration = new ExpandItemDecoration<>(mAdapter);
        itemDecoration.setHorizonLineWidth(24 * 3);
        mRecycleView.addItemDecoration(itemDecoration);
        mRecycleView.setAdapter(mAdapter);
        mAdapter.setData(createExpandEntities());
    }

    private List<TestEnpandEntity> createExpandEntities() {
        List<TestEnpandEntity> expandEntityList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TestEnpandEntity testEnpandEntity = createExpandEntity(0,0);
            expandEntityList.add(testEnpandEntity);
        }
        return expandEntityList;
    }

    private TestEnpandEntity createExpandEntity(int deep, int index) {
        TestEnpandEntity expandEntity = new TestEnpandEntity();
        expandEntity.setText("deep: " + deep + ",index: " + index);
        expandEntity.setDeep(deep);
        if (deep > 5) {
            return expandEntity;
        }
        int nextDeep = ++deep;

        Random random = new Random();
//        int length = random.nextInt(10) % (10 - 5 + 1) + 5;
        int length = 5;
        for (int i = 0; i < length; i++) {
            ExpandEntity childEntity = createExpandEntity(nextDeep, i);
            expandEntity.addChild(childEntity);
        }

        return expandEntity;
    }

    private class ExpandRecycleAdapterImpl extends AbsExpandRecycleViewAdapter<TestEnpandEntity, ExpandRecycleAdapterImpl.MyHolder> {
        int mCurrentPosition = -1;
        @NonNull
        @Override
        public MyHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.expand_item_view, null);
            ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(-1, 56 * 3);
            view.setLayoutParams(params);
            return new MyHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull MyHolder myHolder, int i) {
            super.onBindViewHolder(myHolder, i);
            final TestEnpandEntity expandEntity = getDataByPosition(myHolder.getAdapterPosition());
            if (expandEntity.isExpand()) {
                myHolder.arrowIv.setSelected(true);
            } else {
                myHolder.arrowIv.setSelected(false);
            }
            myHolder.textView.setText(expandEntity.getText());
            if (mCurrentPosition == i) {
                myHolder.checkBox.setSelected(true);
            } else {
                myHolder.checkBox.setSelected(false);
            }
        }

        @Override
        public void expand(MyHolder myHolder) {
            super.expand(myHolder);
            myHolder.arrowIv.setSelected(true);
        }

        @Override
        public void collapse(MyHolder myHolder) {
            super.collapse(myHolder);
            myHolder.arrowIv.setSelected(false);
        }

        class MyHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ImageView checkBox;
            ImageView arrowIv;

            public MyHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.textTv);
                checkBox = itemView.findViewById(R.id.checkIv);
                arrowIv = itemView.findViewById(R.id.arrowIv);
            }
        }
    }

}
