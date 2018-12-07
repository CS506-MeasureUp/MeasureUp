package com.example.aksha.measureup;

import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.aksha.db.models.Measurement;
import com.example.aksha.db.models.VideoObject;
import com.example.aksha.db.viewmodels.MeasurementViewModel;
import com.example.aksha.db.viewmodels.VideoObjectViewModel;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ObjectDetailsFragment extends Fragment {
    private VideoObjectViewModel videoObjectViewModel;
    private MeasurementViewModel measurementViewModel;

    public ObjectDetailsFragment() {

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState){
        final View rootView = inflater.inflate(R.layout.fragment_object_details, container, false);

        final TextView text = rootView.findViewById(R.id.name);
        final ImageView thumbnail = rootView.findViewById(R.id.thumbnail);
        final RecyclerView measurementsView = rootView.findViewById(R.id.measurementList);

        final MeasurementAdapter measurementAdapter = new MeasurementAdapter(getContext());

        measurementsView.setAdapter(measurementAdapter);
        measurementsView.setLayoutManager(new LinearLayoutManager(getContext()));

        videoObjectViewModel = ViewModelProviders.of(getActivity()).get(VideoObjectViewModel.class);
        measurementViewModel = ViewModelProviders.of(getActivity()).get(MeasurementViewModel.class);

        videoObjectViewModel.getCurrentVideoObject().observe(this, new Observer<VideoObject>() {
            @Override
            public void onChanged(final VideoObject videoObject) {
                text.setText(videoObject.getName());
                thumbnail.setImageBitmap(BitmapFactory.decodeFile(videoObject.getThumbnailPath()));

                measurementViewModel.getMeasurements(videoObject).observe(ObjectDetailsFragment.this, new Observer<List<Measurement>>() {
                    @Override
                    public void onChanged(List<Measurement> measurements) {
                        measurementAdapter.setMeasurements(measurements);
                    }
                });
            }
        });

        return rootView;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
}
