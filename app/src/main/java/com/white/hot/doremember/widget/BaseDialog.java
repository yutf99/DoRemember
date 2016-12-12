package com.white.hot.doremember.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.white.hot.doremember.R;
import com.white.hot.doremember.utils.UIHelper;

/**
 * Created by ytf on 2016/11/28.
 * descption:  对话框
 */

public class BaseDialog extends Dialog
{
    private ViewGroup parent;
    private FrameLayout container;
    private TextView tvNegative;
    private TextView tvPositive;
    private TextView tip;
    //对话框比例，最大为10，最小为1；
    private int dialogSizeRation = 5;

    public TextView getTvPositive()
    {
        return tvPositive;
    }

    public TextView getTvNegative()
    {
        return tvNegative;
    }

    public TextView getTip()
    {
        return tip;
    }

    public BaseDialog(Context context)
    {
        this(context, R.style.AlertDialogStyle);
    }

    public BaseDialog(Context context, int themeResId)
    {
        super(context, themeResId);
        parent = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.base_dialog, null, false);
        init();
    }
    public BaseDialog(Context context, int themeResId, int ration)
    {
        this(context, themeResId);
        this.dialogSizeRation = ration;
    }

    private void init()
    {
        container = (FrameLayout) parent.findViewById(R.id.container);
        tvNegative = (TextView) parent.findViewById(R.id.btn_negative);
        tvPositive = (TextView) parent.findViewById(R.id.btn_positive);
        tip = (TextView) parent.findViewById(R.id.message);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        super.setContentView(parent);

        buildSize(dialogSizeRation);
    }

    /***
     * 通过传入参数控制对话框的屏幕占比
     * @param ration
     */
    public void buildSize(int ration)
    {
        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        DisplayMetrics m = getContext().getResources().getDisplayMetrics();
        int width = m.widthPixels;
        p.width = (int) (width / (10.0 / ration));
        p.height = WindowManager.LayoutParams.WRAP_CONTENT;
        //此处因为是重新设置了params，所以在xml中设置的动画已经无效了，要再设置一次
        window.setWindowAnimations(R.style.BaseDialogAnim);
        window.setAttributes(p);
    }

    public static class Builder
    {
        private BaseParams p;
        private BaseDialog dialog;
        private boolean isCreated;
        private int ration = -1;

        public Builder(Context context)
        {
            p = new BaseParams(context);
        }

        public Builder setNagetiveListen(String negativeText, View.OnClickListener listener)
        {
            p.negativeText = negativeText;
            p.negativeListener = listener;
            return this;
        }

        public Builder setMessage(String message)
        {
            if(TextUtils.isEmpty(message))
            {
                p.message = "";
            }else
            {
                p.message = message;
            }
            return this;
        }

        public Builder setPositiveListen(String positiveText, View.OnClickListener listener)
        {
            p.positiveText = positiveText;
            p.positiveListener = listener;
            return this;
        }

        public Builder setView(int resId)
        {
            p.customContentViewResId = resId;
            return this;
        }

        public Builder showOkOnly()
        {
            p.showOkOnly = true;
            return this;
        }

        public Builder setView(View v)
        {
            p.customContentView = v;
            return this;
        }

        public BaseDialog create()
        {
            if(ration != -1 && ration >= 1 && ration <= 10)
            {
                dialog = new BaseDialog(p.context, R.style.AlertDialogStyle, ration);
            }else
            {
                dialog = new BaseDialog(p.context);
            }
            p.apply(dialog);
            isCreated = true;
            return dialog;
        }

        public Builder setRation(int r)
        {
            this.ration = r;
            return this;
        }

        public BaseDialog show()
        {
            if(!isCreated)
            {
                isCreated = true;
                create().show();
            }else
            {
                if(dialog.isShowing())
                {
                    dialog.dismiss();
                }
                dialog.show();
            }
            return dialog;
        }
    }

    protected static class BaseParams
    {
        public View.OnClickListener negativeListener, positiveListener;
        public OnDismissListener dismissListener;
        public boolean cancelable = true;
        public String negativeText, positiveText;
        public View customContentView;
        public int customContentViewResId = -1;
        //默认会带一个textview显示message
        public String message;
        public Context context;
        public boolean showOkOnly;

        public BaseParams(Context context)
        {
            this.context = context;
        }

        public void apply(final BaseDialog dialog)
        {
            if(!TextUtils.isEmpty(negativeText))
            {
                dialog.getTvNegative().setText(negativeText);
            }

                dialog.getTvNegative().setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        dialog.dismiss();
                        if(negativeListener != null)
                        {
                            negativeListener.onClick(v);
                        }
                    }
                });


            if(!TextUtils.isEmpty(positiveText))
            {
                dialog.getTvPositive().setText(positiveText);
            }

            dialog.getTvPositive().setOnClickListener(new View.OnClickListener()
            {

                @Override
                public void onClick(View v)
                {
                    dialog.dismiss();
                    if(positiveListener != null)
                    {
                        positiveListener.onClick(v);
                    }
                }
            });

            if(showOkOnly)
            {
                dialog.getTvNegative().setVisibility(View.GONE);
            }

            if (cancelable) {
                dialog.setCanceledOnTouchOutside(true);
            }else
            {
                dialog.setCanceledOnTouchOutside(false);
            }
            if(message != null)
            {
                dialog.getTip().setText(message);
            }
            if(customContentView != null)
            {
                dialog.setContentView(customContentView);
            }else if(customContentViewResId != -1)
            {
                dialog.setContentView(customContentViewResId);
            }
        }
    }

    @Override
    public void setContentView(int layoutResID)
    {
        container.removeAllViews();
        container.addView(LayoutInflater.from(getContext()).inflate(layoutResID, container, false));
    }

    @Override
    public void setContentView(View v)
    {
        container.removeAllViews();

        UIHelper.removeParent(v);
        container.addView(v);
    }

}
