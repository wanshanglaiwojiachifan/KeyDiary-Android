package com.xeodou.keydiary.adapter;

import java.util.List;
import java.util.Map;

import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.DiaryTime;
import com.xeodou.keydiary.views.EditDialog;

import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class DiaryFragementAdapter extends PagerAdapter{

    private Map<String, Diary> diaries;
    private List<DiaryTime> data;
    private DiaryAdapter diaryAdapter;
    public DiaryFragementAdapter(Map<String, Diary> diaries, List<DiaryTime> data){
        this.diaries = diaries;
        this.data = data;
    }
    
    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return data.size();
    }

    @Override
    public View instantiateItem(final ViewGroup container,final int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.diaryitem_layout, null);
        ListView grid = (ListView)view.findViewById(R.id.diary_grid);
        grid.setCacheColorHint(Color.TRANSPARENT);
//        grid.setAlwaysDrawnWithCacheEnabled(true);
        View v = new View(container.getContext());
        v.setMinimumHeight((int)container.getContext().getResources().getDimension(R.dimen.footerHeight));
        grid.addFooterView(v);
        v = new View(container.getContext());
        v.setMinimumHeight((int)container.getContext().getResources().getDimension(R.dimen.headerHeight));
        grid.addHeaderView(v);
        TextView title = (TextView)view.findViewById(R.id.title);
        diaryAdapter = new DiaryAdapter(container.getContext(), grid, diaries, data.get(position).getYear(), data.get(position).getMonth());
        grid.setAdapter(diaryAdapter);
        ScaleInAnimationAdapter sia = new ScaleInAnimationAdapter(diaryAdapter, 0.5f, 100, 500);
        sia.setListView(grid);
        grid.setAdapter(sia);
        title.setText(data.get(position).toTitle());
        title.setTypeface(Utils.getTypeface());
        if(data.get(position).getMonth() == Utils.getCurrentMonth()){
            grid.setSelection(Utils.getCurrentDay() - 1);
        }
        container.addView(view, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
      container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        // TODO Auto-generated method stub
        return view == object;
    }
    
    @Override
    public CharSequence getPageTitle(int position) {
      return data.get(position).toTitle();
    }
}
