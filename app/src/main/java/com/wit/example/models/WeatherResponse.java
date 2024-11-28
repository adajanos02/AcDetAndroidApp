package com.wit.example.models;

import java.util.List;

public class WeatherResponse {
    public List<Timeline> timelines;

    public static class Timeline {
        public List<Interval> intervals;

        public static class Interval {
            public String startTime;
            public Values values;

            public static class Values {
                public double temperature;
                public double precipitationProbability;
                public double windSpeed;
            }
        }
    }
}
