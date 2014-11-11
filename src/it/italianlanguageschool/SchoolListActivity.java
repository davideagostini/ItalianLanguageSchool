package it.italianlanguageschool;

import it.italianlanguageschool.classes.SchoolItem;
import it.italianlanguageschool.utility.ConnectionDetector;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

public class SchoolListActivity extends Activity {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	ArrayList<SchoolItem> schoolList;
	JSONArray item = null;
	ListView list;
	SchoolListAdapter adapter;
	private String err = null;
	String titolo, sub_titolo, tipo, kId= "";
	boolean fisso;
	private TextView listTitle;
	protected ProgressBar mProgressBar;
	LocaleLanguage locLanguage = new LocaleLanguage();
	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private MyBroadcastReceiver connectionReceiver;


	@SuppressWarnings("unchecked")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.animator.push_left_in, R.animator.push_left_out);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.school_list_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);
		
		connectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(connectionReceiver, filter);

		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		list = (ListView) findViewById(android.R.id.list);

		Intent i = getIntent();
		Bundle extras = i.getExtras();
		if (extras != null) {
			tipo = i.getStringExtra("tipo");
			titolo = i.getStringExtra("titolo");
			sub_titolo = i.getStringExtra("sub_titolo");
			kId = i.getStringExtra("item_id");
			fisso = i.getBooleanExtra("fisso", false);
		}
		listTitle = (TextView) findViewById(R.id.title_list);
		listTitle.setText(titolo+((sub_titolo.isEmpty()||sub_titolo.equals("")) ? "" : " - "+sub_titolo));

		cd = new ConnectionDetector(getApplicationContext());
		if(savedInstanceState != null) {
			schoolList = ((ArrayList<SchoolItem>) savedInstanceState.getSerializable("schoolList"));
			list.setScrollingCacheEnabled(false);
			adapter = new SchoolListAdapter(SchoolListActivity.this, schoolList);
			list.setAdapter(adapter);

			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if (cd.isConnectingToInternet()) {
						Intent i = new Intent(SchoolListActivity.this,SchoolDetail.class);
						String school_id = ((TextView) view.findViewById(R.id.school_id)).getText().toString();
						i.putExtra("school_id", school_id);
						startActivity(i);
					}
				}
			});
		}
		else {
			mProgressBar.setVisibility(View.VISIBLE);
			new LoadListSchool().execute();
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
		outState.putSerializable("schoolList", schoolList);
	}

	class LoadListSchool extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));

			if(fisso==true) {
				params.add(new BasicNameValuePair("t", "1"));
				params.add(new BasicNameValuePair("s", tipo));
				params.add(new BasicNameValuePair("k", kId));
			}
			else
				params.add(new BasicNameValuePair("t", tipo));

			String json = jsonParser.makeHttpRequest(Constant.URL_LIST_SCHOOL, "GET",params);

			try {
				item = new JSONArray(json);
				schoolList = new ArrayList<SchoolItem>();

				if (item != null) {
					for (int i = 0; i < item.length(); i++) {
						JSONObject c = item.getJSONObject(i);

						String id = c.getString(Constant.ITEM_SCHOOL_ID);
						String titolo = c.getString(Constant.ITEM_SCHOOL_TITLE);
						String indirizzo = c.getString(Constant.ITEM_SCHOOL_INDIRIZZO);
						String descrizione = c.getString(Constant.ITEM_SCHOOL_DESCRIPTION);
						String thumbnail = c.getString(Constant.ITEM_SCHOOL_IMG);
						
						SchoolItem item = new SchoolItem(id, titolo, indirizzo, descrizione, thumbnail);
						schoolList.add(item);
					}
				} else {
					err = "Nessuna informazione disponibile";
				}

			} catch (JSONException e) {
				e.printStackTrace();
				err = "Impossibile scaricare la lista delle scuole. Riprova pi tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			mProgressBar.setVisibility(View.INVISIBLE);
			setProgressBarIndeterminateVisibility(false);

			if(err!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			}
			else {
				//runOnUiThread(new Runnable() {
					//public void run() { 
						list.setScrollingCacheEnabled(false);
						adapter = new SchoolListAdapter(SchoolListActivity.this, schoolList);
						list.setAdapter(adapter);

						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
								if (cd.isConnectingToInternet()) {
									Intent i = new Intent(SchoolListActivity.this,SchoolDetail.class);
									String school_id = ((TextView) view.findViewById(R.id.school_id)).getText().toString();
									i.putExtra("school_id", school_id);
									startActivity(i);
								}
							}
						});
					//}
				//});
			}
		}
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
				new LoadListSchool().execute();
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

