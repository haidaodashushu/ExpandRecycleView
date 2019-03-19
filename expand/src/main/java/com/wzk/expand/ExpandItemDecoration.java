package com.wzk.expand;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.List;

/**
 * Created by 政魁 on 2019/3/14 15:29
 * E-Mail Address：wangzhengkui@yingzi.com
 */
public class ExpandItemDecoration<T extends ExpandEntity> extends RecyclerView.ItemDecoration {
    Paint mPaint;
    int horizonLineWidth = 12 * 3;
    int verticalDeltaHeight = 18 * 3;
    int lineColor = 0xFF008577;
    ExpandAdapterInterface<T> mExpandAdapter;

    public ExpandItemDecoration(ExpandAdapterInterface<T> expandAdapter) {
        mExpandAdapter = expandAdapter;
        mPaint = new Paint();
        mPaint.setColor(lineColor);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2);
    }

    public void setHorizonLineWidth(int leftMargin) {
        horizonLineWidth = leftMargin;
    }

    public void setVerticalDeltaHeight(int verticalDeltaHeight) {
        this.verticalDeltaHeight = verticalDeltaHeight;
    }

    private void setLineColor(int color) {
        this.lineColor = color;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int itemPosition = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewAdapterPosition();
        //拿到该itemPosition对应的数据实体
        ExpandEntity dataByPosition = mExpandAdapter.getDataByPosition(itemPosition);
        if (dataByPosition == null) {
            dataByPosition = (ExpandEntity) view.getTag();
        }
        int deep = dataByPosition == null ? 0 : dataByPosition.getDeep();
        outRect.set(horizonLineWidth * deep, outRect.top, outRect.right, outRect.bottom);
        Log.i("ExpandItemDecoration", "getItemOffsets: " + itemPosition + "，" + deep + ", "+horizonLineWidth * deep);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);

        //划线：
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        int first = layoutManager.findFirstVisibleItemPosition();
        int last = layoutManager.findLastVisibleItemPosition();
        //画水平线
        drawHorizontal(c, first, last, parent);
        //画竖直线
        //首先判断当前节点是否帮助其父节点画线，因为父节点可能已经滑出屏幕了
        drawParentVertical(c, first, last, parent);
        //再画自己的线
        drawVertical(c, first, last, parent);
    }

    /**
     * 这里会有多个父节点需要处理
     *
     * @param canvas
     * @param first
     * @param last
     * @param parent
     */
    private void drawHorizontal(Canvas canvas, int first, int last, RecyclerView parent) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        for (int i = first; i <= last; i++) {
            ExpandEntity expandEntity = mExpandAdapter.getDataByPosition(i);
            //找到对应的View
            View view = layoutManager.findViewByPosition(i);
            int deep = expandEntity.getDeep();
            //画水平线
            if (deep > 0) {
                int startX = view.getLeft() - horizonLineWidth / 2;
                startX = getLWithPadding(startX, parent);
                int y = view.getTop() + view.getHeight() / 2;
                //不需要画了
                if (y >= parent.getPaddingTop() && y <= (parent.getBottom() - parent.getPaddingBottom())) {
                    canvas.drawLine(startX, y, view.getLeft(), y, mPaint);
                }
            }

        }
    }

    private void drawParentVertical(Canvas canvas, int first, int last, RecyclerView parent) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        List<T> expandEntities = mExpandAdapter.getData();

        ExpandEntity firstEntity = mExpandAdapter.getDataByPosition(first);
        if (firstEntity.getDeep() <= 0) {
            //不需要帮助
            return;
        }
        int startX, startY, endX, endY = 0;

        for (int i = first; i <= last; i++) {
            ExpandEntity expandEntity = mExpandAdapter.getDataByPosition(i);
            View managerViewByPosition = layoutManager.findViewByPosition(i);
            int firstLeft = managerViewByPosition.getLeft();
            ExpandEntity parentEntity = expandEntity.getParentEntity();
            if (parentEntity == null) {
                continue;
            }
            int index = expandEntities.indexOf(parentEntity);
            //如果不在屏幕内，则start
            if (index < first) {
                startX = firstLeft - horizonLineWidth / 2;
                endX = firstLeft - horizonLineWidth / 2;
                startY = layoutManager.findViewByPosition(first).getTop();
                endY = managerViewByPosition.getTop() + managerViewByPosition.getHeight() / 2;
                canvas.drawLine(startX, startY, endX, endY, mPaint);
            }
        }
    }

    private void drawVertical(Canvas canvas, int first, int last, RecyclerView parent) {
        LinearLayoutManager layoutManager = (LinearLayoutManager) parent.getLayoutManager();
        List<T> expandEntities = mExpandAdapter.getData();
        for (int i = first; i <= last; i++) {
            ExpandEntity expandEntity = mExpandAdapter.getDataByPosition(i);
            //找到对应的View
            View view = layoutManager.findViewByPosition(i);
            boolean isExpand = expandEntity.isExpand();
            if (!isExpand) {
                continue;
            }
            int startX, startY, endX, endY = 0;
            startX = view.getLeft() + horizonLineWidth / 2;
            endX = view.getLeft() + horizonLineWidth / 2;
            startY = view.getBottom() - verticalDeltaHeight;

            //计算最后一个子View在屏幕上的位置,以便获取endY的值
            List<ExpandEntity> childEntity = expandEntity.getChildEntity();
            if (childEntity == null || childEntity.size() == 0) {
                continue;
            }
            int lastVisibleChild = 0;
            for (int j = 0; j < childEntity.size(); j++) {
                ExpandEntity child = childEntity.get(j);
                int index = expandEntities.indexOf(child);
                if (index >= first || index <= last) {
                    lastVisibleChild = index;
                } else {
                    break;
                }
            }
            View lastVisiableChildView = layoutManager.findViewByPosition(lastVisibleChild);
            //如果lastVisiableChildView为null。那就说明该view已经滑出了recycleView，则这是直接画到底部即可
            if (lastVisiableChildView == null) {
                endY = parent.getBottom();
            } else {
                endY = lastVisiableChildView.getTop() + lastVisiableChildView.getHeight() / 2;
            }
            startX = getLWithPadding(startX, parent);
            startY = getTWithPadding(startY, parent);
            endX = getRWithPadding(endX, parent);
            endY = getBWithPadding(endY, parent);
            canvas.drawLine(startX, startY, endX, endY, mPaint);

        }
    }

    private int getLWithPadding(int l, RecyclerView recyclerView) {
        return Math.max(recyclerView.getPaddingLeft(), l);
    }

    private int getTWithPadding(int t, RecyclerView recyclerView) {
        return Math.max(recyclerView.getPaddingTop(), t);
    }

    private int getRWithPadding(int r, RecyclerView recyclerView) {
        return Math.min(recyclerView.getRight() - recyclerView.getPaddingRight(), r);
    }

    private int getBWithPadding(int b, RecyclerView recyclerView) {
        return Math.min(recyclerView.getBottom() - recyclerView.getPaddingBottom(), b);
    }
}
