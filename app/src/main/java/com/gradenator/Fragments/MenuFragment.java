package com.gradenator.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.gradenator.Internal.Constant;
import com.gradenator.MainActivity;
import com.gradenator.R;

public class MenuFragment extends ListFragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.list, container, false);
    }

    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        SidebarAdapter adapter = new SidebarAdapter(getActivity());
        // read from internal JSON file and create objects based on the file
        adapter.add(new SidebarItem(getString(R.string.sidebar_home), R.drawable.ic_action_home));
        adapter.add(new SidebarItem(getString(R.string.sidebar_search),
                R.drawable.ic_action_search));
        adapter.add(new SidebarItem(getString(R.string.sidebar_settings),
                R.drawable.ic_action_settings));
        setListAdapter(adapter);
    }

    private class SidebarItem {
        public String tag;
        public int iconRes;
        public SidebarItem(String tag, int iconRes) {
            this.tag = tag;
            this.iconRes = iconRes;
        }
    }

    public class SidebarAdapter extends ArrayAdapter<SidebarItem> {

        public SidebarAdapter(Context context) {
            super(context, 0);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row, null);
            }
            ImageView icon = (ImageView) convertView.findViewById(R.id.row_icon);
            icon.setImageResource(getItem(position).iconRes);
            TextView title = (TextView) convertView.findViewById(R.id.row_title);
            title.setText(getItem(position).tag);
            return convertView;
        }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (getActivity() instanceof MainActivity) {
            MainActivity m = (MainActivity) getActivity();
            switch (position) {
                case Constant.SIDEBAR_HOME: {
                    m.switchToHome();
                    break;
                }
                case Constant.SIDEBAR_SEARCH: {

                    break;
                }
                case Constant.SIDEBAR_SETTINGS: {

                    break;
                }
            }
        }
    }
}
