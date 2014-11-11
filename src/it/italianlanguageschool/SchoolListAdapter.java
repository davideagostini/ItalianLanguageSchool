package it.italianlanguageschool;

import it.italianlanguageschool.classes.SchoolItem;
import it.italianlanguageschool.utility.ImageLoader;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SchoolListAdapter extends ArrayAdapter<SchoolItem> {
	private Context activity;
	private ArrayList<SchoolItem> data;
	public ImageLoader imageLoader;

	public SchoolListAdapter(Context a, ArrayList<SchoolItem> d) {
		super(a, R.layout.list_item_school, d);
		activity = a;
		data = d;
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}
	
	static class ViewHolder {
		public TextView id;
		public TextView titolo;
		public TextView indirizzo;
		public TextView descrizione;
		public ImageView thumb_image; 
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_school, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.id = (TextView) vi.findViewById(R.id.school_id);
			viewHolder.titolo = (TextView) vi.findViewById(R.id.school_title);
			viewHolder.indirizzo = (TextView) vi.findViewById(R.id.school_indirizzo);
			viewHolder.descrizione = (TextView) vi.findViewById(R.id.school_desc);
			viewHolder.thumb_image = (ImageView) vi.findViewById(R.id.school_thumb_image);
			viewHolder.thumb_image.setMaxHeight(90);
			viewHolder.thumb_image.setMaxWidth(90);
			vi.setTag(viewHolder);
		}
		
		ViewHolder holder = (ViewHolder) vi.getTag();
		final SchoolItem item = data.get(position);
		
		holder.id.setText(item.getId());
		holder.titolo.setText(item.getTitolo());
		holder.indirizzo.setText(item.getIndirizzo());
		holder.descrizione.setText(item.getDescrizione());
		imageLoader.DisplayImage(item.getThumbnail(),holder.thumb_image);

		return vi;
	}

}

