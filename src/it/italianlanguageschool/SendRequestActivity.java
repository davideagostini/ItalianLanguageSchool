package it.italianlanguageschool;

import it.italianlanguageschool.utility.JSONParser;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

public class SendRequestActivity extends Activity{

	JSONParser jsonParser = new JSONParser();
	private String sId, sSchoolName, sSchoolEmail, sSchoolVerify, sName, sSurname, sNationality, sEmail, sRequest = null;
	private String response = "";
	private TextView tvResponse;
	private static final String EMAIL_PATTERN = 
			"^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.information_request);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		setProgressBarIndeterminateVisibility(false);
		
		tvResponse = (TextView) findViewById(R.id.send_response);

		Intent i = getIntent();
		sId = i.getStringExtra("school_id");
		sSchoolName = i.getStringExtra("school_name");
		sSchoolEmail = i.getStringExtra("school_email");
		sSchoolVerify = i.getStringExtra("school_verify");

	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main, menu);
		MenuItem item = menu.findItem(R.id.action_refresh);
		item.setVisible(false);
		MenuItem item2 = menu.findItem(R.id.action_send);
		item2.setVisible(true);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			finish();
			return true;
		case R.id.action_send:
			sendMail();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	public void sendMail() {
		final EditText eName = (EditText) findViewById(R.id.f_name);
		final EditText eSurname = (EditText) findViewById(R.id.f_surname);
		final EditText eNationality = (EditText) findViewById(R.id.f_nationality);
		final EditText eEmail = (EditText) findViewById(R.id.f_email);
		final EditText eRequest = (EditText) findViewById(R.id.f_request);
		
		sName = eName.getText().toString();
		sSurname = eSurname.getText().toString();
		sNationality = eNationality.getText().toString();
		sEmail = eEmail.getText().toString();
		sRequest = eRequest.getText().toString();
		
		if(sName.isEmpty() || sSurname.isEmpty() || sNationality.isEmpty() || sEmail.isEmpty() || sRequest.isEmpty()) {
			tvResponse.setText(R.string.send_all_fields);
			tvResponse.setVisibility(View.VISIBLE);
		}
		else {
			if(sEmail.matches(EMAIL_PATTERN))
				new SendInformationRequest().execute();
			else {
				tvResponse.setText(R.string.send_mail_valid);
				tvResponse.setVisibility(View.VISIBLE);
			}
		}
	}


	class SendInformationRequest extends AsyncTask<String, String, String> {
		protected void onPreExecute() {
			super.onPreExecute();
			setProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) { 
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("id", sId));
			params.add(new BasicNameValuePair("nome_scuola", sSchoolName));
			params.add(new BasicNameValuePair("email_scuola", sSchoolEmail));
			params.add(new BasicNameValuePair("tipo_verifica", sSchoolVerify));
			params.add(new BasicNameValuePair("name", sName));
			params.add(new BasicNameValuePair("surname", sSurname));
			params.add(new BasicNameValuePair("nationality", sNationality));
			params.add(new BasicNameValuePair("email", sEmail));
			params.add(new BasicNameValuePair("request", sRequest));
			
			
			response = jsonParser.makeHttpRequest(Constant.URL_CONTACT, "POST", params);

			return null;
		}

		protected void onPostExecute(String file_url) {
			setProgressBarIndeterminateVisibility(false);
			
			if(response.trim().equalsIgnoreCase("ok"))
				tvResponse.setText(R.string.send_ok);
			else
				tvResponse.setText(R.string.send_no_ok);
			
			tvResponse.setVisibility(View.VISIBLE);
			
			Timer t = new Timer(false);
			t.schedule(new TimerTask() {
			  @Override
			  public void run() {
			       runOnUiThread(new Runnable() {
			            public void run() {
			            	tvResponse.setVisibility(View.INVISIBLE);
			            	if(response.trim().equalsIgnoreCase("ok")) {
			            		finish();
			            		overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
			            	}
			            }
			        });
			    }
			}, 5000);			
		}
	}

	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(R.animator.slide_in_up, R.animator.slide_out_up);
	}

}
