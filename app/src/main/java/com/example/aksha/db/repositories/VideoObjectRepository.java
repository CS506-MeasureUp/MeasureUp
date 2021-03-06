package com.example.aksha.db.repositories;

import android.app.Application;
import android.os.AsyncTask;

import com.example.aksha.db.AppDatabase;
import com.example.aksha.db.dao.VideoObjectDao;
import com.example.aksha.db.models.VideoObject;

import java.util.List;

import androidx.lifecycle.LiveData;

public class VideoObjectRepository {
    private VideoObjectDao videoObjectDao;
    private LiveData<List<VideoObject>> allVideoObjects;

    private static class VideoObjectAsyncTask extends AsyncTask<VideoObject, Void, Void> {
        VideoObjectDao videoObjectDao;

        VideoObjectAsyncTask(VideoObjectDao videoObjectDao) {
            this.videoObjectDao = videoObjectDao;
        }

        @Override
        protected Void doInBackground(VideoObject... videoObjects) {
            long id = videoObjectDao.insert(videoObjects[0]);
            videoObjects[0].setId((int) id);
            return null;
        }
    }



    public VideoObjectRepository(Application application) {

        AppDatabase db = AppDatabase.getAppDatabase(application);

        videoObjectDao = db.videoObjectDao();

        allVideoObjects = videoObjectDao.getAll();
    }

    public LiveData<List<VideoObject>> getAllVideoObjects() {
        return allVideoObjects;
    }

    public void insert(VideoObject videoObject) {
        new VideoObjectAsyncTask(videoObjectDao).execute(videoObject);
    }

    public void delete(final VideoObject videoObject) {

        Thread thread = new Thread(){
            public void run(){
                videoObjectDao.delete(videoObject);            }
        }; thread.start();

        allVideoObjects.getValue().remove(videoObject);


    }
}
