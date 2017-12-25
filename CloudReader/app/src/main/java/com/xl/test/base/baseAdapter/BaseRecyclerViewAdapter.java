package com.xl.test.base.baseAdapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hushendian on 2017/10/25.
 */

public abstract class BaseRecyclerViewAdapter<T> extends RecyclerView.Adapter<BaseRecyclerViewHolder> {

    private List<T> data = new ArrayList<>();
    protected OnItemClickListener<T> listener;
    protected OnItemLongClickListener<T> onItemLongClickListener;


    @Override
    public void onBindViewHolder(BaseRecyclerViewHolder holder, int position) {
        holder.onBaseBindViewHolder(data.get(position), position);
    }

    @Override
    public int getItemCount() {
        Log.d("BaseRecyclerViewAdapter", "getItemCount: "+data.size());
        return data.size();
    }

    public void addAll(List<T> data) {
        this.data.addAll(data);

    }

    public void add(T t) {
        this.data.add(t);
    }

    public void clear() {
        this.data.clear();
    }

    public void remove(T t) {
        this.data.remove(t);
    }

    public void remove(int position) {
        this.data.remove(position);
    }

    public void removeAll(List<T> ts) {
        this.data.removeAll(ts);
    }

    public void setOnItemClickListener(OnItemClickListener<T> listener) {
        this.listener = listener;
    }


    public List<T> getData() {
        return data;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener<T> onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}
