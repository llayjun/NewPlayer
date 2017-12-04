package com.millet.androidlib.Utils;

import android.content.Context;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.style.ImageSpan;
import android.widget.TextView;

/**
 * Created by Administrator on 2017/4/27 0027.
 */

public class TextUtils {

    /**
     * TextView追加图片方法
     *
     * @param _context  上下文
     * @param textView  文本控件
     * @param content   内容
     * @param _drawable 图片
     * @param maxLines  最大行数
     */
    public static void addImage(Context _context, TextView textView, String content, Drawable _drawable, int maxLines) {
        textView.setMaxLines(maxLines);// 设置最大行数
        TextPaint paint = textView.getPaint();// 获取文本控件字体样式
        Paint.FontMetrics fm = paint.getFontMetrics();
        int textFontHeight = (int) Math.ceil(fm.descent - fm.top) - 28;// 计算字体高度座位图片高度
        int imageWidth = _drawable.getIntrinsicWidth() * textFontHeight
                / _drawable.getIntrinsicHeight();// 计算图片根据字体大小等比例缩放后的宽度
        _drawable.setBounds(0, dp2px(_context, 1), imageWidth,
                textFontHeight);// 缩放图片
        int maxWidth = textView.getLayoutParams().width;// 获取文本控件最大宽度
        if (paint.measureText(content) > maxWidth) {// 如果文本大于一行才进入复杂的计算逻辑
            String s = "";// 临时存储截取的字符串
            int start = 0;// 截取的起始位置
            int end = 1;// 截取的结束位置
            int lines = 1;// 计算的行数
            boolean flag = false;// 已经计算到了最大行但是该行文本加图片的宽度超出文本框的宽度时，设置该标记将进行文本递减拼接测量
            do {
                s = content.substring(start, end);// 截取制定位置的字符串
                float width = paint.measureText(s);// 测量截取的字符串宽度
                if (width < maxWidth) {// 截取的文字长度小于控件宽度
                    if (lines == maxWidth) {// 如果已经是最大行
                        if (width + imageWidth < maxWidth) {
                            // 文本宽度+图片宽度小于控件宽度
                            if (width + imageWidth + paint.measureText("...") < maxWidth) {
                                // 文本宽度+图片宽度+省略号宽度大于控件宽度
                                if (flag) {
                                    // 递减测量的第一次进入并且满足上一个if则停止循环
                                    content = content.substring(0, end);// 文案切割
                                    content += "...";// 文案拼接省略号
                                    break;
                                } else {// 还在进行递增测量，结束位置+1
                                    end++;
                                }
                            } else {
                                // 文本宽度+图片宽度+省略号宽度大于控件宽度，因为已经是最大行（lines ==
                                // maxWidth）需要对文字做递减测量，结束位置-1
                                end--;
                            }
                        } else {
                            // 文本宽度+图片宽度大于控件宽度，因为已经是最大行（lines == maxWidth）
                            // 需要对文字做递减测量，结束位置-1，flag置为true
                            end--;
                            flag = true;
                        }
                    } else {
                        // 截取文字的上限位置+1
                        end++;
                    }
                } else {
                    // 截取的文字长度大于控件宽度，一行的位置已经确定，下一行的起始位置为当前结束位置-1，行数+1
                    start = end - 1;
                    lines++;
                }
            } while (end <= content.length() && lines <= maxLines);
        }
        // 文本后面拼接图片
        ImageSpan span = new ImageSpan(_drawable, ImageSpan.ALIGN_BASELINE);
        content += "*";
        SpannableString spanString = new SpannableString(content);
        spanString.setSpan(span, content.length() - 1, content.length(),
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(spanString);
    }

    /**
     * dp转px
     *
     * @param _context
     * @param dp
     * @return
     */
    public static int dp2px(Context _context, int dp) {
        final float scale = _context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    /**
     * sp转px的方法。
     */
    public static int sp2px(Context _context, float spValue) {
        final float fontScale = _context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

}
