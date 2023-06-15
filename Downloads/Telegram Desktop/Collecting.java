package com.efimchick.ifmo;

import com.efimchick.ifmo.util.CourseResult;
import com.efimchick.ifmo.util.Person;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;



public class Collecting {
    public int sum(IntStream limit) {
        return limit.sum();
    }

    public int production(IntStream limit) {
        return limit.reduce((x, y)->x*y).getAsInt();
    }

    public int oddSum(IntStream limit) {
        return limit.filter(x->x%2!=0).sum();
    }

    public Map<Integer, Integer> sumByRemainder(int i, IntStream limit) {
        return limit.boxed().collect(Collectors.
                toMap(x-> x % i, Function.identity(), Integer::sum));
    }

    public Map<Person, Double> totalScores(Stream<CourseResult> programmingResults) {

        List<CourseResult> courseResults = programmingResults.collect(Collectors.toList());

        int count = (int) courseResults.stream().map(CourseResult::getTaskResults)
                .map(Map::keySet)
                .flatMap(Set<String>::stream)
                .distinct()
                .count();

        return courseResults.stream().
                collect(Collectors.toMap(CourseResult::getPerson,
                        x->x.getTaskResults().values().stream().mapToInt(Integer::intValue).sum() / (double)count));
    }

    public double averageTotalScore(Stream<CourseResult> programmingResults) {
        List<CourseResult> courseResults = programmingResults.collect(Collectors.toList());

        int countOfSubjects = (int) courseResults.stream().map(CourseResult::getTaskResults)
                .map(Map::keySet)
                .flatMap(Set<String>::stream)
                .distinct()
                .count();

        int countOfStudents = (int) courseResults.stream()
                .map(CourseResult::getPerson).count();

        return courseResults.stream().map(CourseResult::getTaskResults)
                .map(Map::values)
                .flatMap(Collection<Integer>::stream)
                .mapToInt(Integer::intValue)
                .sum() / (double) (countOfStudents * countOfSubjects);

    }

    public Map<String, Double> averageScoresPerTask(Stream<CourseResult> programmingResults) {
        List<CourseResult> courseResults = programmingResults.collect(Collectors.toList());

        List<String> tasks = courseResults.stream().map(CourseResult::getTaskResults)
                .map(Map::keySet)
                .flatMap(Set<String>::stream)
                .distinct()
                .collect(Collectors.toList());

        int countOfStudents = (int) courseResults.stream()
                .map(CourseResult::getPerson).count();

        Map<String, Integer> tasksToSum = courseResults.stream()
                .map(CourseResult::getTaskResults)
                .map(Map::entrySet)
                .flatMap(Set::stream)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        Integer::sum
                ));

        return tasksToSum.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        x-> x.getValue() / (double) countOfStudents
                ));
    }

    public Map<Person, String> defineMarks(Stream<CourseResult> programmingResults) {
        Map<Person, Double> avg = totalScores(programmingResults);

        Function<Double, String> f = (x) -> {
            if(x > 90) return "A";
            if(x >= 83) return "B";
            if(x >= 75) return "C";
            if(x >= 68) return "D";
            if(x >= 60) return "E";
            return "F";
        };

        return avg.entrySet().stream()
                .collect(Collectors.toMap(
                   Map.Entry::getKey,
                   x-> f.apply(x.getValue())
                ));
    }

    public String easiestTask(Stream<CourseResult> programmingResults) {
        return averageScoresPerTask(programmingResults)
                .entrySet().stream()
                .max((x, y)->Double.compare(x.getValue(), y.getValue()))
                .get().getKey();
    }

    public Collector<CourseResult, List<CourseResult>, String > printableStringCollector() {


        return new Collector<CourseResult, List<CourseResult>, String>() {
            @Override
            public Supplier<List<CourseResult>> supplier() {
                return ArrayList::new;
            }

            @Override
            public BiConsumer<List<CourseResult>, CourseResult> accumulator() {
                return List::add;
            }

            @Override
            public BinaryOperator<List<CourseResult>> combiner() {
                return (x, y) -> {
                    x.addAll(y);
                    return x;
                };
            }

            @Override
            public Function<List<CourseResult>, String> finisher() {
                return (courseList) -> {

                    Map<Person, Double> totalScoresOfStudent = totalScores(courseList.stream());
                    Map<String, Double> totalScoreOfNames = totalScoresOfStudent.entrySet().stream()
                            .collect(Collectors.toMap(
                                    (x)->x.getKey().getLastName() + " " + x.getKey().getFirstName(),
                                    Map.Entry::getValue
                            ));

                    Map<String, Double> averageScoresOfTask = averageScoresPerTask(courseList.stream());
                    Map<Person, String> marksOfStudents = defineMarks(courseList.stream());

                    Map<String, String> marksOfNames = marksOfStudents.entrySet().stream()
                            .collect(Collectors.toMap(
                                    (x)->x.getKey().getLastName() + " " + x.getKey().getFirstName(),
                                    Map.Entry::getValue
                            ));

                    Map<String, Map<String, Integer>> nameToMapTaskToMark = courseList.stream()
                            .collect(Collectors.toMap(
                                    (CourseResult x)-> x.getPerson().getLastName() + " " + x.getPerson().getFirstName(),
                                    CourseResult::getTaskResults
                            ));

                    List<String> tasks = courseList.stream().map(CourseResult::getTaskResults)
                            .map(Map::keySet)
                            .flatMap(Set<String>::stream)
                            .distinct()
                            .sorted()
                            .collect(Collectors.toList());

                    List<Person> students = courseList.stream().map(CourseResult::getPerson)
                            .collect(Collectors.toList());

                    List<String> names = students.stream().map(x->x.getLastName() + " " + x.getFirstName())
                            .collect(Collectors.toList());

                    int longestNameSize = names.stream().mapToInt(String::length).max().orElse(0);
                    int longestTaskSize = tasks.stream().mapToInt(String::length).max().orElse(0);

                    StringBuilder sb = new StringBuilder();

                    sb.append(String.format("%-" + longestNameSize + "s | ", "Student"));
                    tasks.forEach(x-> sb.append(x).append(" | "));
                    sb.append("Total | Mark |\n");

                    names.stream().sorted().forEach(name -> {
                        sb.append(String.format("%-" + longestNameSize + "s | ", name));

                        tasks.forEach(task -> {
                            sb.append(String.format("%" + task.length() + "d | ",
                                    nameToMapTaskToMark.get(name).getOrDefault(task, 0)));
                        });

                        sb.append(String.format("%5.2f | ", totalScoreOfNames.get(name)));
                        sb.append(String.format("%4s |\n", marksOfNames.get(name)));
                    });

                    sb.append(String.format("%-" + longestNameSize + "s | ", "Average"));

                    tasks.forEach((task) -> {
                        sb.append(String.format("%" + task.length() + ".2f | ",
                                averageScoresOfTask.get(task)));
                    });

                    double sum = averageScoresOfTask.values().stream().mapToDouble(Double::doubleValue).sum();

                    Function<Double, String> f = (x) -> {
                        if(x > 90) return "A";
                        if(x >= 83) return "B";
                        if(x >= 75) return "C";
                        if(x >= 68) return "D";
                        if(x >= 60) return "E";
                        return "F";
                    };

                    sb.append(String.format("%5.2f | ", sum / tasks.size()));
                    sb.append(String.format("%4s |", f.apply(sum / tasks.size())));

                    return sb.toString().replace(",", ".");
                };
            }

            @Override
            public Set<Characteristics> characteristics() {
                return EnumSet.of(Characteristics.CONCURRENT);
            }
        };
    }
}
