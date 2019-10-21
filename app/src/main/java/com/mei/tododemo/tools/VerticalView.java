package com.mei.tododemo.tools;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import com.mei.tododemo.R;

/**
 * created by meishenbo
 * 2018/12/12
 */
public class VerticalView  extends FrameLayout{
    private final static String TAG="VerticalView";
    private ViewGroup.LayoutParams layoutParams ;
    public VerticalView(Context context) {
        this(context,null);
    }

    public VerticalView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
    }

    public VerticalView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        View view = LayoutInflater.from(context).inflate(R.layout.vertical_layuot, this,false);
        addView(view);
        View topView = view.findViewById(R.id.top_liner);
        View bottomView = view.findViewById(R.id.bottom_liner);
        View content_view = view.findViewById(R.id.content_liner);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.vertical_view);
        float topWH = typedArray.getDimension(R.styleable.vertical_view_top_wh, 0);
        if (topWH>0){
            layoutParams = topView.getLayoutParams();
            layoutParams.height = (int) topWH;
            layoutParams.width = (int) topWH;
            topView.setLayoutParams(layoutParams);

        }
        float bottomWH = typedArray.getDimension(R.styleable.vertical_view_bottom_wh, 0);
        Log.d(TAG, "init: "+bottomWH);
        if (bottomWH>0) {
           layoutParams =  bottomView.getLayoutParams();
           layoutParams.height = (int) bottomWH;
           layoutParams.width = (int) bottomWH;
           bottomView.setLayoutParams(layoutParams);

        }
        float contentW =  typedArray.getDimension(R.styleable.vertical_view_content_width, 0);
        if (contentW>0) {
            layoutParams = content_view.getLayoutParams();
            layoutParams.width = (int)contentW;
            content_view.setLayoutParams(layoutParams);
        }
        float contentH = typedArray.getDimension(R.styleable.vertical_view_content_height, 0);
        if (contentH>0) {
            layoutParams =  content_view.getLayoutParams();
            layoutParams.height =(int) contentW;
            content_view.setLayoutParams(layoutParams);
        }
        int topColor = typedArray.getColor(R.styleable.vertical_view_top_color, Color.BLUE);
        topView.setBackgroundColor(topColor);
        int bottomColor = typedArray.getColor(R.styleable.vertical_view_bottom_color, Color.BLUE);
        bottomView.setBackgroundColor(bottomColor);
        int contentColor = typedArray.getColor(R.styleable.vertical_view_content_color, Color.BLUE);
        content_view.setBackgroundColor(contentColor);

        int topVisibility = typedArray.getInt(R.styleable.vertical_view_topVisibility, -1);

        if (topVisibility!=-1) {
            topView.setVisibility(VISIBLE);
        }

        int bottomVisibility = typedArray.getInt(R.styleable.vertical_view_bottomVisibility, -1);

        if (bottomVisibility!=-1) {
            bottomView.setVisibility(VISIBLE);
        }


    }








}
