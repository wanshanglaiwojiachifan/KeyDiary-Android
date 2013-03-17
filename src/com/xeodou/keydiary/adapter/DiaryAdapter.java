package com.xeodou.keydiary.adapter;

import java.util.Map;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class DiaryAdapter extends BaseAdapter {

    class ViewHolder {
        TextView day;
        TextView content;
    }
    
    private Map<String, Diary> diaries;
    private Context context;
    private int year, month;
    public DiaryAdapter(Context context, Map<String, Diary> diaries,int year, int month){
        this.context = context;
        this.diaries = diaries;
        this.year = year;
        this.month = month;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return Utils.getDayOfMonth(year, month);
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return diaries.get(year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position));
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
//        if(convertView == null){
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.diary_item_layout, null);
            viewHolder.day = (TextView)convertView.findViewById(R.id.day_diary_tv);
            viewHolder.content = (TextView)convertView.findViewById(R.id.content_diary_tv);
//            convertView.setTag(viewHolder);
//        } else {
//            viewHolder = (ViewHolder)convertView.getTag();
//        }
        if( year > Utils.getCurrentYear()){
            viewHolder.content.setEnabled(false);
        } else {
            if(month > Utils.getCurrentMonth()){
                viewHolder.content.setEnabled(false);
            } else {
                if(position + 1 > Utils.getCurrentDay()){
                    viewHolder.content.setEnabled(false);
                }
            }
        }
        viewHolder.day.setText(Utils.douInt(position+1));
        Diary diary = diaries.get(year+"-"+Utils.douInt(month)+"-" + Utils.douInt(position));
        if(diary != null){
            viewHolder.content.setText(diary.getContent());
        }
        return convertView;
    }

}
