package club.contaniif.contaniff.videos;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeThumbnailView;

import club.contaniif.contaniff.R;

/**
 * Created by sonu on 10/11/17.
 */

public class YoutubeViewHolder extends RecyclerView.ViewHolder{
    public final YouTubeThumbnailView videoThumbnailImageView;
    public final TextView tituloVideo;
    public final TextView descripcionVideo;

    public YoutubeViewHolder(View itemView) {
        super(itemView);
        videoThumbnailImageView = itemView.findViewById(R.id.video_thumbnail_image_view);
        LinearLayout youtubeCardView = itemView.findViewById(R.id.youtube_row_card_view);
        tituloVideo=itemView.findViewById(R.id.tituloVideo);
        descripcionVideo=itemView.findViewById(R.id.descVideo);
    }
}
