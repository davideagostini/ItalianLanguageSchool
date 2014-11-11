package it.italianlanguageschool;

import it.italianlanguageschool.utility.ConnectionDetector;
import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends Activity {

	private ConnectionDetector cd;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] list_1;
	private String[] list_2;
	private String[] list_3;
	private String[] list_4;

	static final String ACTION = "android.net.conn.CONNECTIVITY_CHANGE";
	private MyBroadcastReceiver connectionReceiver;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
//		int SDK_INT = android.os.Build.VERSION.SDK_INT;
//
//		if (SDK_INT>8){
//			StrictMode.setThreadPolicy( new StrictMode.ThreadPolicy.Builder()
//			.detectDiskReads()
//			.detectDiskWrites()
//			.detectNetwork()
//			.penaltyLog()
//			.build());
//
//			StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
//			.detectAll()
//			.penaltyLog()
//			.build());
//
//		}

		super.onCreate(savedInstanceState);	
		requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
		setContentView(R.layout.activity_main);
		setProgressBarIndeterminateVisibility(false);
		cd = new ConnectionDetector(MainActivity.this);
		
		connectionReceiver = new MyBroadcastReceiver();
		IntentFilter filter = new IntentFilter(ACTION);
		this.registerReceiver(connectionReceiver, filter);

		//ColorDrawable colorDrawable = new ColorDrawable();

		mTitle = mDrawerTitle = getTitle();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);

		DrawerAdapter mAdapter = new DrawerAdapter(this);        
		
		mAdapter.addHeader(R.string.title1);
		list_1 = getResources().getStringArray(R.array.list_1); 
		String[] menuOneIcons = getResources().getStringArray(R.array.icon_list_1);
		int oneIcons = 0;
		for (String item : list_1) {
			int id_menu_one = this.getResources().getIdentifier(item, "string", this.getPackageName());
			int id_menu_one_icons = getResources().getIdentifier(menuOneIcons[oneIcons], "drawable", this.getPackageName());
			ListNavigationItemModel mItem = new ListNavigationItemModel(id_menu_one, id_menu_one_icons);
			mAdapter.addItem(mItem);
			oneIcons++;
		}

		mAdapter.addHeader(R.string.title2);
		list_2 = getResources().getStringArray(R.array.list_2); 
		String[] menuTwoIcons = getResources().getStringArray(R.array.icon_list_2);
		int twoIcons = 0;
		for (String item : list_2) {
			int id_menu_one = this.getResources().getIdentifier(item, "string", this.getPackageName());
			int id_menu_one_icons = getResources().getIdentifier(menuTwoIcons[twoIcons], "drawable", this.getPackageName());
			ListNavigationItemModel mItem = new ListNavigationItemModel(id_menu_one, id_menu_one_icons);
			mAdapter.addItem(mItem);
			twoIcons++;
		}

		mAdapter.addHeader(R.string.title3);
		list_3 = getResources().getStringArray(R.array.list_3); 
		String[] menuThreeIcons = getResources().getStringArray(R.array.icon_list_3);
		int threeIcons = 0;
		for (String item : list_3) {
			int id_menu_one = this.getResources().getIdentifier(item, "string", this.getPackageName());
			int id_menu_one_icons = getResources().getIdentifier(menuThreeIcons[threeIcons], "drawable", this.getPackageName());
			ListNavigationItemModel mItem = new ListNavigationItemModel(id_menu_one, id_menu_one_icons);
			mAdapter.addItem(mItem);
			threeIcons++;
		}
		
		mAdapter.addHeader(R.string.title4);
		list_4 = getResources().getStringArray(R.array.list_4); 
		String[] menuFourIcons = getResources().getStringArray(R.array.icon_list_4);
		int fourIcons = 0;
		for (String item : list_4) {
			int id_menu_one = this.getResources().getIdentifier(item, "string", this.getPackageName());
			int id_menu_one_icons = getResources().getIdentifier(menuFourIcons[fourIcons], "drawable", this.getPackageName());
			ListNavigationItemModel mItem = new ListNavigationItemModel(id_menu_one, id_menu_one_icons);
			mAdapter.addItem(mItem);
			fourIcons++;
		}


		mDrawerList = (ListView) findViewById(R.id.left_drawer);
		if (mDrawerList != null)
			mDrawerList.setAdapter(mAdapter);	

		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);

		//colorDrawable.setColor(0xffFEBB31);
		//getActionBar().setBackgroundDrawable(colorDrawable);

		mDrawerToggle = new ActionBarDrawerToggle(
				this,
				mDrawerLayout,
				R.drawable.ic_drawer,
				R.string.drawer_open,
				R.string.drawer_close
				) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				invalidateOptionsMenu();
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				invalidateOptionsMenu();
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}
		
	}
	
//	@Override
//	protected void onPause() {
//	   unregisterReceiver(connectionReceiver);
//	   super.onPause();
//	}
	
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


	/*** COMMENTATA LA ZONA DEL MENU PER IL WEB SEARCH ***/
	
   @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Called whenever we call invalidateOptionsMenu()
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		switch(item.getItemId()) {
//		case R.id.action_websearch:
//			Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
//			intent.putExtra(SearchManager.QUERY, getActionBar().getTitle());
//			if (intent.resolveActivity(getPackageManager()) != null) {
//				startActivity(intent);
//			} else {
//				Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
//			}
//			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// update the main content by replacing fragments
		//Fragment fragment = new PlanetFragment();
		//Bundle args = new Bundle();
		//args.putInt(PlanetFragment.ARG_PLANET_NUMBER, position);
		//fragment.setArguments(args);
		Fragment fragment = new HomeFragment();

		if (!cd.isConnectingToInternet()) { 
			fragment = new NoConnectionFragment();
		}
		else {
			switch(position) {
			case 1:
				//fragment = new PostFragment();
				fragment = new GenericListFragment();
				Bundle tipo1 = new Bundle();
				tipo1.putString("tipo", "4");
				tipo1.putString("titolo", getString(R.string.a_list_1));
				fragment.setArguments(tipo1);
				break;
			case 2:
				fragment = new MapActivity();
				break;
			case 3:
				fragment = new SearchSchoolFragment();
				Bundle tipo3 = new Bundle();
				tipo3.putString("titolo", getString(R.string.c_list_1));
				fragment.setArguments(tipo3);
				break;
			case 5:
				fragment = new SchoolListFragment();
				Bundle tipo5 = new Bundle();
				tipo5.putString("tipo", "2");
				tipo5.putString("titolo", getString(R.string.a_list_2));
				fragment.setArguments(tipo5);
				break;
			case 6:
				fragment = new SchoolListFragment();
				Bundle tipo6 = new Bundle();
				tipo6.putString("tipo", "3");
				tipo6.putString("titolo", getString(R.string.b_list_2));
				fragment.setArguments(tipo6);
				break;
			case 8:
				fragment = new GenericListFragment();
				Bundle tipo8 = new Bundle();
				tipo8.putString("tipo", "1");
				tipo8.putString("titolo", getString(R.string.a_list_3));
				fragment.setArguments(tipo8);
				break;
			case 9:
				fragment = new GenericListFragment();
				Bundle tipo9 = new Bundle();
				tipo9.putString("tipo", "2");
				tipo9.putString("titolo", getString(R.string.b_list_3));
				fragment.setArguments(tipo9);
				break;
			case 10:
				fragment = new GenericListFragment();
				Bundle tipo10 = new Bundle();
				tipo10.putString("tipo", "3");
				tipo10.putString("titolo", getString(R.string.c_list_3));
				fragment.setArguments(tipo10);
				break;
			case 12:
				fragment = new NewsFragmentEndless();
				Bundle tipo12 = new Bundle();
				tipo12.putString("titolo", getString(R.string.a_list_4));
				fragment.setArguments(tipo12);
				break;
			case 13:
				fragment = new LMFragment();
				Bundle tipo13 = new Bundle();
				tipo13.putString("titolo", getString(R.string.b_list_4));
				fragment.setArguments(tipo13);
				break;
			case 14:
				fragment = new CreditsFragment();
				Bundle tipo14 = new Bundle();
				tipo14.putString("titolo", getString(R.string.c_list_4));
				fragment.setArguments(tipo14);
				break;
			
			}	
		}

		FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

		mDrawerList.setItemChecked(position, true);
		mDrawerLayout.closeDrawer(mDrawerList);
	}

	@SuppressLint("NewApi")
	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
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
