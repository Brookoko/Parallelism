package Marks;

import java.util.Objects;

public class Student
{
    private final String name;
    private String group;

    public Student(String name)
    {
        this.name = name;
    }

    public void setGroup(String group)
    {
        this.group = group;
    }

    @Override
    public String toString()
    {
        return name + " (" + group + ")";
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Student student = (Student) o;
        return Objects.equals(name, student.name) && Objects.equals(group, student.group);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(name, group);
    }
}
