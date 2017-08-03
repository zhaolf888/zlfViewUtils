package com.zlf.zlfviewutils;/**
 * Created by Administrator on 2017/6/17 0017.
 */

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * 作者：zhaolifeng
 * 时间：2017/06/17 10:56
 * 描述：
 */
public abstract class MyBaseRecyclerAdapter<T> extends RecyclerView.Adapter<MyBaseRecyclerAdapter.InnerBaseViewHolder> {
    protected Context mContext;
    protected int layoutId;
    protected List<T> data;
    protected LayoutInflater inflater;
    protected HashMap<Integer, Integer> layoutIds;
    private final static int EMPTY_TYPE = 40000; //数据为空时返回这个集合
    private EmptyView mEmptyView = new EmptyView();
    private EmptyShowWhat mEmptyShowWhat = EmptyShowWhat.SHOW_ALL; //默认头尾布局全部显示
    private int headerType = 20000;
    private int footerType = 30000;
    private HeaderAndFooterList mHeaderViews;
    private HeaderAndFooterList mFooterViews;
    private int headerCount; //承接变量，
    private int footerCount; //承接变量
    private onItemClickListener onItemClickListener;
    private onItemLongClickListener onItemLongClickListener;
    protected OnItemViewClickListener onItemViewClickListener;

    public interface onItemClickListener {
        void onItemClick(int position);
    }

    public interface onItemLongClickListener {
        boolean onItemLongClick(int position);
    }

    public interface OnItemViewClickListener {
        void onItemViewClick(View view, int position);
    }

    public void setOnItemLongClickListener(MyBaseRecyclerAdapter.onItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }

    public void setOnItemClickListener(MyBaseRecyclerAdapter.onItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public void setOnItemViewClickListener(OnItemViewClickListener onItemViewClickListener) {
        this.onItemViewClickListener = onItemViewClickListener;
    }

    /**
     * 设置没数据时的emptyView
     *
     * @param emptyView 没有数据时显示的控件
     * @param showWhat  没有数据时是否显示头尾布局
     */
    public void setEmptyView(View emptyView, EmptyShowWhat showWhat) {
        mEmptyView.setEmpty(emptyView);
        mEmptyView.setShowWhat(showWhat);
    }

    /**
     * 设置是否显示头尾布局，注意这个showWhat对emptyView显示时的showWhat互相独立，仅仅设置的是有数据时头尾布局的显示方式
     * 如果要设置emptyView的头尾布局显示方式，请调用setEmptyView方法
     *
     * @param mEmptyShowWhat
     */
    public void setShowWhat(EmptyShowWhat mEmptyShowWhat) {
        this.mEmptyShowWhat = mEmptyShowWhat;
        changeFooterAndHeaderShow(mEmptyShowWhat);
        notifyDataSetChanged();
    }

    private void changeFooterAndHeaderShow(EmptyShowWhat mEmptyShowWhat) {
        switch (mEmptyShowWhat) {
            case SHOW_ALL:
                setHeardViewsShowWhat(true);
                setFooterViewsShowWhat(true);
                break;
            case SHOW_HEADER:
                setHeardViewsShowWhat(true);
                setFooterViewsShowWhat(false);
                break;
            case SHOW_FOOTER:
                setHeardViewsShowWhat(false);
                setFooterViewsShowWhat(true);
                break;
            case SHOW_NONE:
                setHeardViewsShowWhat(false);
                setFooterViewsShowWhat(false);
                break;
        }
    }


    private void setHeardViewsShowWhat(boolean isShow) {
        if (mHeaderViews != null) {
            mHeaderViews.setShow(isShow);
        }
    }

    private void setFooterViewsShowWhat(boolean isShow) {
        if (mFooterViews != null) {
            mFooterViews.setShow(isShow);
        }
    }

    /**
     * 清除emptyView
     */
    public void clearEmptyView() {
        this.mEmptyView.setEmpty(null);
        this.mEmptyView.setShowWhat(null);
    }


    public MyBaseRecyclerAdapter(Context context, @LayoutRes int layoutId, List<T> data) {
        this.mContext = context;
        this.layoutId = layoutId;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }


    /**
     * @param context
     * @param layoutIds 一个hashMap key值是itemtype的值，value值是layoutID
     * @param data
     */
    public MyBaseRecyclerAdapter(Context context, HashMap<Integer, Integer> layoutIds, List<T> data) {
        this.mContext = context;
        this.layoutIds = layoutIds;
        this.data = data;
        inflater = LayoutInflater.from(context);
    }

    public InnerBaseViewHolder addHeaderView(View headerView) {
        return addHeaderView(headerView, -1);
    }

    /**
     * 在置顶位置插入一个headerView
     *
     * @param headerView
     * @param index      这个index不能超过插入前headerViews的长度
     */
    public InnerBaseViewHolder addHeaderView(View headerView, int index) {
        if (mHeaderViews == null) {
            mHeaderViews = new HeaderAndFooterList();
        }
        //同一个headerView不能重复添加
        for (int i = 0; i < mHeaderViews.size(); i++) {
            if (mHeaderViews.get(i).view.itemView == headerView) {
                return mHeaderViews.get(i).view;
            }
        }
        HeaderAndFooterBean map = new HeaderAndFooterBean();
        map.itemType = headerType;
        map.view = new InnerBaseViewHolder(headerView,null,true);
        if (index == -1) {
            mHeaderViews.add(map);
            notifyItemInserted(mHeaderViews.size() - 1);
        } else {
            mHeaderViews.add(index, map);
            notifyItemInserted(index);
        }
        headerType++;
        return map.view;
    }

    public void removeHeaderView(View headerView) {
        if (mHeaderViews == null) {
            return;
        }
        for (int i = mHeaderViews.size() - 1; i >= 0; i--) {
            if (mHeaderViews.get(i).view.itemView == headerView) {
                mHeaderViews.remove(i);
                notifyItemRemoved(i);
            }
        }
    }

    public void clearHeaderView() {
        if (mHeaderViews == null) {
            return;
        }
        notifyItemRangeRemoved(0, mHeaderViews.size() - 1);
        mHeaderViews.clear();
    }

    public int findHeaderPosition(View view) {
        for (int i = 0; i < mHeaderViews.size(); i++) {
            if (mHeaderViews.get(i).view.itemView == view) {
                return i;
            }
        }
        return -1;
    }

    public void clearFooterView() {
        if (mFooterViews == null) {
            return;
        }
        mFooterViews.clear();
    }

    public void removeFooterView(View footerView) {
        if (mFooterViews == null) {
            return;
        }
        for (int i = mFooterViews.size() - 1; i >= 0; i--) {
            if (mFooterViews.get(i).view.itemView == footerView) {
                mFooterViews.remove(i);
            }
        }
        int headerSize = 0;
        int dataSize = 0;
        int footerSize = 0;
        if (mHeaderViews != null) {
            headerSize = mHeaderViews.size();
        }
        if (mFooterViews != null) {
            footerSize = mFooterViews.size();
        }
        if (data != null) {
            dataSize = data.size();
        }
        footerType++;
        //size从1开始，position从0开始，所以需要减1
        notifyItemRemoved((headerSize + footerSize + dataSize + mFooterViews.size()) - 1);
    }

    public InnerBaseViewHolder addFooterView(View footerView) {
        if (mFooterViews == null) {
            mFooterViews = new HeaderAndFooterList();
        }
        //如果已经添加过了，则不在添加
        for (int i = 0; i < mFooterViews.size(); i++) {
            View view = mFooterViews.get(i).view.itemView;
            if (view == footerView) {
                return mFooterViews.get(i).view;
            }
        }
        HeaderAndFooterBean map = new HeaderAndFooterBean();
        map.itemType = footerType;
        map.view = new InnerBaseViewHolder(footerView,null,true);
        mFooterViews.add(map);
        int headerSize = 0;
        int dataSize = 0;
        int footerSize = 0;
        if (mHeaderViews != null) {
            headerSize = mHeaderViews.size();
        }
        if (mFooterViews != null) {
            footerSize = mFooterViews.size();
        }
        if (data != null) {
            dataSize = data.size();
        }
        footerType++;
        //size从1开始，position从0开始，所以需要减1
        notifyItemInserted((headerSize + footerSize + dataSize + mFooterViews.size()) - 1);
        return map.view;
    }


    public void setItemTypeLayout(HashMap<Integer, Integer> itemTypeLayout) {
        layoutIds = itemTypeLayout;
    }

    @Override
    public InnerBaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == EMPTY_TYPE) { //如果是空
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            mEmptyView.getEmpty().setLayoutParams(layoutParams);
            InnerBaseViewHolder viewHolder = new InnerBaseViewHolder(mEmptyView.getEmpty(), mContext);
            return viewHolder;
        }
        if (mHeaderViews != null && mHeaderViews.getShow()) {
            for (int i = 0; i < mHeaderViews.size(); i++) {
                if (mHeaderViews.get(i).itemType == viewType) {
                    InnerBaseViewHolder viewHolder = mHeaderViews.get(headerCount).view;
                    viewHolder.setmContext(mContext);
                    return viewHolder;
                }
            }
        }
        if (mFooterViews != null && mFooterViews.getShow()) {
            for (int i = 0; i < mFooterViews.size(); i++) {
                if (mFooterViews.get(i).itemType == viewType) {
                    InnerBaseViewHolder viewHolder = mFooterViews.get(footerCount).view;
                    viewHolder.setmContext(mContext);
                    return viewHolder;
                }
            }
        }

        if (layoutIds != null) {
            layoutId = layoutIds.get(viewType);
        }
        View view = inflater.inflate(layoutId, parent, false);
        InnerBaseViewHolder viewHolder = new InnerBaseViewHolder(view, mContext);
        onInitViewHolder(viewHolder,parent,viewType);
        return viewHolder;
    }

    protected void onInitViewHolder(MyBaseRecyclerAdapter.InnerBaseViewHolder holder,ViewGroup parent, int viewType){
        onInitViewHolder(holder,viewType);
    }
    /**
     * 初始化控件布局，有需要的话可以重写
     *
     * @param holder
     * @param viewType
     */
    protected void onInitViewHolder(MyBaseRecyclerAdapter.InnerBaseViewHolder holder, int viewType) {

    }

    /**
     * 与onCreateViewHolder对应的方法
     *
     * @param holder
     * @param position
     * @param payloads
     */
    @Override
    public void onBindViewHolder(MyBaseRecyclerAdapter.InnerBaseViewHolder holder, int position, List<Object> payloads) {
        if (isHeaderView(position) || isFooterView(position) || isEmpty) {
            return;
        }
        final int pos = getRealPosition(holder);
        if (payloads.isEmpty()) { //区分是否是增量更新 ，不是
            onBindViewHolder(holder, pos);
        } else {  //是
            convert(holder, data.get(pos), pos, getType(pos), payloads);
        }
    }

    @Override
    public void onBindViewHolder(MyBaseRecyclerAdapter.InnerBaseViewHolder holder, int position) {
        convert(holder, data.get(position), position, getType(position));
    }


    private int getRealPosition(RecyclerView.ViewHolder holder) {
        int position = holder.getLayoutPosition();
        return getRealPosition(position);
    }

    protected int getRealPosition(int position) {
        if (mHeaderViews != null && position < getItemSize()) {
            position = position - (mHeaderViews.size());
        }
        if (mFooterViews != null && position > getItemSize()) {
            position = position - getItemSize() - mFooterViews.size();
        }
        return position;
    }

    private boolean isHeaderView(int position) {
        return mHeaderViews != null && mHeaderViews.getShow() && position < mHeaderViews.size();
    }

    private boolean isFooterView(int position) {
        return mFooterViews != null && mFooterViews.getShow() && position > getItemSize() - 1;
    }

    /**
     * 根据不同的布局类型返回不同的布局。负责区分是否是头尾布局
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        //有头布局且当前显示的Position在头布局的范围内
        if (isHeaderView(position)) {
            headerCount = position;
            return mHeaderViews.get(headerCount).itemType;
        }
        //有尾布局且当前显示的position在尾布局的范围内
        if (isFooterView(position)) {
            footerCount = position - getItemSize();
            return mFooterViews.get(footerCount).itemType;
        }
        if (isEmpty) {//如果是空布局，则显示
            return EMPTY_TYPE;
        }
        int pos = getRealPosition(position);
        return getType(pos);
    }

    //获取item的真实数量，包括头布局的item
    private int getItemSize() {
        int headerSize = 0;
        int dataSize = 0;
        if (mHeaderViews != null) {
            headerSize = mHeaderViews.size();
        }
        if (data != null) {
            dataSize = data.size();
        }
        if (data.size() == 0 && mEmptyView.getEmpty() != null) {
            dataSize = 1;//空布局
        }
        return headerSize + dataSize;
    }

    /**
     * 获取item类型，Position已经预处理排出了headerView与footer的影响，
     *
     * @param position 当前要显示的数据的position
     * @return item重用受返回值影响<p/>注意不要返回大于20000的值，免得与头尾布局冲突复用出错报空指针
     */
    public abstract int getType(int position);

    /**
     * 处理数据
     *
     * @param holder   要显示数据的item
     * @param t        要显示的数据
     * @param itemType item的类型(如果需要进行类型处理)
     * @param position 当前item的position，已经进行预处理，去掉了头尾布局的影响
     */
    public abstract void convert(InnerBaseViewHolder holder, T t, int position, int itemType);

    /**
     * 适用于增量更新的convert，需要使用增量更新可以重写使用
     *
     * @param holder
     * @param t
     * @param position
     * @param itemType
     * @param payloads
     */
    public void convert(InnerBaseViewHolder holder, T t, int position, int itemType, List<Object> payloads) {
        convert(holder, t, position, itemType);
    }

    private boolean isEmpty;//数据是否为空

    /**
     * 计算item的数量，已经计算了头尾布局的数量
     *
     * @return
     */
    @Override
    public int getItemCount() {
        int size = data.size();
        if (data.size() == 0 && mEmptyView.getEmpty() != null) { //数据为空且设置了空布局
            //如果数据为0，则显示空布局emptyView
            size = 1;
            isEmpty = true;
            changeFooterAndHeaderShow(mEmptyView.showWhat);
        } else if (data.size() == 0) {//数据为空且没设置空布局,则按照原有模式进行，当然也可以设置是否显示头尾布局
            changeFooterAndHeaderShow(mEmptyShowWhat);
//            return 0;
        } else { //数据不为空,则按照自己本身的是否显示头尾布局来对头尾布局进行设置
            isEmpty = false;
            changeFooterAndHeaderShow(mEmptyShowWhat);
        }
        if (mHeaderViews != null) {
            size = size + mHeaderViews.size();
        }
        if (mFooterViews != null) {
            size = size + mFooterViews.size();
        }
        return size;
    }

    /**
     * 专门处理瀑布流的RecyclerView的头布局
     *
     * @param holder
     */
    @Override
    public void onViewAttachedToWindow(MyBaseRecyclerAdapter.InnerBaseViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        //GridLayoutManager的头尾布局判断
        RecyclerView recyclerView = (RecyclerView) holder.itemView.getParent();
        if (mHeaderViews != null || mFooterViews != null || isEmpty) {
            //GridLayoutManager头尾布局判断方法
            RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
            if (lm instanceof GridLayoutManager) {
                final GridLayoutManager glm = (GridLayoutManager) lm;
                glm.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                    @Override
                    public int getSpanSize(int position) {
                        return isHeaderView(position) || isFooterView(position) || isEmpty ? glm.getSpanCount() : 1;
                    }
                });
            }
            //瀑布流StaggeredGridLayoutManager头尾布局的判断方法
            ViewGroup.LayoutParams lp = holder.itemView.getLayoutParams();
            if (lp != null && lp instanceof StaggeredGridLayoutManager.LayoutParams) {
                int layoutPosition = holder.getLayoutPosition();
                StaggeredGridLayoutManager.LayoutParams slp = (StaggeredGridLayoutManager.LayoutParams) lp;
                slp.setFullSpan(isHeaderView(layoutPosition) || isFooterView(layoutPosition) || isEmpty);
            }
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public class InnerBaseViewHolder extends RecyclerView.ViewHolder {
        private SparseArray<View> views;
        private Context mContext;
        private Object tag;

        public Object getTag() {
            return tag;
        }

        public void setTag(Object tag) {
            this.tag = tag;
        }

        public InnerBaseViewHolder(View itemView) {
            super(itemView);
        }

        public void setmContext(Context mContext) {
            this.mContext = mContext;
        }

        public InnerBaseViewHolder(View itemView, Context context) {
            this(itemView,context,false);
        }
        public InnerBaseViewHolder(View itemView, Context context,boolean isHeaderOrFooter){
            super(itemView);
            mContext = context;
            views = new SparseArray<>();
            if (isEmpty||isHeaderOrFooter) { //空布局,头尾布局不设置任何监听
                return;
            }
            if (onItemClickListener != null) {
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onItemClickListener.onItemClick(getRealPosition(getLayoutPosition()));
                    }
                });
            }
            if (onItemLongClickListener != null) {
                itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        boolean result = onItemLongClickListener.onItemLongClick(getRealPosition(getLayoutPosition()));
                        return result;
                    }
                });
            }
        }
        public <Z extends View> Z getView(@IdRes int resId) {
            View view = views.get(resId);
            if (view == null) {
                view = itemView.findViewById(resId);
                views.put(resId, view);
            }
            return (Z) view;
        }
    }

    class HeaderAndFooterBean {
        int itemType;
        InnerBaseViewHolder view;
    }

    class HeaderAndFooterList extends ArrayList<HeaderAndFooterBean> {
        private boolean isShow = true;

        public void setShow(boolean isShow) {
            this.isShow = isShow;
        }

        public boolean getShow() {
            return isShow;
        }

        @Override
        public int size() {
            if (!isShow) {
                return 0;
            }
            return super.size();
        }
    }

    private class EmptyView {
        private View empty;
        private EmptyShowWhat showWhat;

        public View getEmpty() {
            return empty;
        }

        public void setEmpty(View empty) {
            this.empty = empty;
        }

        public EmptyShowWhat getShowWhat() {
            return showWhat;
        }

        public void setShowWhat(EmptyShowWhat showWhat) {
            this.showWhat = showWhat;
        }
    }

    public enum EmptyShowWhat {
        SHOW_ALL, SHOW_HEADER, SHOW_FOOTER, SHOW_NONE;
    }

    public int getmHeadSize() {
        return mHeaderViews == null ? 0 : mHeaderViews.size();
    }

    public int getmFootSize() {
        return mFooterViews == null ? 0 : mFooterViews.size();
    }

    /**
     * 预处理了头尾布局
     */
    public abstract class BaseDiffUtilCallback extends DiffUtil.Callback {
        private List<T> mOldList;
        private List<T> mNewList;

        public BaseDiffUtilCallback(List<T> oldList, List<T> newList) {
            this.mOldList = oldList;
            this.mNewList = newList;
        }

        public List<T> getOldList() {
            return mOldList;
        }

        public List<T> getNewList() {
            return mNewList;
        }

        @Override
        public int getOldListSize() {
            return mOldList != null ? getmHeadSize() + mOldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return mNewList != null ? getmHeadSize() + mNewList.size() : 0;
        }

        /**
         * 去掉头布局对position的影响
         *
         * @param oldItemPosition
         * @param newItemPosition
         * @return
         */
        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            if (isHeaderView(oldItemPosition) && isHeaderView(newItemPosition)) { //diffUtil不进行头尾布局的刷新操作
                return true;
            }
            if (isHeaderView(oldItemPosition) != isHeaderView(newItemPosition)) { //如果同样的item一个是头尾布局一个不是，则默认返回false
                return false;
            }
            if (isFooterView(oldItemPosition) != isFooterView(newItemPosition)) { //如果同样的item一个是头尾布局一个不是，则默认返回false
                return false;
            }
            if(oldItemPosition>getOldList().size()-1||newItemPosition>getNewList().size()-1){
                return false;
            }
            return areItemSame(getRealPosition(oldItemPosition), getRealPosition(newItemPosition));
        }

        public abstract boolean areItemSame(int oldItemPosition, int newItemPosition);

        /**
         * 去掉头布局对position的影响
         *
         * @param oldItemPosition
         * @param newItemPosition
         * @return
         */
        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            if (isHeaderView(oldItemPosition) && isHeaderView(newItemPosition)) { //diffUtil不进行头尾布局的刷新操作
                return true;
            }
            return areContentSame(getRealPosition(oldItemPosition), getRealPosition(newItemPosition));
        }

        public abstract boolean areContentSame(int oldItemPosition, int newItemPosition);

        /**
         * 取出了头布局对集合数据的影响
         *
         * @param oldItemPosition
         * @param newItemPosition
         * @return
         */
        @Nullable
        @Override
        public Object getChangePayload(int oldItemPosition, int newItemPosition) {
            return getChangeLoad(getRealPosition(oldItemPosition), getRealPosition(newItemPosition));
        }

        @Nullable
        public abstract Object getChangeLoad(int oldItemPosition, int newItemPosition);
    }

}