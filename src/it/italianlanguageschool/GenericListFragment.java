package it.italianlanguageschool;

import android.app.Fragment;
import android.content.Intent;

import it.italianlanguageschool.classes.GenericItem;
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

public class GenericListFragment extends Fragment {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	ArrayList<GenericItem> itemList;
	JSONArray item = null;
	RelativeLayout rl;
	String titolo, tipo= "";
	private TextView listTitle;
	ListView list;
	GenericListAdapter adapter;
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
		}

//		itemList = new ArrayList<HashMap<String, String>>();
		cd = new ConnectionDetector(getActivity());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rl = (RelativeLayout)inflater.inflate(R.layout.generic_list, container, false);	
		mProgressBar = (ProgressBar) rl.findViewById(R.id.progressBar);
		listTitle = (TextView) rl.findViewById(R.id.title_list);
		listTitle.setText(titolo);
		list = (ListView) rl.findViewById(android.R.id.list);

		if(savedInstanceState!= null) {
			itemList = ((ArrayList<GenericItem>) savedInstanceState.getSerializable("consigliList"));
			adapter = new GenericListAdapter(getActivity(), itemList);
			list.setAdapter(adapter);				
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if (cd.isConnectingToInternet()) {
						String item_id = ((TextView) view.findViewById(R.id.item_id)).getText().toString();		
						String item_title = ((TextView) view.findViewById(R.id.item_title)).getText().toString();		
						Intent i = new Intent(getActivity(),SchoolListActivity.class);
						i.putExtra("fisso", true);
						i.putExtra("tipo", tipo);
						i.putExtra("titolo", titolo);
						i.putExtra("sub_titolo", item_title);
						i.putExtra("item_id", item_id);
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
		outState.putSerializable("consigliList", itemList);
	}

	class LoadList extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("t", tipo));
			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));

			String json = jsonParser.makeHttpRequest(Constant.URL_LIST, "GET",params);

			try {
				item = new JSONArray(json);
				itemList = new ArrayList<GenericItem>();
				
				if (item != null) {
					for (int i = 0; i < item.length(); i++) {
						JSONObject c = item.getJSONObject(i);

						String id = c.getString(Constant.ITEM_ID);
						String titolo = c.getString(Constant.ITEM_TITLE);
						
						GenericItem item = new GenericItem(id,titolo);
						itemList.add(item);
					}
				} else {
					err = "Nessuna informazione disponibile";
				}

			} catch (JSONException e) {
				e.printStackTrace();
				err = "Impossibile scaricare i dati. Riprova pi tardi.";
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
				//getActivity().runOnUiThread(new Runnable() {
					//public void run() { 
						list.setScrollingCacheEnabled(false);
						adapter = new GenericListAdapter(getActivity(), itemList);
						list.setAdapter(adapter);

						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
							@Override
							public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
								if (cd.isConnectingToInternet()) {
									String item_id = ((TextView) view.findViewById(R.id.item_id)).getText()
											.toString();
									String item_title = ((TextView) view.findViewById(R.id.item_title)).getText().toString();		
									Intent i = new Intent(getActivity(),SchoolListActivity.class);
									i.putExtra("fisso", true);
									i.putExtra("tipo", tipo);
									i.putExtra("titolo", titolo);
									i.putExtra("sub_titolo", item_title);
									i.putExtra("item_id", item_id);
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
	public void onDestroy() {
		super.onDestroy();
		/*if(pDialog != null) {
			if(pDialog.isShowing()) {   
				pDialog.dismiss();
			}
		}*/
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

