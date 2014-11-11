package it.italianlanguageschool;

import it.italianlanguageschool.utility.ConnectionDetector;
import it.italianlanguageschool.utility.ImageLoader;
import it.italianlanguageschool.utility.JSONParser;
import it.italianlanguageschool.utility.LocaleLanguage;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LMDetail extends Activity {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	JSONArray jLM_detail = null;
	private String lm_id = null;
	private String school_id, school_img, school_name, school_address, lm_detail;
	private String err = null;
	private TextView txt_school_name, txt_school_address, txt_detail;
	private ImageView imgView;
	private ImageLoader imageLoader;
	private ProgressBar mProgressBar;
	LocaleLanguage locLanguage = new LocaleLanguage();
	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private MyBroadcastReceiver connectionReceiver;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.lm_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);
		
		connectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(connectionReceiver, filter);

		Intent i = getIntent();
		lm_id = i.getStringExtra("lm_id");
		school_id = i.getStringExtra("school_id");

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		txt_school_name = (TextView) findViewById(R.id.sd_name);
		txt_school_address = (TextView) findViewById(R.id.sd_address);
		txt_detail = (TextView) findViewById(R.id.lm_detail);
		imgView = (ImageView) findViewById(R.id.sd_image);
		imageLoader = new ImageLoader(getApplicationContext());

		cd = new ConnectionDetector(getApplicationContext());
		if (!cd.isConnectingToInternet()) {
		}
		else {
			if(savedInstanceState != null) {
				school_id = savedInstanceState.getString("school_id");
				school_name = savedInstanceState.getString("school_name");
				school_address = savedInstanceState.getString("school_address");
				lm_detail = savedInstanceState.getString("lm_detail");
				school_img = savedInstanceState.getString("school_img");
				imageLoader.DisplayImage(school_img, imgView);
				txt_school_name.setText(school_name);
				txt_school_address.setText(school_address);
				txt_detail.setText(lm_detail);
				showDetail(true);
			}
			else {
				mProgressBar.setVisibility(View.VISIBLE);
				new LoadLMDetail().execute();
			}
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
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString("school_id", school_id);
		outState.putString("school_img", school_img);
		outState.putString("school_address", school_address);
		outState.putString("school_name", school_name);
		outState.putString("lm_detail", lm_detail);
	}


	class LoadLMDetail extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("k", lm_id));
			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));

			String json = jsonParser
					.makeHttpRequest(Constant.URL_LIST_LASTMINUTE, "GET", params);

			try {
				JSONObject jObj = new JSONObject(json);
				if (jObj != null) {
					school_id = jObj.getString(Constant.ITEM_LD_ID_SCHOOL);
					school_img = jObj.getString(Constant.ITEM_LM_IMG);
					school_address = jObj.getString(Constant.ITEM_LM_INDIRIZZO);
					school_name = jObj.getString(Constant.ITEM_LM_TITLE);
					lm_detail = jObj.getString(Constant.ITEM_LM_DESCRIPTION);
				}
			} catch (JSONException e) {
				err = "Impossibile scaricare le notizie. Riprova più tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			setProgressBarIndeterminateVisibility(false);

			if(err!=null) {
				//alert.showAlertDialog(ArticleDetail.this, "Errore di connessione", err, false);
			}
			else {
				mProgressBar.setVisibility(View.INVISIBLE);
				showDetail(true);
				imageLoader.DisplayImage(school_img, imgView);
				txt_school_name.setText(school_name);
				txt_school_address.setText(school_address);
				txt_detail.setText(lm_detail);
			}
		}
	}

	public void showSchool() {
		Button btn_show = (Button) findViewById(R.id.showSchool);
		btn_show.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
				Intent i = new Intent(LMDetail.this, SchoolDetail.class);
				i.putExtra("school_id", school_id);
				startActivity(i);
			}
		});
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.animator.push_right_in, R.animator.push_right_out);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			overridePendingTransition(R.animator.push_right_in, R.animator.push_right_out);
			return true;
		case R.id.action_refresh:
			if(cd.isConnectingToInternet())
				new LoadLMDetail().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showDetail(boolean set) {
		findViewById(R.id.det_id).setVisibility(set ? View.VISIBLE : View.GONE);
		showSchool();
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

