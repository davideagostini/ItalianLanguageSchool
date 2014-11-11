package it.italianlanguageschool;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CreditsFragment extends Fragment {

	private LinearLayout ll;
	private String titolo;
	private TextView title;

	@SuppressWarnings("unused")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		Bundle bundle = this.getArguments();
		if(getArguments()!=null) {
			titolo = getArguments().getString("titolo");
		}		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		ll = (LinearLayout)inflater.inflate(R.layout.credits, container, false);
		title = (TextView) ll.findViewById(R.id.title_list);
		title.setText(titolo);
		return ll;
	}
	

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.action_refresh).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }

}
