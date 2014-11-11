package it.italianlanguageschool;

import it.italianlanguageschool.classes.GenericItem;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class GenericListAdapter extends ArrayAdapter<GenericItem> {
	private Context activity;
	private ArrayList<GenericItem> data;

	public GenericListAdapter(Context a, ArrayList<GenericItem> d) {
		super(a, R.layout.list_item_generic, d);
		activity = a;
		data = d;
	}
	
	static class ViewHolder {
		public TextView id;
		public TextView title;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_generic, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) vi.findViewById(R.id.item_id);
			viewHolder.title = (TextView) vi.findViewById(R.id.item_title);
			vi.setTag(viewHolder);		
		}
		
		ViewHolder holder = (ViewHolder) vi.getTag();
		final GenericItem item = data.get(position);
		holder.id.setText(item.getId());
		holder.title.setText(item.getTitle());

		return vi;
	}
}
