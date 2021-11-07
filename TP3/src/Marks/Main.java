package Marks;

import java.util.Random;

public class Main
{
    private static final int GROUPS = 3;
    private static final int MIN_STUDENT_NUMBER = 15;
    private static final int MAX_STUDENT_NUMBER = 30;
    private static final int THREADS = 4;
    private static final int WEEKS = 7;

    private static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException
    {
        MarkBook markBook = createMarkBook();
        Thread[] threads = new Thread[THREADS];
        for (int i = 0; i < THREADS; i++)
        {
            Thread thread = new MarkThread(markBook, WEEKS);
            threads[i] = thread;
            thread.start();
        }
        for (int i = 0; i < THREADS; i++)
        {
            threads[i].join();
        }
        System.out.println(markBook);
    }

    private static MarkBook createMarkBook()
    {
        var markBook = new MarkBook();
        for (int i = 0; i < GROUPS; i++)
        {
            markBook.addGroup(createGroup(i + 1));
        }
        return markBook;
    }

    private static Group createGroup(int groupNumber)
    {
        Group group = new Group("IP-8" + groupNumber);
        var count = random.nextInt(MAX_STUDENT_NUMBER - MIN_STUDENT_NUMBER) + MIN_STUDENT_NUMBER;
        for (int i = 0; i < count; i++)
        {
            group.addStudent(createStudent(i + 1));
        }
        return group;
    }

    private static Student createStudent(int i)
    {
        return new Student("St " + i);
    }
}
