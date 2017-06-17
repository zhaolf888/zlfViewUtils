/**
 * Copyright © 2016, Forp Co., LTD
 *
 * All Rights Reserved.
 */
package com.zlf.zlfviewutils;


import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 类功能描述:加载dialog
 *
 * @author	zhaolifeng
 * @date	2016年9月22日 下午4:42:17
 */
public class LoadingDialog {

	private static Dialog mDialog = null;
	/**
	 * 
	 * 描述:LoadingDialog 弹窗
	 * 
	 * @author zhaolifeng
	 * @version 2016年4月15日 下午6:07:37
	 * @param context
	 * @param msg
	 */
	@SuppressWarnings("deprecation")
	public static void showLoadingDialog(Activity context, String msg) {

		if (mDialog != null && mDialog.isShowing())
			try {
				mDialog.dismiss();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		LayoutInflater inflater = LayoutInflater.from(context);
		View v = inflater.inflate(R.layout.loading_dialog, null);
		LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_view);
		ImageView spaceshipImage = (ImageView) v
				.findViewById(R.id.loding_image_icon);
		TextView tipTextView = (TextView) v
				.findViewById(R.id.loding_textview_title);
		Animation hyperspaceJumpAnimation = AnimationUtils.loadAnimation(
				context, R.anim.loading_animation);
		spaceshipImage.startAnimation(hyperspaceJumpAnimation);
		tipTextView.setText(msg);
		mDialog = new Dialog(context, R.style.loading_custom_style);
		mDialog.setCancelable(false);
		mDialog.setContentView(layout, new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT,
				LinearLayout.LayoutParams.FILL_PARENT));
		mDialog.show();
		
		

	}
	
	// 关闭
		public static void DismissLoadingDialog() {
			if (mDialog != null)
				mDialog.dismiss();
		}
}
