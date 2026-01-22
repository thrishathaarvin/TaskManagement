package org.example.service;
import org.example.domain.activity.ActivityEvent;
import org.example.storage.InMemoryDatabase;

public class ActivityService {

    public void record(ActivityEvent event){
        InMemoryDatabase.activities.add(event);
    }
}
