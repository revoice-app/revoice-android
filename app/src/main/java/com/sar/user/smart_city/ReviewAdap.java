package com.sar.user.smart_city;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.jaiselrahman.filepicker.model.MediaFile;


import java.util.List;
import java.util.zip.Inflater;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.RecyclerView;

public class ReviewAdap  extends RecyclerView.Adapter<ReviewAdap.ImageHolder> {

    private List<ModelReview> mFilesList;
    private Context context;
    private DeletePostImageListener deletePostImageListener;
    private AddImageListener addImageListener;
    private final int viewImage = 1;
    private final int viewVideo = 0;


    public ReviewAdap(Context context, List<ModelReview> mFilesList) {
        this.mFilesList = mFilesList;
        this.context=context;
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
        holder.textView.setText(mFilesList.get(position).getText());

        if(mFilesList.get(position).getImage().length()>10)
        {
            holder.textView.setText(mFilesList.get(position).getText());
            holder.textViewImage.setText(mFilesList.get(position).getTextClass());
            byte[] decodedString = Base64.decode(mFilesList.get(position).getImage(), Base64.DEFAULT);
            holder.imageButton.setOnClickListener(new View.OnClickListener() {
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
        }
        else
        {
            holder.textViewImage.setVisibility(View.GONE);
            holder.imageButton.setVisibility(View.GONE);
        }
    }



    @Override
    public int getItemCount() {
        return mFilesList.size() ;
    }

    class ImageHolder extends RecyclerView.ViewHolder {
          private TextView textView;
        private TextView textViewImage;
        private Button imageButton;


        public ImageHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.titleTV);
            textViewImage= itemView.findViewById(R.id.image);
            imageButton = itemView.findViewById(R.id.imageButton);

        }



    }
}

