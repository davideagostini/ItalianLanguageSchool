package it.italianlanguageschool;
//package it.italianlanguageschool;
//
//import it.italianlanguageschool.utility.ConnectionDetector;
//import it.italianlanguageschool.utility.JSONParser;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import org.apache.http.NameValuePair;
//import org.apache.http.message.BasicNameValuePair;
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import android.app.Fragment;
//import android.os.AsyncTask;
//import android.os.Bundle;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.Button;
//import android.widget.ListView;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//public class NewsFragment extends Fragment {
//
//	ConnectionDetector cd;
//	JSONParser jsonParser = new JSONParser();
//	ArrayList<HashMap<String, String>> newsList;
//	JSONArray news = null;
//	RelativeLayout rl;
//	ListView list;
//	NewsListAdapter adapter;
//	int i=0;
//	private String err = null;
//	private int paginazione = 1;
//	private int step = 10;
//	private Button btn_more;
//	private View footerView;
//	protected ProgressBar mProgressBar;
//	private boolean btnMore = true;
//	private TextView listTitle;
//	private String titolo = "";
//
//
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		@SuppressWarnings("unused")
//		Bundle bundle = this.getArguments();
//		if(getArguments()!=null) {
//			titolo = getArguments().getString("titolo");
//		}
//		
//		newsList = new ArrayList<HashMap<String, String>>();
//		cd = new ConnectionDetector(getActivity());
//	}
//
//	@SuppressWarnings("unchecked")
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//
//		rl = (RelativeLayout)inflater.inflate(R.layout.news_list, container, false);
//		mProgressBar = (ProgressBar) rl.findViewById(R.id.progressBar);
//		listTitle = (TextView) rl.findViewById(R.id.title_list);
//		listTitle.setText(titolo);
//
//		list = (ListView) rl.findViewById(android.R.id.list);
//		if(savedInstanceState!= null)
//			btnMore = savedInstanceState.getBoolean("btnMore");
//		
//		if(btnMore) {
//			footerView = inflater.inflate(R.layout.btn_more, null);
//			list.addFooterView(footerView);
//
//			btn_more = (Button) footerView.findViewById(R.id.loadMore);
//			btn_more.setOnClickListener(new View.OnClickListener() {
//				@Override
//				public void onClick(View view) {
//					if (!cd.isConnectingToInternet()) {
//						//alert.showAlertDialog(getActivity(), "Errore", "Nessuna connessione di rete disponibile.", false);
//					}
//					else {
//						paginazione++;
//						//FAR APPARIRE IL LOADING NELL'ACTION BAR
//						new LoadNews().execute();
//					}
//				}
//			});
//		}
//
//		if(savedInstanceState!= null) {
//			newsList = ((ArrayList<HashMap<String, String>>) savedInstanceState.getSerializable("newsList"));
//			paginazione = (Integer) savedInstanceState.getSerializable("paginazione");			
//			adapter = new NewsListAdapter(getActivity(), newsList);
//			list.setAdapter(adapter);				
//			list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//				@Override
//				public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
//					if (cd.isConnectingToInternet()) {
//						//COMPLETARE
//					}
//				}
//			});
//		}
//		else {
//			mProgressBar.setVisibility(View.VISIBLE);
//			new LoadNews().execute();
//		}
//
//		return rl;
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		outState.putSerializable("newsList", newsList);
//		outState.putSerializable("paginazione", paginazione);
//		outState.putBoolean("btnMore", btnMore);
//	}
//
//
//	class LoadNews extends AsyncTask<String, String, String> {
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//		}
//		protected String doInBackground(String... args) {
//			List<NameValuePair> params = new ArrayList<NameValuePair>();
//			params.add(new BasicNameValuePair("s", "n"));
//			params.add(new BasicNameValuePair("pag", Integer.toString(paginazione)));
//			params.add(new BasicNameValuePair("step", Integer.toString(step)));
//
//			String json = jsonParser.makeHttpRequest(Constant.URL_LIST_NEWS, "GET",
//					params);
//
//			try {
//				news = new JSONArray(json);
//
//				if (news != null) {
//					int limit;
//					if(news.length()>step) limit = step;
//					else {
//						limit = news.length();
//					}
//					for (int i = 0; i < limit; i++) {
//						JSONObject c = news.getJSONObject(i);
//
//						String url = c.getString(Constant.ITEM_NEWS_URL);
//						String titolo = c.getString(Constant.ITEM_NEWS_TITLE);
//						String descrizione = c.getString(Constant.ITEM_NEWS_DESCRIPTION);
//						String data = c.getString(Constant.ITEM_NEWS_DATA);
//						String scuola = c.getString(Constant.ITEM_NEWS_SCHOOL);
//						String thumbnail = c.getString(Constant.ITEM_NEWS_IMG);
//
//
//						HashMap<String, String> map = new HashMap<String, String>();
//
//						map.put(Constant.ITEM_NEWS_URL, url);
//						map.put(Constant.ITEM_NEWS_TITLE, titolo);
//						map.put(Constant.ITEM_NEWS_DESCRIPTION, descrizione);
//						map.put(Constant.ITEM_NEWS_DATA, data);
//						map.put(Constant.ITEM_NEWS_SCHOOL, scuola);
//						map.put(Constant.ITEM_NEWS_IMG, thumbnail);
//
//						newsList.add(map);
//
//						if (isCancelled()) break;
//					}
//				} else {
//					//Log.d("consigli: ", "null");
//					err = "Nessuna news disponibile";
//				}
//
//			} catch (JSONException e) {
//				e.printStackTrace();
//				err = "Impossibile scaricare le news. Riprova più tardi.";
//			}
//
//			return null;
//		}
//
//		protected void onPostExecute(String file_url) {
//			mProgressBar.setVisibility(View.INVISIBLE);
//			if(err!=null) {
//				//alert.showAlertDialog(getActivity(), "Errore", err, false);
//			}
//			else {
//				//getActivity().runOnUiThread(new Runnable() {
//					//public void run() { 
//						list.setScrollingCacheEnabled(false);
//
//						int currentPosition = list.getFirstVisiblePosition();
//
//						if(news.length()<step) {
//							list.removeFooterView(footerView);
//							btnMore = false;
//						}
//
//						adapter = new NewsListAdapter(getActivity(), newsList);
//						list.setAdapter(adapter);
//						int pag = (paginazione==1 ? currentPosition : currentPosition + 1);
//						list.setSelectionFromTop(pag, 0);
//						
//
//						list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//							@Override
//							public void onItemClick(AdapterView<?> arg0, View view, int arg2, long arg3) {
//								if (cd.isConnectingToInternet()) {
//									//COMPLETARE
//								}
//							}
//						});
//					//}
//				//});
//			}
//		}
//	}
//}
//
