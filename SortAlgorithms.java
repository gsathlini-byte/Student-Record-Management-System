import java.util.List;
import java.util.ArrayList;

public class SortAlgorithms {

    public static List<Student> mergeSortByGpaDescending(List<Student> students) {
        if (students.size() <= 1) return students;
        List<Student> copy = new ArrayList<>(students);
        return mergeSort(copy);
    }

    private static List<Student> mergeSort(List<Student> list) {
        if (list.size() <= 1) return list;
        int mid = list.size() / 2;
        List<Student> left = mergeSort(new ArrayList<>(list.subList(0, mid)));
        List<Student> right = mergeSort(new ArrayList<>(list.subList(mid, list.size())));
        return merge(left, right);
    }

    private static List<Student> merge(List<Student> left, List<Student> right) {
        List<Student> result = new ArrayList<>();
        int i = 0, j = 0;
        while (i < left.size() && j < right.size()) {
            // descending order: වැඩිම GPA එක මුලින්ම
            if (left.get(i).getGpa() >= right.get(j).getGpa()) {
                result.add(left.get(i++));
            } else {
                result.add(right.get(j++));
            }
        }
        while (i < left.size()) result.add(left.get(i++));
        while (j < right.size()) result.add(right.get(j++));
        return result;
    }
}