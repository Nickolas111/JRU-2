package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class Collecting {

public int sum(IntStream intStream) { return intStream.reduce(0,(x, y)->x+y); }



public int production(IntStream limit) {


    return limit.reduce(1,(x,y)->x*y);
}


public void oddSum(IntStream limit) {

           return limit.filter(x -> x % 2; !

    }

    public void inverse() {

    }

    public Map<Collection<k>, v> inverse(Map<? extends k, ? extends v> map){
            HashMap resultMap = new HashMap();

            Set<k> keys = map.keySet();
                 for (k key : keys){
                v value;
                value = map.get(keys);
                resultMap.compute(value, (v, ks) -> {
                    if (ks == null) {
                        ks = new HashSet();
                    }
                    ks.add(key);
                    return ks;
                });
            }
            return resultMap;
    }


    public <k, v> Map<k, v> totalScores(Stream<CourseResult>programmingResults) { return null;}



public double averageTotalScore(Stream<CourseResult>programmingResults) {
    //System.out.println(programmingResults.collect(Collectors.averagingDouble(x->
    //x.getTaskResults().entrySet().stream().collect(Collectors.averagingDouble(y->y.getValue))))));
    return programmingResults.collect(Collectors.averagingDouble(x ->
            x.getTaskResults().entrySet().stream().collect(Collectors.averagingDouble(y -> y.getValue()))));
}

public Map<String, Double>averageScoresPerTask(Stream<CourseResult>programmingResults) { return null; }



public <k, v> Map <k, v> defineMarks(Stream<CourseResult>programmingResults) { return null;}
private long getNumOfTasks (List<CourseResult>crList) {
    return
            crList.stream()
                    .flatMap(cr ->cr.getTaskResults().keySet().stream())
                    .distinct().count();
            }
        }
