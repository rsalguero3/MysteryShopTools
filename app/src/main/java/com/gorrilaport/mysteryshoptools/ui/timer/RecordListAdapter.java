package com.gorrilaport.mysteryshoptools.ui.timer;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.gorrilaport.mysteryshoptools.R;

import java.util.LinkedList;
import java.util.List;

public class RecordListAdapter extends BaseAdapter {

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    // refer to the list view
    private ListView listView;

    private List<Record> records = new LinkedList<Record>();
    // record how many items inserted.
    private int recordNum;
    // record how many place holders are in records.
    private int holderNum;
    private final int PLACE_HOLDER_NUM = 9;

    RecordListAdapter(ListView listView) {
        super();
        this.listView = listView;
        reset();
    }

    public void reset() {
        recordNum = 0;
        // we leave one place holder when the list is full of items for visual reasons
        holderNum = PLACE_HOLDER_NUM - 1;
        // clear records
        records.clear();
        // set place holder items
        for (int i = 0; i < PLACE_HOLDER_NUM; i++) {
            records.add(new Record());
        }
        notifyDataSetInvalidated();
    }

    @Override
    public int getCount() {
        return records.size();
    }

    @Override
    public Object getItem(int position) {
        return records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            view = inflater.inflate(R.layout.timer_list, null);
            Button btn = (Button) view.findViewById(R.id.delta);
            btn.setFocusable(false);
            btn.setClickable(false);
        } else {
            view = convertView;
        }
        Record record = records.get(position);
        int order = record.order;
        View layout = view.findViewById(R.id.record_layout);
        if (order == -1) {
            // this is just a "place holder" record.
            layout.setVisibility(View.INVISIBLE);
        } else {
            layout.setVisibility(View.VISIBLE);
            // set text
            TextView orderTextView = (TextView) view.findViewById(R.id.lap_order);
            orderTextView.setText(String.valueOf(order));
            TextView timeText = (TextView) view.findViewById(R.id.time_text);
            Period period = record.period;
            timeText.setText(String.format("%02d:%02d.%01d", period.minutes, period.seconds, period.tenOfSec));
            Button deltaBtn = (Button) view.findViewById(R.id.delta);
            // calculate the delta
            // there is no previous record or not
            if (order == 1) {
                deltaBtn.setText(String.format("+%02d:%02d.%01d", period.minutes, period.seconds, period.tenOfSec));
            } else {
                // get the previous period
                Period prePeriod = records.get(position + 1).period;
                // calculate the delta in ten of a second
                int deltaUnit = (period.minutes * 600 + period.seconds * 10 + period.tenOfSec)
                        - (prePeriod.minutes * 600 + prePeriod.seconds * 10 + prePeriod.tenOfSec);
                int deltaMin = deltaUnit / 600;
                int deltaSec = deltaUnit / 10 - deltaMin * 60;
                int deltaTS = deltaUnit % 10;
                deltaBtn.setText(String.format("+%02d:%02d.%01d", deltaMin, deltaSec, deltaTS));
            }
        }
        return view;
    }

    // add new record to records.
    public void addRecord(Period period) {
        records.add(0, new Record(++recordNum, period));
//        if (holderNum > 0) {
//            records.remove(records.size() - 1);
//            --holderNum;
//        }
        notifyDataSetInvalidated();
        // set scroll to previous record
        listView.setSelection(1);
        // TODO: when upLayout minimized, there will be a feeling of "sudden sliding".
        // scroll to the latest record
        listView.post(new Runnable() {
            @Override
            public void run() {
                listView.smoothScrollToPositionFromTop(0, 0, 500);
            }
        });
        if (holderNum > 0) {
            listView.post(new Runnable() {
                @Override
                public void run() {
                    records.remove(records.size() - 1);
                }
            });
            --holderNum;
        }
    }
}