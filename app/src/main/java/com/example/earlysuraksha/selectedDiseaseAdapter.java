package com.example.earlysuraksha;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.earlysuraksha.fragments.DemandsFragment;

import java.util.ArrayList;


public class selectedDiseaseAdapter extends RecyclerView.Adapter<selectedDiseaseAdapter.ViewHolder> {


    public static ArrayList<Integer> selecteddiseaseimage;
    private static ArrayList<String> selecteddiseasename;

    public selectedDiseaseAdapter(ArrayList<String> dataSet, ArrayList<Integer> selecteddiseaseimages) {
        selecteddiseasename = dataSet;
        selecteddiseaseimage = selecteddiseaseimages;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.selecteddisease, viewGroup, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        viewHolder.selecteddiseasenameT.setText(selecteddiseasename.get(position));
        viewHolder.selecteddiseaseimageI.setImageResource(selecteddiseaseimage.get(position));

    }

    @Override
    public int getItemCount() {
        return selecteddiseasename.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView selecteddiseasenameT;
        ImageView selecteddiseaseimageI;
        ImageView close;

        public ViewHolder(View view) {
            super(view);
            selecteddiseasenameT = (TextView) view.findViewById(R.id.selecteddiseasename);
            selecteddiseaseimageI = (ImageView) view.findViewById((R.id.selecteddiseasepicture));
            close = (ImageView) view.findViewById((R.id.close));
            close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    DemandsFragment.getdiseasename().add(selecteddiseasename.get(position));
                    DemandsFragment.getdiseaseimage().add(selecteddiseaseimage.get(position));
                    DemandsFragment.ad.notifyDataSetChanged();
                    selecteddiseaseimage.remove(position);
                    selecteddiseasename.remove(position);
                    notifyItemRemoved(position);
                    DemandsFragment.selected.setText("Selected:(" + selecteddiseaseimage.size() + ")");
                }
            });
        }

        public TextView getTextView() {
            return selecteddiseasenameT;
        }

        public ImageView getImageView() {
            return selecteddiseaseimageI;
        }
    }


}
