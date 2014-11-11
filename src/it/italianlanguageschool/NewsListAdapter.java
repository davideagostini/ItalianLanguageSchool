package it.italianlanguageschool;

import it.italianlanguageschool.classes.NewsItem;
import it.italianlanguageschool.utility.ImageLoader;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NewsListAdapter extends ArrayAdapter<NewsItem> {
	private Context activity;
	private ArrayList<NewsItem> data;
	public ImageLoader imageLoader;

	public NewsListAdapter(Context a, ArrayList<NewsItem> d) {
		super(a, R.layout.list_item_news, d);
		activity = a;
		data = d;
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}
	
	static class ViewHolder {
		public TextView url;
		public TextView titolo; 
		public TextView date;
		public TextView descrizione; 
		public TextView scuola; 
		public ImageView thumb_image;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			vi = inflater.inflate(R.layout.list_item_news, null);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.url = (TextView) vi.findViewById(R.id.news_url);
			viewHolder.titolo = (TextView) vi.findViewById(R.id.news_title);
			viewHolder.date = (TextView) vi.findViewById(R.id.news_date);
			viewHolder.descrizione = (TextView) vi.findViewById(R.id.news_desc);
			viewHolder.scuola = (TextView) vi.findViewById(R.id.titolo_scuola);
			viewHolder.thumb_image = (ImageView) vi.findViewById(R.id.news_thumb_image);
			viewHolder.thumb_image.setMaxHeight(90);
			viewHolder.thumb_image.setMaxWidth(90);
			vi.setTag(viewHolder);
		}
	
		ViewHolder holder = (ViewHolder) vi.getTag();
		final NewsItem item = data.get(position);

		holder.url.setText(item.getUrl());
		holder.titolo.setText(item.getTitolo());
		holder.date.setText(item.getDate());
		holder.descrizione.setText(item.getDescrizione());
		holder.scuola.setText(item.getScuola());
		imageLoader.DisplayImage(item.getThumb_image(), holder.thumb_image);

		return vi;
	}
}

