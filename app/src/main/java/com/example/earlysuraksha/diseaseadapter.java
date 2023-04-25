package com.example.earlysuraksha;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.earlysuraksha.fragments.DemandsFragment;

import java.util.ArrayList;

public class diseaseadapter extends RecyclerView.Adapter<diseaseadapter.ViewHolder> {
    private final ArrayList<String> diseasename;
    private final ArrayList<Integer> diseaseimage;


    public diseaseadapter(ArrayList<String> dataSet, ArrayList<Integer> diseaseimages) {
        diseasename = dataSet;
        diseaseimage = diseaseimages;

    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.medicaldetailscardview, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextView().setText(diseasename.get(position));
        viewHolder.getImageView().setImageResource(diseaseimage.get(position));
    }

    @Override
    public int getItemCount() {
        return diseasename.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final TextView diseasenameT;
        private final ImageView diseaseimageI;

        public ViewHolder(View view) {
            super(view);
            diseasenameT = (TextView) view.findViewById(R.id.diseasename);
            diseaseimageI = (ImageView) view.findViewById((R.id.diseasepicture));
            view.setOnClickListener(this);
        }

        public TextView getTextView() {
            return diseasenameT;
        }

        public ImageView getImageView() {
            return diseaseimageI;
        }

        @Override
        public void onClick(View v) {
            int index = getAdapterPosition();

            Log.i("Itembeforeinserting", String.valueOf(DemandsFragment.getselecteddiseasename().size()));
            DemandsFragment.getselecteddiseaseimage().add(diseaseimage.get(index));
            DemandsFragment.getselecteddiseasename().add(diseasename.get(index));
            DemandsFragment.updateadapter();

            Log.i("Itemafterinserting", String.valueOf(DemandsFragment.getselecteddiseasename().size()));
            diseasename.remove(index);
            diseaseimage.remove(index);
            notifyItemRemoved(index);
            DemandsFragment.selected.setText("Selected:(" + DemandsFragment.getselecteddiseaseimage().size() + ")");
        }
    }

}

