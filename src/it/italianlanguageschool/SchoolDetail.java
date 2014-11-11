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

public class SchoolDetail extends Activity {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	JSONArray school_detail = null;
	String school_id = null;
	String school_img, school_name, school_address, school_telephone, school_lessons, school_max_students, school_material, school_test, school_registration, school_accommodation, school_wifi, school_corpo, school_email, school_verify;
	private String err = null;
	private TextView txt_school_name, txt_school_address, txt_school_telephone, txt_lessons, txt_max_students, txt_material, txt_test, txt_registration, txt_accommodation, txt_wifi, txt_corpo, txt_email, txt_verify;
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
		setContentView(R.layout.school_detail);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);
		cd = new ConnectionDetector(getApplicationContext());
		
		connectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(connectionReceiver, filter);

		Intent i = getIntent();
		school_id = i.getStringExtra("school_id");

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		txt_school_name = (TextView) findViewById(R.id.sd_name);
		txt_school_address = (TextView) findViewById(R.id.sd_address);
		txt_school_telephone = (TextView) findViewById(R.id.sd_telephone);
		txt_lessons = (TextView) findViewById(R.id.sd_lessons);
		txt_max_students = (TextView) findViewById(R.id.sd_max_students);
		txt_material = (TextView) findViewById(R.id.sd_material);
		txt_test = (TextView) findViewById(R.id.sd_level_test);
		txt_registration = (TextView) findViewById(R.id.sd_registration);
		txt_accommodation = (TextView) findViewById(R.id.sd_accommodation);
		txt_wifi = (TextView) findViewById(R.id.sd_wifi);
		txt_corpo = (TextView) findViewById(R.id.sd_corpo);
		
		txt_email = (TextView) findViewById(R.id.sd_email);
		txt_verify = (TextView) findViewById(R.id.sd_verify);

		
		imgView = (ImageView) findViewById(R.id.sd_image);
		imageLoader = new ImageLoader(getApplicationContext());

		if(savedInstanceState != null) {
			school_name = savedInstanceState.getString("school_name");
			school_address = savedInstanceState.getString("school_address");
			school_telephone = savedInstanceState.getString("school_telephone");
			school_lessons = savedInstanceState.getString("school_lessons");
			school_max_students = savedInstanceState.getString("school_max_students");
			school_material = savedInstanceState.getString("school_material");
			school_test = savedInstanceState.getString("school_test");
			school_registration = savedInstanceState.getString("school_registration");
			school_accommodation = savedInstanceState.getString("school_accommodation");
			school_wifi = savedInstanceState.getString("school_wifi");
			school_corpo = savedInstanceState.getString("school_corpo");
			school_img = savedInstanceState.getString("school_img");				
			imageLoader.DisplayImage(school_img, imgView);
			txt_school_name.setText(school_name);
			txt_school_address.setText(school_address);
			txt_school_telephone.setText(school_telephone);
			txt_lessons.setText(school_lessons);
			txt_max_students.setText(school_max_students);
			txt_material.setText(school_material);
			txt_test.setText(school_test);
			txt_registration.setText(school_registration);
			txt_accommodation.setText(school_accommodation);
			txt_wifi.setText(school_wifi);
			txt_corpo.setText(school_corpo);
			txt_email.setText(school_email);
			txt_verify.setText(school_verify);
			showDetail(true);
		}
		else {
			mProgressBar.setVisibility(View.VISIBLE);
			new LoadSchoolDetail().execute();
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
		outState.putString("school_img", school_img);
		outState.putString("school_name", school_name);
		outState.putString("school_address", school_address);
		outState.putString("school_telephone", school_telephone);
		outState.putString("school_lessons", school_lessons);
		outState.putString("school_max_students", school_max_students);
		outState.putString("school_material", school_material);
		outState.putString("school_test", school_test);
		outState.putString("school_registration", school_registration);
		outState.putString("school_accommodation", school_accommodation);
		outState.putString("school_wifi", school_wifi);
		outState.putString("school_corpo", school_corpo);
		outState.putString("school_email", school_email);
		outState.putString("school_verify", school_verify);

	}

	class LoadSchoolDetail extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("k", school_id));
			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));

			String json = jsonParser
					.makeHttpRequest(Constant.URL_SCHOOL_DETAIL, "GET", params);

			try {
				JSONObject jObj = new JSONObject(json);
				if (jObj != null) {
					school_img = jObj.getString(Constant.ITEM_SD_IMG);
					school_address = jObj.getString(Constant.ITEM_SD_ADDRESS);
					school_name = jObj.getString(Constant.ITEM_SD_NAME);
					school_telephone = jObj.getString(Constant.ITEM_SD_TELEPHONE);
					school_lessons = jObj.getString(Constant.ITEM_SD_LESSONS);
					school_max_students = jObj.getString(Constant.ITEM_SD_MAX_STUDENTS);
					school_material = jObj.getString(Constant.ITEM_SD_MATERIAL);
					school_test = jObj.getString(Constant.ITEM_SD_TEST);
					school_registration = jObj.getString(Constant.ITEM_SD_REGISTRATION);
					school_accommodation = jObj.getString(Constant.ITEM_SD_ACCOMMODATION);
					school_wifi = jObj.getString(Constant.ITEM_SD_WIFI);
					school_corpo = jObj.getString(Constant.ITEM_SD_CORPO);
					school_email = jObj.getString(Constant.ITEM_SD_EMAIL);
					school_verify = jObj.getString(Constant.ITEM_SD_VERIFY);

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
				//runOnUiThread(new Runnable() {
					//public void run() {
						mProgressBar.setVisibility(View.INVISIBLE);
						showDetail(true);
						imageLoader.DisplayImage(school_img, imgView);
						txt_school_name.setText(school_name);
						txt_school_address.setText(school_address);
						txt_school_telephone.setText(school_telephone);
						txt_lessons.setText(school_lessons);
						txt_max_students.setText(school_max_students);
						txt_material.setText(school_material);
						txt_test.setText(school_test);
						txt_registration.setText(school_registration);
						txt_accommodation.setText(school_accommodation);
						txt_wifi.setText(school_wifi);
						txt_corpo.setText(school_corpo);
						txt_email.setText(school_email);
						txt_verify.setText(school_verify);

					}
				//});
			//}
		}
	}

	//	public void buttonHeader() {
	//		Button send_mail = (Button) findViewById(R.id.btn_back);
	//		send_mail.setOnClickListener(new View.OnClickListener() {
	//			@Override
	//			public void onClick(View view) {
	//				finish();
	//				//overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
	//			}
	//		});
	//	}

	public void btnSendMail() {
		Button btn_send_mail = (Button) findViewById(R.id.sendMail);
		btn_send_mail.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent i = new Intent(SchoolDetail.this, SendRequestActivity.class);
				i.putExtra("school_id", school_id);
				i.putExtra("school_name", school_name);
				i.putExtra("school_email", school_email);
				i.putExtra("school_verify", school_verify);
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
				new LoadSchoolDetail().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void showDetail(boolean set) {
		findViewById(R.id.det_id).setVisibility(set ? View.VISIBLE : View.GONE);
		btnSendMail();
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

