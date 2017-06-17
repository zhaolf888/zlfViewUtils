package com.zlf.zlfviewutils;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * 没有加载到数据的空view
 *
 *         EmptyBuilder builder=new EmptyBuilder(this);
 builder.setAddButton("去登录", new View.OnClickListener() {
@Override
public void onClick(View v) {
Intent intent = new Intent(this, LoginActivity.class);
startActivityForResult(intent, 80);
}
});
 builder.setContent("您还未添加收货地址",null);
 builder.setEmptyImg(R.drawable.address_no_data,null);
 View emptyView = builder.create();
 *
 */
public class EmptyBuilder {

    private View emptyView;
    private TextView tvTitle;
    private TextView tvAddOne;
    private ImageView ivEmpty;
    private Context context;
 private RelativeLayout rl_data_is_null;
    public EmptyBuilder(Context context) {
        this.context = context;
        emptyView = LayoutInflater.from(context).inflate(R.layout.default_empty, null);
        rl_data_is_null = (RelativeLayout) emptyView.findViewById(R.id.data_is_null);
        tvTitle = (TextView) emptyView.findViewById(R.id.tv_empty_title);
        tvAddOne = (TextView) emptyView.findViewById(R.id.tv_add_one);
        ivEmpty = (ImageView) emptyView.findViewById(R.id.iv_empty);
    }

    /**
     * 设置标题
     *
     * @param title
     */
    public EmptyBuilder setContent(String title, View.OnClickListener onClickListener) {
        tvTitle.setText(title);
        tvTitle.setVisibility(View.VISIBLE);
        if (onClickListener != null) {
            tvTitle.setOnClickListener(onClickListener);
        }
        return this;
    }

    /**
     *  设置空白页的背景颜色
     * @param rs_cl
     * @return
     */
    public EmptyBuilder  setBackgroundColor(int rs_cl){
        rl_data_is_null.setBackgroundResource(rs_cl);
        return this;
    }

    /**
     * 点击按钮
     *
     * @param addOne
     * @param onClickListener
     */
    public EmptyBuilder setAddButton(String addOne, View.OnClickListener onClickListener) {
        tvAddOne.setText(addOne);
        tvAddOne.setVisibility(View.VISIBLE);
        if (onClickListener != null) {
            tvAddOne.setOnClickListener(onClickListener);
        }
        return this;
    }

    public EmptyBuilder setEmptyImg(@DrawableRes int resId, View.OnClickListener onClickListener) {
        ivEmpty.setImageResource(resId);
        if (onClickListener != null) {
            ivEmpty.setOnClickListener(onClickListener);
        }
        return this;
    }

    public View create() {
        return emptyView;
    }
}
