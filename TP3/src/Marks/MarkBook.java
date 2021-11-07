package Marks;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class MarkBook
{
    private final List<Group> groups;
    private final HashMap<Student, Integer> marks;

    public MarkBook()
    {
        groups = new ArrayList<>();
        marks = new HashMap<>();
    }

    public void addGroup(Group group)
    {
        groups.add(group);
    }

    public List<Group> getGroups()
    {
        return groups;
    }

    public List<Student> getStudents()
    {
        return getGroups()
                .stream()
                .map(Group::getStudents)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public synchronized void setMark(Student student, int mark)
    {
        marks.put(student, mark);
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        for (Group group : groups)
        {
            builder.append(group.getName());
            builder.append(":");
            builder.append("\n");
            for (Student student : group.getStudents())
            {
                builder.append(student.toString());
                builder.append(" - ");
                if (marks.containsKey(student))
                {
                    builder.append(marks.get(student));
                }
                builder.append("\n");
            }
            builder.append("\n");
        }
        return builder.toString();
    }
}
