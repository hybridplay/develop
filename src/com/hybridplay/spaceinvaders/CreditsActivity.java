package com.hybridplay.spaceinvaders;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class CreditsActivity extends Activity {
	   @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        WebView webview = new WebView(this);
	        setContentView(webview);
	        
	        String page = "<html>  <style type=\"text/css\"> " +
	        		"body {color: #0084FF;" +
	        		"background-color: BLACK;"+
                    "background-image: url(file:///android_res/drawable/tile.png);" +
	        		"font-family: sans-serif;" +
	        		"}</style>" +
	        		"<body>" +
	        		"<center>" +
	        		"<b>Space Invaders</b><br/>" +
	        		"By Glow Worm Applications<br/>" +
	        		"<img src=\"file:///android_res/drawable/ic_launcher.png\"/><br/>" +
	        		"This game is free software<br/>" +
	        		"and is available on Sourceforge<br/>" +
	        		"under the terms of the GPLv3<br/><br/>" +
	        		"Software by Joseph Warren<br/>" +
	        		"Sounds by Joe Newbold<br/>" +
	        		"Images courtesy of www.NasaImages.org" +
	        		"</center>"+
	        		"</body>" +
	        		"</html>";
	        webview.setScrollBarStyle(
	        		android.view.View.SCROLLBARS_INSIDE_OVERLAY);
	        webview.loadDataWithBaseURL("android.resource://com.glowwormapps.spaceinvaders",page, "text/html","UTF-8", null);
	   }
}
