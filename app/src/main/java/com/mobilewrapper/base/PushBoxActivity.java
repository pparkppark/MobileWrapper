package com.mobilewrapper.base;

import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.mobilewrapper.base.common.BaseActivity;
import com.mobilewrapper.base.gcm.beans.LocalDBBeans;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;

/**
 * Created by ppark on 2015-08-20.
 */
public class PushBoxActivity extends BaseActivity {

    @Bind(R.id.pushBox_back)
    Button back;
    @Bind(R.id.pushBox_listview)
    ListView listview;

    RealmResults<LocalDBBeans> result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_pushbox);
        ButterKnife.bind(this);

        Realm realm = Realm.getInstance(this);
        RealmQuery<LocalDBBeans> query = realm.where(LocalDBBeans.class);
        result = query.findAll();
        result.sort("id", RealmResults.SORT_ORDER_DESCENDING);

        listview.setAdapter(new PushBoxAdapter());

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                LocalDBBeans bean = result.get(position);

                getIntent().putExtra("pushUrl", bean.getLinkUrl());
                setResult(RESULT_OK, getIntent());
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        onBackClick();
    }

    @OnClick(R.id.pushBox_back)
    void onBackClick() {
        finishActivity();
    }

    class PushBoxAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return result.size();
        }

        @Override
        public Object getItem(int position) {
            return result.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            PushBoxViewHolder holder = null;
            if (convertView != null) {
                holder = (PushBoxViewHolder) convertView.getTag();
            } else {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pushbox, null);
            }
            holder = new PushBoxViewHolder(convertView);
            convertView.setTag(holder);

            LocalDBBeans bean = result.get(position);

            holder.title.setText(bean.getId() + " : " + bean.getTitle());
            holder.description.setText(Html.fromHtml(bean.getDescription()));
            holder.otherInfo.setText("thumb : " + bean.getThumb() + "\n"
                    + "linkUrl : " + bean.getLinkUrl() + "\n"
                    + "date : " + bean.getDate());


            return convertView;
        }

        /**
         * This class contains all butterknife-injected Views & Layouts from layout file 'item_pushbox.xml'
         * for easy to all layout elements.
         *
         * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
         */
        class PushBoxViewHolder {
            @Bind(R.id.item_pushbox_title)
            TextView title;
            @Bind(R.id.item_pushbox_description)
            TextView description;
            @Bind(R.id.item_pushbox_otherInfo)
            TextView otherInfo;

            PushBoxViewHolder(View view) {
                ButterKnife.bind(this, view);
            }
        }
    }
}
