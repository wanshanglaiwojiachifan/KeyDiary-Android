package com.xeodou.keydiary.adapter;

import java.util.List;
import java.util.Map;

import com.haarman.listviewanimations.swinginadapters.prepared.ScaleInAnimationAdapter;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.Utils;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.DiaryTime;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
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
    public View instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(container.getContext()).inflate(R.layout.diaryitem_layout, null);
        ListView grid = (ListView)view.findViewById(R.id.diary_grid);
        View v = new View(container.getContext());
        v.setMinimumHeight((int)container.getContext().getResources().getDimension(R.dimen.footerHeight));
        grid.addFooterView(v);
        TextView title = (TextView)view.findViewById(R.id.title);
        diaryAdapter = new DiaryAdapter(container.getContext(), diaries, data.get(position).getYear(), data.get(position).getMonth());
        grid.setAdapter(diaryAdapter);
        ScaleInAnimationAdapter sia = new ScaleInAnimationAdapter(diaryAdapter, 0.5f, 100, 500);
        sia.setListView(grid);
        grid.setAdapter(sia);
        title.setText(data.get(position).toTitle());
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
