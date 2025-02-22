package io.github.sidvenu.mathjaxview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.annotation.Nullable;

public class MathJaxView extends WebView {

    String text, config, preDefinedConfig;

    public MathJaxView(Context context, AttributeSet attrs) {
        super(context, attrs);

        // if text is set in XML, call setText() with that text
        TypedArray a = getContext().obtainStyledAttributes(
                attrs,
                R.styleable.MathJaxView);
        String text = a.getString(R.styleable.MathJaxView_android_text);

        // default config for MathJax
        setConfig(
                "MathJax.Hub.Config({" +
                "    extensions: ['fast-preview.js']," +
                "    messageStyle: 'none'," +
                "    \"fast-preview\": {" +
                "      disabled: false" +
                "    }," +
                "    CommonHTML: {" +
                "      linebreaks: { automatic: true, width: \"container\" }" +
                "    }," +
                "    tex2jax: {" +
                "      inlineMath: [ ['$','$'] ]," +
                "      displayMath: [ ['$$','$$'] ]," +
                "      processEscapes: true" +
                "    }," +
                "    TeX: {" +
                "      extensions: [\"file:///android_asset/MathJax/extensions/TeX/mhchem.js\"]," +
                "      mhchem: {legacy: false}"+
                "    }" +
                "});"
        );
        preDefinedConfig = "TeX-MML-AM_CHTML";

        if (!TextUtils.isEmpty(text))
            setText(text);
        a.recycle();

        getSettings().setJavaScriptEnabled(true);
        getSettings().setCacheMode(WebSettings.LOAD_DEFAULT);
        setBackgroundColor(Color.TRANSPARENT);

        // render MathJax once page finishes loading
        setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                view.loadUrl("javascript:MathJax.Hub.Queue(['Typeset',MathJax.Hub]);");
            }
        });
    }

    // disable touch event on MathJaxView
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return false;
    }

    /**
     * I have included a default config that will be convenient for most of the people.
     * If you need to edit that config , please use this method to set your own MathJax custom config,
     * and note to call this before calling setText(String)
     *
     * @param config MathJax configuration to be used
     */
    public void setConfig(String config) {
        this.config = minifyConfig(config);
    }

    /**
     * This is to include any predefined MathJax configurations in the project, as referenced by
     * http://docs.mathjax.org/en/latest/config-files.html#common-configurations.
     * Only TeX-MML-AM_CHTML and MMLorHTML configurations comes with the assets of this project.
     * If you want other assets, create a MathJax folder in your application's assets, and
     * paste the config file in assets/MathJax/config/ folder.
     *
     * @param predefinedConfig MathJax predefined configuration to be used
     */
    public void setPredefinedConfig(String predefinedConfig) {
        this.preDefinedConfig = predefinedConfig;
    }

    /**
     * Minifies the MathJax config
     *
     * @param config The config to be minified
     */
    private String minifyConfig(String config) {
        return config.replace("\n", "").replace(" ", "");
    }

    /**
     * Renders MathJax code that is found in the passed-in string
     *
     * @param text Text that contains the MathJax to be rendered
     */
    public void setText(String text) {
        this.text = text;
        loadDataWithBaseURL("about:blank",
                "<html><head>" +
                        "<style>" +
                        "body {" +
                        "    text-align: left;" +
                        "}" +
                        "</style>" +
                        "<script type=\"text/x-mathjax-config\">" +
                        config +
                        "</script>" +
                        "<script type=\"text/javascript\" async src=\"file:///android_asset/MathJax/MathJax.js?config=" + preDefinedConfig + "\"></script>" +
                        "</head>" +
                        "<body>" +
                        text +
                        "</body>" +
                        "</html>", "text/html", "utf-8", "");
    }

    /**
     * Returns the MathJax code that was passed into using setText
     *
     * @return raw MathJax code
     */
    @Nullable
    public String getText() {
        return text;
    }
}
