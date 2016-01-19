package com.weijie.firerunning.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.weijie.firerunning.R;
import com.weijie.firerunning.bean.MenuItem;

public class MenuAdapter extends BaseAdapter {

	private List<MenuItem> menus;
	private LayoutInflater inflater;
	private Context context;
	
	public MenuAdapter(List<MenuItem> menus, Context context) {
		this.menus = menus;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
	}

	@Override
	public int getCount() {
		return menus.size();
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		MenuItem menu = menus.get(position);
		ViewHolder holder;
		if(convertView==null) {
			convertView = inflater.inflate(R.layout.item_left_menu, parent, false);
			holder = new ViewHolder();
			holder.icon = (ImageView) convertView.findViewById(R.id.icon);
			holder.label = (TextView) convertView.findViewById(R.id.label);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) (convertView.getTag());
		}
		holder.icon.setImageDrawable(context.getResources().getDrawable(menu.icon));
		holder.label.setText(menu.label);
		return convertView;
	}
	
	class ViewHolder {
		ImageView icon;
		TextView label;
	}

}
