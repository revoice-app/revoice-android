package com.sar.user.smart_city;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdap extends RecyclerView.Adapter<ReviewAdap.ImageHolder> {

    private List<ModelReview> mFilesList;
    private Context context;
    private DeletePostImageListener deletePostImageListener;
    private AddImageListener addImageListener;
    private final int viewImage = 1;
    private final int viewVideo = 0;


    public ReviewAdap(Context context, List<ModelReview> mFilesList) {
        this.mFilesList = mFilesList;
        this.context = context;
    }


    @NonNull
    @Override
    public ImageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.image_grid_view_list, parent, false);
        return new ImageHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull ImageHolder holder, int position) {
        holder.reviewText.setText(mFilesList.get(position).getText());
        holder.reviewSent.setText(mFilesList.get(position).getTextSent());
        holder.reviewNum.setText("Review : "+String.valueOf(position+1));
        holder.date.setText("Date and Time : "+mFilesList.get(position).getDate());
        holder.location.setText("Location : "+mFilesList.get(position).getLocation());

        if (mFilesList.get(position).getImage().length() > 10) {

            holder.imageText.setText(mFilesList.get(position).getTextClass());
            holder.image.setText("Image Capture");
            holder.imageText.setText(mFilesList.get(position).getTextClass());
            byte[] decodedString = Base64.decode(mFilesList.get(position).getImage(), Base64.DEFAULT);
            holder.image.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
// ...Irrelevant code for customizing the buttons and title
                    LayoutInflater inflater = (LayoutInflater) context
                            .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View dialogView = inflater.inflate(R.layout.alert_label_editor, null);
                    dialogBuilder.setView(dialogView);

                    ImageView editText = (ImageView) dialogView.findViewById(R.id.goPro);
                    editText.setImageBitmap(BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length));
                    AlertDialog alertDialog = dialogBuilder.create();
                    alertDialog.show();
                }
            });
        } else {
            holder.image.setVisibility(View.GONE);
            holder.imageText.setVisibility(View.GONE);

        }
    }


    @Override
    public int getItemCount() {
        return mFilesList.size();
    }

    class ImageHolder extends RecyclerView.ViewHolder {
        private TextView reviewNum;
        private TextView reviewText;
        private TextView image;
        private TextView reviewSent;
        private TextView imageText;
        private TextView location;
        private TextView date;
        private Button imageButton;




        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            reviewNum = itemView.findViewById(R.id.review);
            reviewSent = itemView.findViewById(R.id.reviewSent);
            date = itemView.findViewById(R.id.date);
            location = itemView.findViewById(R.id.location);
            reviewText = itemView.findViewById(R.id.reviewText);
            image= itemView.findViewById(R.id.image);
            imageText=itemView.findViewById(R.id.imageText);
            reviewSent=itemView.findViewById(R.id.reviewSent);



        }


    }
}

