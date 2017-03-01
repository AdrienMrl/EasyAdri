package com.easy.adri.Patterns;

import java.util.ArrayList;
import java.util.List;

public class Observer {

    public interface Observing {
        public void onNotification(Object obj);
    }

    public static class Observed {

        private List<Observing> observings = new ArrayList<>();

        public void registerObserving(Observing object) {
            observings.add(object);
        }

        public void unregister(Observing object) {
            observings.remove(object);
        }

        public void sendNotification(Object obj) {
            for (Observing observing: observings)
                observing.onNotification(obj);
        }
    }
}
