package it.italianlanguageschool;

import it.italianlanguageschool.classes.MapMarker;
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
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends Fragment implements OnMarkerClickListener, OnInfoWindowClickListener{

	private GoogleMap map;
	private String err = null;
	JSONParser jsonParser = new JSONParser();
	ArrayList<MapMarker> mapList;
	JSONArray item = null;
	LocaleLanguage locLanguage = new LocaleLanguage();
	private MapMarker mapMarker;
	private String idMyMarker = "";
	private ConnectionDetector cd;
	private static View view;


	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		cd = new ConnectionDetector(getActivity());
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	
		if (view != null) {
	        ViewGroup parent = (ViewGroup) view.getParent();
	        if (parent != null)
	            parent.removeView(view);
	    }
	    try {
			view = inflater.inflate(R.layout.map_activity, null, false);
			initializeMap();
			new LoadMapSchool().execute();
	    } 
	    catch (InflateException e) { }
		catch (Exception e) { e.printStackTrace(); }

		return view;
	}

	private void initializeMap() {
		if (map == null) {
			map=((MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment)).getMap();
			if (map == null) {
				Toast.makeText(getActivity(),"Sorry! unable to create maps", Toast.LENGTH_SHORT).show();
			}
			CameraPosition cameraPosition = new CameraPosition.Builder().target(
					new LatLng(41.91, 12.52)).zoom(5).build();
			map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
		}
	}

	private void initializeMarker(LatLng ll, String title, String country, String id) {

		// adding marker
		//MarkerOptions marker = new MarkerOptions().position(STARTING_POINT).title("Il mio pin");	 
		//marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_place));
		//map.addMarker(new MarkerOptions().position(STARTING_POINT).title("Il mio pin"));
		//map.addMarker(marker);

		map.addMarker(new MarkerOptions().position(ll).title(title).snippet(id+","+country));
		//map.setMyLocationEnabled(true);
		map.setOnMarkerClickListener(this);
		map.setInfoWindowAdapter(new MyInfoWindowAdaper(getActivity()));
		map.setOnInfoWindowClickListener(this);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		MapFragment f = (MapFragment) getFragmentManager().findFragmentById(R.id.mapFragment);
		if (f != null) 
			getFragmentManager().beginTransaction().remove(f).commit();
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		return false;
	}

	@Override
	public void onInfoWindowClick(Marker marker) {
		Intent i = new Intent(getActivity(),SchoolDetail.class);
		i.putExtra("school_id", idMyMarker);
		startActivity(i);
	}

	class MyInfoWindowAdaper implements InfoWindowAdapter {

		private final View myContentView;

		MyInfoWindowAdaper(Context context) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			myContentView = inflater.inflate(R.layout.map_info_contents, null);
		}

		@Override
		public View getInfoContents(Marker marker) {
			TextView title = ((TextView)myContentView.findViewById(R.id.title));
			title.setText(marker.getTitle());
			TextView snippet = ((TextView)myContentView.findViewById(R.id.snippet));

			String myMarker[] = marker.getSnippet().split(",");
			idMyMarker = myMarker[0];

			snippet.setText(myMarker[1]);

			return myContentView;
		}

		@Override
		public View getInfoWindow(Marker arg0) {
			return null;
		}
	}


	class LoadMapSchool extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			getActivity().setProgressBarIndeterminateVisibility(true);
		}

		protected String doInBackground(String... args) {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("l", String.valueOf(locLanguage.getLang())));
			String json = jsonParser.makeHttpRequest(Constant.URL_SCHOOL_MAP, "GET",params);

			try {
				item = new JSONArray(json);
				mapList = new ArrayList<MapMarker>();

				if (item != null) {
					for (int i = 0; i < item.length(); i++) {
						JSONObject c = item.getJSONObject(i);

						String id = c.getString(Constant.ITEM_MAP_ID);
						String lat = c.getString(Constant.ITEM_MAP_LAT);
						String lng = c.getString(Constant.ITEM_MAP_LNG);
						String school = c.getString(Constant.ITEM_MAP_SCHOOL);
						String comune = c.getString(Constant.ITEM_MAP_COUNTRY);

						mapMarker = new MapMarker(id,lat,lng,school,comune);
						mapList.add(mapMarker);
					}
				} else {
					//Log.d("consigli: ", "null");
					err = "Nessuna informazione disponibile";
				}

			} catch (JSONException e) {
				e.printStackTrace();
				err = "Impossibile scaricare i dati. Riprova pi tardi.";
			}

			return null;
		}

		protected void onPostExecute(String file_url) {
			getActivity().setProgressBarIndeterminateVisibility(false);

			if(err!=null) {
				//alert.showAlertDialog(getActivity(), "Errore", err, false);
			}
			else {
				for(int i = 0; i < mapList.size(); i++) {
					LatLng point = new LatLng(Double.parseDouble(mapList.get(i).getLat()),Double.parseDouble(mapList.get(i).getLng()));
					initializeMarker(point, mapList.get(i).getTitle(), mapList.get(i).getCountry(), mapList.get(i).getId());
				}
				//listenerMarker();
			}
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			if (cd.isConnectingToInternet())
				new LoadMapSchool().execute();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}
