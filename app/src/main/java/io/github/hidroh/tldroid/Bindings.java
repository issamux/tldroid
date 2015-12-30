package io.github.hidroh.tldroid;

import android.content.Context;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.databinding.BaseObservable;
import android.databinding.Bindable;
import android.databinding.BindingAdapter;
import android.support.annotation.AttrRes;
import android.support.annotation.IdRes;
import android.support.v4.content.ContextCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.webkit.WebView;
import android.widget.TextView;

public class Bindings {
    private static final String FORMAT_HTML_COLOR = "%06X";

    @BindingAdapter({"bind:monospace"})
    public static void setFont(TextView textView, boolean enabled) {
        if (enabled) {
            textView.setTypeface(Application.MONOSPACE_TYPEFACE);
        }
    }

    @BindingAdapter({"bind:highlightText", "bind:highlightColor"})
    public static void highlightText(TextView textView, String highlightText,
                                     @AttrRes int highlightColor) {
        if (TextUtils.isEmpty(highlightText)) {
            return;
        }
        Spannable spannable = new SpannableString(textView.getText());
        int start = TextUtils.indexOf(spannable, highlightText);
        if (start >= 0) {
            spannable.setSpan(new ForegroundColorSpan(ContextCompat.getColor(
                    textView.getContext(), getIdRes(textView.getContext(), highlightColor))),
                    start, start + highlightText.length(),
                    Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
        }
        textView.setText(spannable);
    }

    @BindingAdapter({
            "bind:html",
            "bind:htmlBackgroundColor",
            "bind:htmlTextColor",
            "bind:htmlLinkColor",
            "bind:htmlTextSize",
            "bind:htmlMargin"
    })
    public static void setHtml(WebView webView,
                               String html,
                               @AttrRes int backgroundColor,
                               @AttrRes int textColor,
                               @AttrRes int linkColor,
                               float textSize,
                               float margin) {
        if (TextUtils.isEmpty(html)) {
            return;
        }
        webView.setBackgroundColor(ContextCompat.getColor(webView.getContext(),
                getIdRes(webView.getContext(), backgroundColor)));
        webView.loadDataWithBaseURL(null,
                wrapHtml(webView.getContext(), html, textColor, linkColor, textSize, margin),
                "text/html", "UTF-8", null);
    }

    private static String wrapHtml(Context context, String html,
                                   @AttrRes int textColor,
                                   @AttrRes int linkColor,
                                   float textSize,
                                   float margin) {
        return context.getString(R.string.styled_html,
                html,
                toHtmlColor(context, textColor),
                toHtmlColor(context, linkColor),
                toHtmlPx(context, textSize),
                toHtmlPx(context, margin));
    }

    private static String toHtmlColor(Context context, @AttrRes int colorAttr) {
        return String.format(FORMAT_HTML_COLOR, 0xFFFFFF &
                ContextCompat.getColor(context, getIdRes(context, colorAttr)));
    }

    private static float toHtmlPx(Context context, float dimen) {
        return dimen / context.getResources().getDisplayMetrics().density;
    }

    @IdRes
    private static int getIdRes(Context context, @AttrRes int attrRes) {
        TypedArray ta = context.getTheme().obtainStyledAttributes(new int[]{attrRes});
        int resId = ta.getResourceId(0, 0);
        ta.recycle();
        return resId;
    }

    public static class Command extends BaseObservable {
        private String name;
        private String platform;

        public static Command fromProvider(Cursor cursor) {
            Command command = new Command();
            command.name = cursor.getString(cursor.getColumnIndexOrThrow(
                    TldrProvider.CommandEntry.COLUMN_NAME));
            command.platform = cursor.getString(cursor.getColumnIndexOrThrow(
                    TldrProvider.CommandEntry.COLUMN_PLATFORM));
            return command;
        }

        @Bindable
        public String getName() {
            return name;
        }

        @Bindable
        public void setName(String name) {
            this.name = name;
        }

        @Bindable
        public String getPlatform() {
            return platform;
        }

        @Bindable
        public void setPlatform(String platform) {
            this.platform = platform;
        }
    }
}
