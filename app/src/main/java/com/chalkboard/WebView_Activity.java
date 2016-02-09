package com.chalkboard;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;

public class WebView_Activity extends Activity{
	
	WebView webview;
	String getUrl = "";
	
@Override
protected void onCreate(Bundle savedInstanceState) {
	// TODO Auto-generated method stub
	super.onCreate(savedInstanceState);
	requestWindowFeature(Window.FEATURE_NO_TITLE);
	setContentView(R.layout.webview);
	
	getUrl = getIntent().getStringExtra("URL");
	webview = (WebView) findViewById(R.id.web);
	
	if(getUrl != null){
		
		loadUrl(getUrl);
	}

	getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

	
}

private void loadUrl(String getString) {
	// TODO Auto-generated method stub
	webview.loadUrl(getString);
}
}
