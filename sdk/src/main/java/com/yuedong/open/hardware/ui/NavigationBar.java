package com.yuedong.open.hardware.ui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuedong.open.hardware.sdk.R;


/**
 * Created by virl on 15/5/18.
 */
public class NavigationBar extends FrameLayout implements View.OnClickListener {
    public NavigationBar(Context context) {
        super(context);
        initView(context);
    }
    public NavigationBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public NavigationBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initView(context);
    }

    private FrameLayout leftBn;
    private FrameLayout rightBn;
    private View leftBnContent;
    private View rightBnContent;
    private TextView labelTitle;
    private NavBnClickedListener l;
    private LinearLayout rightBnContainer;

    public void setTitle(CharSequence title) {
        labelTitle.setText(title);
    }

    public void setTitle(int titleRes) {
        labelTitle.setText(titleRes);
    }

    private void initView(Context context) {
        labelTitle = new TextView(context);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        int margin = getResources().getDimensionPixelSize(R.dimen.oh_sdk_nav_title_margin_left_right);
        params.leftMargin = margin;
        params.rightMargin = margin;
        labelTitle.setGravity(Gravity.CENTER);
        labelTitle.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                getResources().getDimensionPixelSize(R.dimen.oh_sdk_nav_title_text_size));
        labelTitle.setTextColor(Color.WHITE);
        labelTitle.setSingleLine(true);
        labelTitle.setEllipsize(TextUtils.TruncateAt.END);
        addView(labelTitle, params);
        setBackgroundColor(getResources().getColor(R.color.oh_sdk_main_green));
        rightBnContainer = new LinearLayout(context);
        rightBnContainer.setOrientation(LinearLayout.HORIZONTAL);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.RIGHT;
        addView(rightBnContainer, params);
    }

    public TextView getLabelTitle() {
        return labelTitle;
    }

    public View getLeftBnContent() {
        return leftBnContent;
    }

    public View getRightBnContent() {
        return rightBnContent;
    }

    public View getLeftBn() {
        return leftBn;
    }

    public View getRightBn() {
        return rightBn;
    }

    private void buildLeftBn() {
        leftBn = createBn();
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.LEFT | Gravity.CENTER_VERTICAL;
        leftBn.setId(R.id.oh_sdk_bn_nav_left);
        addView(leftBn, params);
    }

    private void buildRightBn() {
        rightBn = createBn();
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        rightBn.setId(R.id.oh_sdk_bn_nav_right);
        rightBnContainer.addView(rightBn, params);
    }

    private FrameLayout createBn() {
        PressedFrameLayout bn = new PressedFrameLayout(getContext());
        bn.setMinimumWidth(getResources().getDimensionPixelSize(R.dimen.oh_sdk_nav_bn_min_width));
        int padding = getResources().getDimensionPixelSize(R.dimen.ok_sdk_nav_bn_padding_left_right);
        bn.setPadding(padding, 0, padding, 0);
        bn.setBackgroundResource(R.drawable.oh_sdk_selector_bg_nav_bn);
        return bn;
    }

    public void setLeftBnContent(View leftBnContent) {
        if(null == leftBn) {
            buildLeftBn();
        }
        if(null!=this.leftBnContent) {
            leftBn.removeView(this.leftBnContent);
        }
        addBnContentToBn(leftBnContent, leftBn);
        leftBn.setOnClickListener(this);
        this.leftBnContent = leftBnContent;
    }

    public void setRightBnContent(View rightBnContent) {
        if(null == rightBn) {
            buildRightBn();
        }
        if(null!=this.rightBnContent) {
            rightBn.removeView(this.rightBnContent);
        }
        addBnContentToBn(rightBnContent, rightBn);
        rightBn.setOnClickListener(this);
        this.rightBnContent = rightBnContent;
    }

    private void addBnContentToBn(View bnContent, FrameLayout bn) {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        bn.addView(bnContent, params);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.oh_sdk_bn_nav_left) {
            if(l!=null)
                l.onNavLeftBnClicked();
        } else if(id == R.id.oh_sdk_bn_nav_right) {
            if(l!=null)
                l.onNavRightBnClicked();
        } else {
            navExtraBnClickedListener.onNavExtraBnClicked((Integer)view.getTag());
        }
    }

    public interface NavBnClickedListener {
        void onNavLeftBnClicked();
        void onNavRightBnClicked();
    }

    public void addRightBn(View bnContent, int tag) {
        FrameLayout bn = createBn();
        addBnContentToBn(bnContent, bn);
        bn.setTag(Integer.valueOf(tag));
        bn.setOnClickListener(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        if(null==rightBn) {
            rightBnContainer.addView(bn, params);
        } else {
            rightBnContainer.addView(bn, rightBnContainer.getChildCount()-1, params);
        }
    }

    public interface NavExtraBnClickedListener {
        void onNavExtraBnClicked(int tag);
    }

    private NavExtraBnClickedListener navExtraBnClickedListener;

    public void setNavExtraBnClickedListener(NavExtraBnClickedListener navExtraBnClickedListener) {
        this.navExtraBnClickedListener = navExtraBnClickedListener;
    }

    public void setNavBnClickedListener(NavBnClickedListener l) {
        this.l = l;
    }

    public static View iconBn(Context context, int resId) {
        ImageView view = new ImageView(context);
        view.setImageResource(resId);
        return view;
    }

    public static View textBn(Context context, int textRes) {
        TextView textView = new TextView(context);
        textView.setText(textRes);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                context.getResources().getDimensionPixelSize(R.dimen.oh_sdk_nav_text_bn_text_size));
        textView.setTextColor(context.getResources().getColorStateList(R.color.oh_sdk_selector_navigation_text));
        return textView;
    }

    public static View backBn(Context context) {
        return iconBn(context, R.mipmap.oh_sdk_icon_nav_back);
    }

//    public static View moreBn(Context context) {
//        return iconBn(context, R.drawable.run_more);
//    }
//
//    public static View shareBn(Context context) {
//        return iconBn(context, R.drawable.selector_nav_icon_share);
//    }
//
//    public static View trashBn(Context context) {
//        return iconBn(context, R.drawable.selector_nav_icon_trash);
//    }
}
