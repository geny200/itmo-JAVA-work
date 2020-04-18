package ru.ifmo.rain.konovalov.student;

import info.kgeorgiy.java.advanced.student.Group;
import info.kgeorgiy.java.advanced.student.Student;
import info.kgeorgiy.java.advanced.student.StudentGroupQuery;

import java.util.*;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StudentDB implements StudentGroupQuery {
    protected final Comparator<Student> comparatorFullName = Comparator
            .comparing(
                    Student::getLastName,
                    String.CASE_INSENSITIVE_ORDER)
            .thenComparing(
                    Student::getFirstName,
                    String.CASE_INSENSITIVE_ORDER)
            .thenComparingInt(
                    Student::getId);

    @Override
    public Map<String, String> findStudentNamesByGroup(Collection<Student> students, String group) {
        return students
                .stream()
                .filter(Student ->
                        Student.getGroup().
                                equals(group))
                .collect(Collectors.toMap(
                        Student::getLastName,
                        Student::getFirstName,
                        BinaryOperator.minBy(String.CASE_INSENSITIVE_ORDER)));
    }

    private Stream<String> getByFunc(List<Student> students, Function<Student, String> toStr) {
        return students.stream().
                map(toStr);
    }

    @Override
    public List<String> getFirstNames(List<Student> students) {
        return getByFunc(students, Student::getFirstName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> getLastNames(List<Student> students) {
        return getByFunc(students, Student::getLastName).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getGroups(List<Student> students) {
        return getByFunc(students, Student::getGroup).
                collect(Collectors.toList());
    }

    @Override
    public List<String> getFullNames(List<Student> students) {
        return getByFunc(
                students,
                student -> String.join(
                        " ",
                        student.getFirstName(),
                        student.getLastName()))
                .collect(Collectors.toList());
    }

    @Override
    public Set<String> getDistinctFirstNames(List<Student> students) {
        return getByFunc(
                students,
                Student::getFirstName)
                .collect(Collectors.toCollection(TreeSet::new));
    }

    @Override
    public String getMinStudentFirstName(List<Student> students) {
        return students
                .stream()
                .min(Comparator.comparingInt(Student::getId))
                .map(Student::getFirstName)
                .orElse("");
    }

    @Override
    public List<Student> sortStudentsById(Collection<Student> students) {
        return students.stream().
                sorted(Comparator.comparingInt(Student::getId)).
                collect(Collectors.toList());
    }

    private Stream<Student> sortStream(Collection<Student> students) {
        return students.stream().
                sorted(comparatorFullName);
    }

    @Override
    public List<Student> sortStudentsByName(Collection<Student> students) {
        return sortStream(students)
                .collect(Collectors.toList());
    }

    private List<Student> findStudent(Collection<Student> students, String name, Function<Student, String> eqStr) {
        return sortStream(students)
                .filter(student -> eqStr
                        .apply(student)
                        .equals(name))
                .collect(Collectors.toList());
    }

    @Override
    public List<Student> findStudentsByFirstName(Collection<Student> students, String name) {
        return findStudent(students, name, Student::getFirstName);
    }

    @Override
    public List<Student> findStudentsByLastName(Collection<Student> students, String name) {
        return findStudent(students, name, Student::getLastName);
    }

    @Override
    public List<Student> findStudentsByGroup(Collection<Student> students, String group) {
        return findStudent(students, group, Student::getGroup);
    }

    private List<Group> makeGroup(Collection<Student> students, Comparator<? super Student> comparator) {
        return students.stream().sorted(comparator)
                .collect(Collectors.collectingAndThen(
                        Collectors.collectingAndThen(
                                Collectors.groupingBy(Student::getGroup),
                                Map::values),
                        Collection::stream))
                .map(st -> new Group(st.get(0).getGroup(), st))
                .sorted(Comparator.comparing(
                        Group::getName,
                        String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    }

    @Override
    public List<Group> getGroupsByName(Collection<Student> students) {
        return makeGroup(students, comparatorFullName);
    }

    @Override
    public List<Group> getGroupsById(Collection<Student> students) {
        return makeGroup(students, Comparator.comparingInt(Student::getId));
    }

    @Override
    public String getLargestGroup(Collection<Student> students) {
        return getGroupsById(students)
                .stream()
                .max(Comparator
                        .comparing(group -> group.getStudents().size()))
                .map(Group::getName)
                .orElse("");
    }

    @Override
    public String getLargestGroupFirstName(Collection<Student> students) {
        return getGroupsById(students)
                .stream()
                .max(Comparator
                        .comparing(group -> group
                                .getStudents()
                                .stream()
                                .map(Student::getFirstName)
                                .distinct()
                                .count()))
                .map(Group::getName)
                .orElse("");
    }
}
