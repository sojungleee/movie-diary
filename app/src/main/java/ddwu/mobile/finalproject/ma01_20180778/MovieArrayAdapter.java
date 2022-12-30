package ddwu.mobile.finalproject.ma01_20180778;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import ddwu.mobile.finalproject.ma01_20180778.model.json.Movie;

public class MovieArrayAdapter extends BaseAdapter {

    private Context context;
    private int layout;
    private ArrayList<Movie> movieList;
    private LayoutInflater layoutInflater;

    public MovieArrayAdapter(Context context, int layout, ArrayList<Movie> movieList) {
        this.context = context;
        this.layout = layout;
        this.movieList = movieList;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        return movieList.size();
    }

    @Override
    public Object getItem(int pos) {
        return movieList.get(pos);
    }

    @Override
    public long getItemId(int pos) {
        return pos; // how?
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;

        final int position = pos;
        Movie movie = movieList.get(position);

        if (convertView == null) {
            convertView = layoutInflater.inflate(layout, viewGroup, false);

            holder = new ViewHolder();
            holder.tvTitle = convertView.findViewById(R.id.tvTitle);
            holder.tvPubDate = convertView.findViewById(R.id.tvPubDate);
            holder.tvUserRating = convertView.findViewById(R.id.tvUserRating);
            holder.tvDirector = convertView.findViewById(R.id.tvDirector);
            holder.tvActor = convertView.findViewById(R.id.tvActor);
            holder.imageView = convertView.findViewById(R.id.imageView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

//        html 태그 해석
        String result = String.valueOf(Html.fromHtml(movie.getTitle()));
        holder.tvTitle.setText(result);
        holder.tvPubDate.setText(movie.getPubDate());
        holder.tvUserRating.setText(movie.getUserRating());
        holder.tvDirector.setText(movie.getDirector());
        holder.tvActor.setText(movie.getActor());
        // 이미지뷰 글라이드
        Glide.with(holder.imageView.getContext())
                .load(movie.getImage())
                .into(holder.imageView);


        return convertView;
    }

    public static class ViewHolder {
        TextView tvTitle;
        TextView tvPubDate;
        TextView tvUserRating;
        TextView tvDirector;
        TextView tvActor;
        ImageView imageView;
    }

    public void addAll(List<Movie> movies) {
        movieList.addAll(movies);
    }

    public void addMovie(Movie movie) {
        movieList.add(movie);
    }

    public void clear() {
        movieList.clear();
    }
}
