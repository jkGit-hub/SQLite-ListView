package com.jkapps.sqlitelistview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class ListAdapter extends BaseAdapter implements Filterable {

    private Context context;
    private int layout;
    private ArrayList<Model> list;

    ArrayList<Model> mValue;
    ValueFilter valueFilter;

    public ListAdapter(Context context, int layout, ArrayList<Model> list) {
        this.context = context;
        this.layout = layout;
        this.list = list;

        mValue = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView tv_title, tv_content;
        ImageView iv_photo;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View row = convertView;
        ViewHolder holder = new ViewHolder();

        if (row == null) {
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            row = inflater.inflate(layout, null);
            holder.tv_title = row.findViewById(R.id.tv_title);
            holder.tv_content = row.findViewById(R.id.tv_content);
            holder.iv_photo = row.findViewById(R.id.iv_photo);
            row.setTag(holder);
        } else {
            holder = (ViewHolder)row.getTag();
        }

        Model model = list.get(position);

        holder.tv_title.setText(model.getTitle());
        holder.tv_content.setText(model.getContent());

        byte[] image = model.getImage();
        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
        holder.iv_photo.setImageBitmap(bitmap);

        return row;
    }

    @Override
        public Filter getFilter() {
            if (valueFilter == null) {
                valueFilter = new ValueFilter();
            }
            return valueFilter;
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
           FilterResults results = new FilterResults();

            if (constraint != null || constraint.length() > 0) {
                ArrayList<Model> filterList = new ArrayList<>();
                for (int i = 0; i < mValue.size(); i++) {
                    if ((mValue.get(i).getTitle().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                        Model model = new Model(mValue.get(i).getId(), mValue.get(i).getTitle(),
                                mValue.get(i).getContent(), mValue.get(i).getImage());
                        filterList.add(model);
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            list = (ArrayList<Model>) results.values;
            notifyDataSetChanged();
        }
    };
}
