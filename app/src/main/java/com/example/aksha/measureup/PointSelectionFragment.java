package com.example.aksha.measureup;


import android.app.AlertDialog;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.os.Environment;
import android.util.Log;
import android.util.SizeF;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.ImageView;

import com.google.common.io.LineReader;

import org.opencv.core.Point;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.util.ArrayList;

public class PointSelectionFragment extends Fragment {
    private PointSelectorView point1;
    private PointSelectorView point2;
    private Button measureButton;
    private ImageView imageView;

    private File img = null;
    private VideoProcessor vp = null;

    double refDistance = 0;

    public PointSelectionFragment() {
        // Required empty public constructor
        init();
    }

    private void init() {
        File videoDirectory = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES) + "/MeasureUp");
        File[] fileList = videoDirectory.listFiles();

        if (fileList.length == 0) return;

        File dir = fileList[fileList.length - 1];
        File text = null;
        File video = null;

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".mp4")) {
                video = file;
            } else if (file.getName().endsWith(".txt")) {
                text = file;
            }
        }

        if (video == null || text == null) return;

        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(text))) {
            refDistance = Double.parseDouble(bufferedReader.readLine()) / 100;

            vp = new VideoProcessor(video);
            vp.grabFrames();

            for (File file : dir.listFiles()) {
                if (file.getName().endsWith("0.jpg")) {
                    img = file;
                }
            }
        } catch (Exception ex) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_point_selection, container, false);

        imageView = view.findViewById(R.id.imageView4);
        imageView.setImageBitmap(BitmapFactory.decodeFile(img.getAbsolutePath()));

        point1 = view.findViewById(R.id.pointSelectorView);
        point2 = view.findViewById(R.id.pointSelectorView2);
        measureButton = view.findViewById(R.id.button12);

        measureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickProcessor();

                // Navigation.findNavController(PointSelectionFragment.this.getView()).navigateUp();
            }
        });

        view.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                View view = PointSelectionFragment.this.getView();

                point1.setX(view.getWidth() / 4 - point1.getWidth() / 2);
                point1.setY(view.getHeight() / 2 - point1.getHeight() / 2);

                point2.setX(view.getWidth() * 3 / 4 - point1.getWidth() / 2);
                point2.setY(view.getHeight() / 2 - point1.getHeight() / 2);

                point1.invalidate();
                point2.invalidate();

                view.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        return view;
    }

    public ArrayList<Point> getMeasurePoints() {
        ArrayList<Point> points = new ArrayList<>();

        points.add(new Point(point1.getX(), point1.getY()));
        points.add(new Point(point2.getX(), point2.getY()));

        return points;
    }

    private SizeF getCameraResolution(int camNum)
    {
        SizeF size = new SizeF(0,0);
        CameraManager manager = (CameraManager) this.getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length > camNum) {
                CameraCharacteristics character = manager.getCameraCharacteristics(cameraIds[camNum]);
                size = character.get(CameraCharacteristics.SENSOR_INFO_PHYSICAL_SIZE);
            }
        }
        catch (CameraAccessException e)
        {
            Log.e("YourLogString", e.getMessage(), e);
        }
        return size;
    }

    private float getFocalLength(int camNum) {
        CameraManager manager = (CameraManager) this.getActivity().getSystemService(Context.CAMERA_SERVICE);
        try {
            String[] cameraIds = manager.getCameraIdList();
            if (cameraIds.length > camNum) {
                CameraCharacteristics character = manager.getCameraCharacteristics(cameraIds[camNum]);
                return character.get(CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS)[camNum];
            }
        }
        catch (CameraAccessException e)
        {
            Log.e("YourLogString", e.getMessage(), e);
        }

        return 0;
    }

    public void onClickProcessor() {
        vp.trackOpticalFlow();
        ArrayList<Point> iniPoints = getMeasurePoints();
        ArrayList<Point> finalPoints = vp.getFinalPoints();
        SizeF sizeF = getCameraResolution(0);
        double oFM = getFocalLength(0) / 1000;
        double ccdH = getCameraResolution(0).getHeight() / 1000;
        double results = vp.measurement(oFM, ccdH, refDistance, iniPoints, finalPoints);

        AlertDialog.Builder builder = new AlertDialog.Builder(this.getActivity());

        builder.setTitle("Results");
        builder.setMessage(results + " m");
        builder.setCancelable(true);

        builder.create().show();
        Log.d("results: ", String.valueOf(results));
    }
}