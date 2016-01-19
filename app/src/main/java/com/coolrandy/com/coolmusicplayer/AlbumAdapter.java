package com.coolrandy.com.coolmusicplayer;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.coolrandy.com.coolmusicplayer.model.AlbumTrack;
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

    private List<AlbumTrack> albumTracks;

    public AlbumAdapter(Context context, List<AlbumTrack> albumTracks) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
        this.albumTracks = albumTracks;
    }

    public void setAlbumList(List<AlbumTrack> albumList){
        albumTracks = albumList;
        //可以采用一些新的数据更新的方法，会有一些动画
        notifyDataSetChanged();
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        //绑定数据
        holder.albumName.setText(albumTracks.get(position).getName());
        holder.artistName.setText(albumTracks.get(position).getArtist_name());
        Picasso.with(context).load(albumTracks.get(position).getImage()).into(holder.pageImage);

    }

    @Override
    public int getItemCount() {
        return albumTracks.size();//这个会首先执行，如果为0，则不会继续向下执行
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

        public ViewHolder(View view){
            super(view);
            ButterKnife.inject(this, view);
            //此处类似于ListView中的item点击事件
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.d("ViewHolder", "onClick--> position = " + getPosition());
                }
            });
        }
    }
}
