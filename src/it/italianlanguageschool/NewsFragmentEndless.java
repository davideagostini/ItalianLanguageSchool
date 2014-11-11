package it.italianlanguageschool;

import it.italianlanguageschool.classes.NewsItem;
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

import android.app.Fragment;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class NewsFragmentEndless extends Fragment {

	ConnectionDetector cd;
	JSONParser jsonParser = new JSONParser();
	ArrayList<NewsItem> newsList;
	JSONArray news = null;
	RelativeLayout rl;
	ListView list;
	NewsListAdapter adapter;
	int i=0;
	private String err = null;
	private int paginazione = 1;
	protected ProgressBar mProgressBar;
	private TextView listTitle;
	private String titolo = "";
	LocaleLanguage locLanguage = new LocaleLanguage();
	private String tot_pag;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);

		@SuppressWarnings("unused")
		Bundle bundle = this.getArguments();
		if(getArguments()!=null) {
			titolo = getArguments().getString("titolo");
		}

		cd = new ConnectionDetector(getActivity());
		newsList = new ArrayList<NewsItem>();
	}

	@SuppressWarnings("unchecked")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		rl = (RelativeLayout)inflater.inflate(R.layout.news_list, container, false);
		mProgressBar = (ProgressBar) rl.findViewById(R.id.progressBar);
		listTitle = (TextView) rl.findViewById(R.id.title_list);
		listTitle.setText(titolo);

		list = (ListView) rl.findViewById(android.R.id.list);
		list.setOnScrollListener(new EndlessScrollListener());

		if(savedInstanceState!= null) {
			newsList = ((ArrayList<NewsItem>) savedInstanceState.getSerializable("newsList"));
			paginazione = (Integer) savedInstanceState.getSerializable("paginazione");			
			adapter = new NewsListAdapter(getActivity(), newsList);
			list.setAdapter(adapter);				
			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
					if (cd.isConnectingToInternet()) {
						Intent i = new Intent(getActivity(), GenericWebView.class);
						String url = ((TextView) view
								.findViewById(R.id.news_url)).getText()
								.toString();
						i.putExtra("url", url);
						startActivity(i);
					}
				}
			});
		}
		else {
			mProgressBar.setVisibility(View.VISIBLE);
			new LoadNews().execute(String.valueOf(paginazione));
		}


		return rl;
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("newsList", newsList);
		outState.putSerializable("paginazione", paginazione);
	}


	class LoadNews extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);

		}
		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			//params.add(new BasicNameValuePair("s", "n"));
			params.add(new BasicNameValuePair("pag", args[0]));
			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));

			String json = jsonParser.makeHttpRequest(Constant.URL_LIST_NEWS, "GET",
					params);
			//Log.d("PAGE VALE","PAGE VALE----------->"+args[0]);
			
			try {
				news = new JSONArray(json);
				//newsList = new ArrayList<NewsItem>();

				if (news != null) {
					for (int i = 0; i < news.length(); i++) {
						if(i==0) {
							JSONObject c = news.getJSONObject(i);
							if(c.has("num_pages"))
								tot_pag = c.getString("num_pages");
						} 
						else {
							JSONObject c = news.getJSONObject(i);

							String url = c.getString(Constant.ITEM_NEWS_URL);
							String titolo = c.getString(Constant.ITEM_NEWS_TITLE);
							String descrizione = c.getString(Constant.ITEM_NEWS_DESCRIPTION);
							String data = c.getString(Constant.ITEM_NEWS_DATA);
							String scuola = c.getString(Constant.ITEM_NEWS_SCHOOL);
							String thumbnail = c.getString(Constant.ITEM_NEWS_IMG);

							NewsItem item = new NewsItem(url,titolo,data,descrizione,scuola,thumbnail);
							newsList.add(item);
						}

						if (isCancelled()) break;	
					}

					//System.out.println("numero pagine: "+tot_pag);

					//for(int j=0; j<newsList.size(); j++)
						//System.out.println(newsList.get(j).getTitolo());
				} 
				else {
					err = "Nessuna news disponibile";
				}

			} catch (JSONException e) {
				e.printStackTrace();
				err = "Impossibile scaricare le news. Riprova pi tardi.";
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
				int currentPosition = list.getFirstVisiblePosition();
				adapter = new NewsListAdapter(getActivity(), newsList);
				list.setAdapter(adapter);
				//int pag = (paginazione==1 ? currentPosition : currentPosition + 1);
				list.setSelection(currentPosition);


				list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
						if (cd.isConnectingToInternet()) {
							Intent i = new Intent(getActivity(), GenericWebView.class);
							String url = ((TextView) view
									.findViewById(R.id.news_url)).getText()
									.toString();
							i.putExtra("url", url);
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
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			if (cd.isConnectingToInternet()) {
				//Log.d("paginazione","paginazione----------->"+paginazione);
				newsList = new ArrayList<NewsItem>();
				for(int i=1; i<=paginazione; i++)
					new LoadNews().execute(String.valueOf(i));
			}
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public class EndlessScrollListener implements OnScrollListener {

		private int visibleTreshold = 5;

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) { }

		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if(scrollState == SCROLL_STATE_IDLE) {
				if(list.getLastVisiblePosition() >= list.getCount() - visibleTreshold) {
					if(paginazione<Integer.parseInt(tot_pag) && cd.isConnectingToInternet()) {
						paginazione++;
						new LoadNews().execute(String.valueOf(paginazione));
					}
				}
			}
		}
	}
}

