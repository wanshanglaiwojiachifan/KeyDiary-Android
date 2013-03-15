package com.xeodou.keydiary.adapter;

import java.util.List;
import java.util.Map;
import org.holoeverywhere.LayoutInflater;
import org.holoeverywhere.widget.GridView;
import com.xeodou.keydiary.R;
import com.xeodou.keydiary.bean.Diary;
import com.xeodou.keydiary.bean.DiaryTime;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;

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
        View view = LayoutInflater.inflate(container.getContext(), R.layout.diaryitem_layout);
        GridView grid = (GridView)view.findViewById(R.id.diary_grid);
        diaryAdapter = new DiaryAdapter(container.getContext(), diaries, data.get(position).getYear(), data.get(position).getMonth());
        grid.setAdapter(diaryAdapter);
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
