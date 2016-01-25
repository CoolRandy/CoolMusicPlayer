package com.coolrandy.com.coolmusicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coolrandy.com.coolmusicplayer.model.AlbumBean;
import com.coolrandy.com.coolmusicplayer.view.ImageRoundView;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by admin on 2016/1/18.
 */
public class AlbumAdapter extends RecyclerView.Adapter<AlbumAdapter.ViewHolder> {

    private final Context context;
    private final LayoutInflater layoutInflater;

    private List<AlbumBean> albumBeans;

    public AlbumAdapter(Context context, List<AlbumBean> albumBeans) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.albumBeans = albumBeans;
    }

    /**
     * 添加一个点击监听接口
     */
    public interface OnItemClickListener{
        void onItemClick(View view, int position);
    }

    private OnItemClickListener onItemClickListener;

    /**
     * 注册点击事件
     */
    public void setOnItemClickListener(OnItemClickListener onItemClickListener){

        this.onItemClickListener = onItemClickListener;
    }

    public void setAlbumList(List<AlbumBean> albumList){
        albumBeans = albumList;
        //可以采用一些新的数据更新的方法，会有一些动画
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        //绑定数据
        holder.albumName.setText(albumBeans.get(position).getName());
        holder.artistName.setText(albumBeans.get(position).getArtist_name());
        Picasso.with(context).load(albumBeans.get(position).getImage()).into(holder.pageImage);
//        Log.e("TAG", "albumLayout--->" + holder.albumLayout);
        //如果设置了回调，则设置点击事件
        if(onItemClickListener != null){
            holder.albumLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    int pos = holder.getLayoutPosition();
                    onItemClickListener.onItemClick(holder.albumLayout, pos);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return albumBeans.size();//这个会首先执行，如果为0，则不会继续向下执行
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(layoutInflater.inflate(R.layout.album_item_layout, parent, false));
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        @InjectView(R.id.album_name)
        public TextView albumName;
        @InjectView(R.id.artist_name)
        public TextView artistName;
        @InjectView(R.id.album_page)
        public ImageRoundView pageImage;
        @InjectView(R.id.album_item_layout)
        public LinearLayout albumLayout;

        public ViewHolder(View view){
            super(view);
            ButterKnife.inject(this, view);
            //此处类似于ListView中的item点击事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    Log.e("ViewHolder", "onClick--> position = " + getPosition());
//                    Log.e("TAG", "albumLayout--->" + albumLayout);
                }
            });
        }
    }
}
