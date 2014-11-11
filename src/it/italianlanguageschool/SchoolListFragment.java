package it.italianlanguageschool;

import android.app.Fragment;
import android.content.Intent;
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

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SchoolListFragment extends Fragment {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	ArrayList<SchoolItem> schoolList;
	JSONArray item = null;
	RelativeLayout rl;
	String titolo, tipo, kId= "";
	boolean fisso = false;
	private TextView listTitle;
	ListView list;
	SchoolListAdapter adapter;
	private String err = null;
	protected ProgressBar mProgressBar;
	LocaleLanguage locLanguage = new LocaleLanguage();

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		@SuppressWarnings("unused")
		Bundle bundle = this.getArguments();
		if(getArguments()!=null) {
			tipo = getArguments().getString("tipo");
			titolo = getArguments().getString("titolo");
			kId = getArguments().getString("item_id");
			fisso = getArguments().getBoolean("fisso");
		}
		
		//schoolList = new ArrayList<HashMap<String, String>>();
		cd = new ConnectionDetector(getActivity());
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rl = (RelativeLayout)inflater.inflate(R.layout.school_list, container, false);
		mProgressBar = (ProgressBar) rl.findViewById(R.id.progressBar);
		listTitle = (TextView) rl.findViewById(R.id.title_list);
		listTitle.setText(titolo);
		list = (ListView) rl.findViewById(android.R.id.list);

		if(savedInstanceState!= null) {
			schoolList = ((ArrayList<SchoolItem>) savedInstanceState.getSerializable("consigliList"));
			adapter = new SchoolListAdapter(getActivity(), schoolList);
			list.setAdapter(adapter);				
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if (cd.isConnectingToInternet()) {
						Intent i = new Intent(getActivity(),SchoolDetail.class);
						String school_id = ((TextView) view.findViewById(R.id.school_id)).getText().toString();
						i.putExtra("school_id", school_id);
						startActivity(i);
					}
				}
			});
		}
		else {
			mProgressBar.setVisibility(View.VISIBLE);
			new LoadList().execute();
		}
		

		return rl;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("consigliList", schoolList);
	}



	class LoadList extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);
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
			if(mProgressBar.getVisibility()==0)
				mProgressBar.setVisibility(View.INVISIBLE);
			getActivity().setProgressBarIndeterminateVisibility(false);

			if(err!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			}
			else {
				getActivity().runOnUiThread(new Runnable() {
					public void run() { 
						list.setScrollingCacheEnabled(false);
						adapter = new SchoolListAdapter(getActivity(), schoolList);
						list.setAdapter(adapter);

						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
								if (cd.isConnectingToInternet()) {
									Intent i = new Intent(getActivity(),SchoolDetail.class);
									String school_id = ((TextView) view.findViewById(R.id.school_id)).getText().toString();
									i.putExtra("school_id", school_id);
									startActivity(i);
								}
							}
						});
					}
				});
			}
		}
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
       switch (item.getItemId()) {
          case R.id.action_refresh:
				if (cd.isConnectingToInternet())
					new LoadList().execute();
             return true;
          default:
             return super.onOptionsItemSelected(item);
       }
    }

}


