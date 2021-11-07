package Marks;

import java.util.ArrayList;
import java.util.List;

public class Group
{
    private final String name;
    private final List<Student> students;

    public Group(String name)
    {
        this.name = name;
        students = new ArrayList<>();
    }

    public void addStudent(Student student)
    {
        students.add(student);
        student.setGroup(name);
    }

    public String getName()
    {
        return name;
    }

    public List<Student> getStudents()
    {
        return students;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();
        builder.append(name);
        builder.append(":");
        builder.append("\n");
        for (Student student : students)
        {
            builder.append(student.toString());
            builder.append("\n");
        }
        builder.append("\n");
        return builder.toString();
    }
}
