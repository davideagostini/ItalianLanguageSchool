package it.italianlanguageschool;

import it.italianlanguageschool.utility.ConnectionDetector;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebSettings.ZoomDensity;
import android.widget.TextView;

public class GenericWebView extends Activity {

	private WebView myWebView;
	ConnectionDetector cd;
	private String url = null;
	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private MyBroadcastReceiver connectionReceiver;

	@SuppressLint("SetJavaScriptEnabled")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.web_view);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(true);
		
		connectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(connectionReceiver, filter);

		cd = new ConnectionDetector(GenericWebView.this);
		if(!cd.isConnectingToInternet()) {
			//alert = new AlertDialogManager();
			//alert.showAlertDialog(WebViewUni.this, "Errore di rete", "Nessuna connessione di rete disponibile.", false);
		}
		else {

			Intent i = getIntent();
			url = i.getStringExtra("url");

			myWebView = (WebView) findViewById(R.id.webView);
			myWebView.getSettings().setJavaScriptEnabled(true);
			myWebView.getSettings().setSupportZoom(true);
			myWebView.getSettings().setBuiltInZoomControls(true);
			myWebView.getSettings().setDefaultZoom(ZoomDensity.CLOSE);
			myWebView.getSettings().setLoadWithOverviewMode(true);
			myWebView.getSettings().setUseWideViewPort(true);
			myWebView.getSettings().setPluginState(PluginState.ON);
			myWebView.setWebViewClient(new WebViewClient() {

				@Override
				public void onPageFinished(WebView view, String url) {
					super.onPageFinished(view, url);
					setProgressBarIndeterminateVisibility(false);
				}
			});
			myWebView.loadUrl(url);
		}
	}
	
	@Override
	protected void onResume() {
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(connectionReceiver, filter);
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		unregisterReceiver(connectionReceiver);
		super.onStop();
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(false);
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void showStatusConnection(boolean a) {
		((TextView)findViewById(R.id.statusConnection)).setVisibility(!a ? View.GONE : View.VISIBLE);
	}
	
	private class MyBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			boolean a = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY,false);
			showStatusConnection(a);
		}
	}; 
}
