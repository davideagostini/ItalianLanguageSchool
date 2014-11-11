package it.italianlanguageschool;

import it.italianlanguageschool.classes.SchoolItem;
import it.italianlanguageschool.utility.ConnectionDetector;
import it.italianlanguageschool.utility.JSONParser;
import it.italianlanguageschool.utility.LocaleLanguage;
import it.italianlanguageschool.utility.MultiSelectionSpinner;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;


public class SearchSchoolFragment extends Fragment {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	private List<String> regions, idRegions;
	private List<String> features, idFeatures;
	private List<String> extra, idExtra;
	private List<String> membership, idMembership;
	private List<Integer> selectedIndexRegions, selectedIndexFeatures, selectedIndexExtra, selectedIndexMembership;	
	private JSONArray myJSON = null;
	private LinearLayout ll;
	private ListView list;
	private String err=null;
	private MultiSelectionSpinner sFeatures, sRegion ,sExtra, sMembership;
	LocaleLanguage locLanguage = new LocaleLanguage();
	private SchoolListAdapter adapter;
	private ArrayList<SchoolItem> schoolList;
	private JSONArray jSchool = null;
	private TextView noResult;
	private TextView listTitle;
	private String titolo;

	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Bundle bundle = this.getArguments();
		if(getArguments()!=null) {
			titolo = getArguments().getString("titolo");
		}		
		cd = new ConnectionDetector(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ll = (LinearLayout)inflater.inflate(R.layout.search_school, container, false);
		listTitle = (TextView) ll.findViewById(R.id.title_list);
		listTitle.setText(titolo);
		list = (ListView) ll.findViewById(android.R.id.list);
		noResult = (TextView) ll.findViewById(R.id.search_result);

		sFeatures = (MultiSelectionSpinner) ll.findViewById(R.id.spinner1);
		sRegion = (MultiSelectionSpinner) ll.findViewById(R.id.spinner2);
		sExtra = (MultiSelectionSpinner) ll.findViewById(R.id.spinner3);
		sMembership = (MultiSelectionSpinner) ll.findViewById(R.id.spinner4);

		setButton();

		if (cd.isConnectingToInternet()) {
			new LoadSpinner().execute();
		}

		return ll;
	}

	class LoadSpinner extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));

			String json = jsonParser.makeHttpRequest(Constant.URL_SEARCH_SCHOOL, "GET",
					params);

			try {
				regions = new ArrayList<String>();
				idRegions = new ArrayList<String>();
				features = new ArrayList<String>();
				idFeatures = new ArrayList<String>();
				extra = new ArrayList<String>();
				idExtra = new ArrayList<String>();
				membership = new ArrayList<String>();
				idMembership = new ArrayList<String>();
				myJSON = new JSONArray(json);

				int j = 0;
				int type_prev = 0;

				if (myJSON != null) {
					for (int i = 0; i < myJSON.length(); i++) {
						JSONObject c = myJSON.getJSONObject(i);

						int type = Integer.parseInt(c.getString(Constant.SEARCH_SCHOOL_TYPE));
						String id = c.getString(Constant.SEARCH_SCHOOL_ID);
						String titolo = c.getString(Constant.SEARCH_SCHOOL_TITLE);

						if(type != type_prev) {
							j = 0;
							type_prev = type;
						}

						switch(type) {
						case 4:
							idRegions.add(j,id);
							regions.add(j, titolo);
							break;
						case 2:
							idFeatures.add(j,id);
							features.add(j, titolo);
							break;
						case 1:
							idExtra.add(j,id);
							extra.add(j, titolo);
							break;
						case 3:
							idMembership.add(j,id);
							membership.add(j, titolo);
							break;
						}

						j++;

						if (isCancelled()) break;
					}
				} else {
					err = "Nessun corso di laurea trovato";
				}

			} catch (JSONException e) {
				err = "Impossibile effettuare la ricerca. Riprova pi tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			getActivity().setProgressBarIndeterminateVisibility(false);
			if(err!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			}
			else {
				sRegion.setItems(regions);
				sFeatures.setItems(features);
				sExtra.setItems(extra);
				sMembership.setItems(membership);
				showSearchForm(true);
			}
		}
	}

	class LoadSchool extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);
		}
		protected String doInBackground(String... args) {

			selectedIndexRegions = sRegion.getSelectedIndicies();
			selectedIndexFeatures = sFeatures.getSelectedIndicies();
			selectedIndexExtra = sExtra.getSelectedIndicies();
			selectedIndexMembership = sMembership.getSelectedIndicies();

			List<NameValuePair> params = new ArrayList<NameValuePair>();

			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));
			params.add(new BasicNameValuePair("t", "4"));
			params.add(new BasicNameValuePair("r", getSelectedIndex(selectedIndexRegions, idRegions)));
			params.add(new BasicNameValuePair("f", getSelectedIndex(selectedIndexFeatures, idFeatures)));
			params.add(new BasicNameValuePair("e", getSelectedIndex(selectedIndexExtra, idExtra)));
			params.add(new BasicNameValuePair("m", getSelectedIndex(selectedIndexMembership, idMembership)));

			String json = jsonParser.makeHttpRequest(Constant.URL_LIST_SCHOOL, "GET",
					params);

			try {
				schoolList = new ArrayList<SchoolItem>();
				jSchool = new JSONArray(json);

				if (jSchool != null) {
					for (int i = 0; i < jSchool.length(); i++) {
						JSONObject c = jSchool.getJSONObject(i);

						String id = c.getString(Constant.ITEM_SCHOOL_ID);
						String titolo = c.getString(Constant.ITEM_SCHOOL_TITLE);
						String indirizzo = c.getString(Constant.ITEM_SCHOOL_INDIRIZZO);
						String descrizione = c.getString(Constant.ITEM_SCHOOL_DESCRIPTION);
						String thumbnail = c.getString(Constant.ITEM_SCHOOL_IMG);

						SchoolItem item = new SchoolItem(id, titolo, indirizzo, descrizione, thumbnail);
						schoolList.add(item);

						if (isCancelled()) break;
					}
				} else {
					//Log.d("consigli: ", "null");
					err = "Nessun corso di laurea trovato";
				}

			} catch (JSONException e) {
				//e.printStackTrace();
				err = "Impossibile effettuare la ricerca. Riprova pi tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			getActivity().setProgressBarIndeterminateVisibility(false);
			if(err!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			}
			else {
				showBtnMostraForm(true);
				
				if(schoolList.isEmpty())
					noResult.setText(R.string.nothing_school);
				else
					noResult.setText(R.string.result_search_school);

				list.setScrollingCacheEnabled(false);

				adapter = new SchoolListAdapter(getActivity(), schoolList);
				list.setAdapter(adapter);

				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) { 
						Intent i = new Intent(getActivity(),SchoolDetail.class);
						String school_id = ((TextView) view.findViewById(R.id.school_id)).getText().toString();
						i.putExtra("school_id", school_id);
						startActivity(i);
					}
				});

			}
		}
	}

	public void setButton() {
		final Button btn_cerca = (Button) ll.findViewById(R.id.btn_search_corso);
		final Button showForm = (Button) ll.findViewById(R.id.btn_showSearch);

		btn_cerca.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				if (cd.isConnectingToInternet()) {
					//showBtnMostraForm(true);
					showSearchForm(false);
					//noResult.setVisibility(View.VISIBLE);

					new LoadSchool().execute();
				}
			}
		});

		showForm.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				showBtnMostraForm(false);
				showSearchForm(true);
			}
		});
	}

	public void showBtnMostraForm(boolean set) {
		ll.findViewById(R.id.btn_showSearch).setVisibility(set ? View.VISIBLE : View.GONE);
		ll.findViewById(R.id.myList).setVisibility(set ? View.VISIBLE : View.GONE);
		noResult.setVisibility(set ? View.VISIBLE : View.GONE);
	}

	public void showSearchForm(boolean set) {
		((ScrollView)ll.findViewById(R.id.search_form)).setVisibility(set ? View.VISIBLE : View.GONE);
	}

	public String getSelectedIndex(List<Integer> a, List<String> b) {
		String index = "";
		for(int i=0;i<a.size();i++) {
			if(i+1 == a.size())
				index += b.get(a.get(i));
			else
				index += b.get(a.get(i))+",";
		}
		return index;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.findItem(R.id.action_refresh).setVisible(false);
		super.onPrepareOptionsMenu(menu);
	}

}


