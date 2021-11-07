package Marks;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class MarkThread extends Thread
{
    private static final int MIN_MARK = 60;
    private static final int MAX_MARK = 100;

    private final MarkBook markBook;
    private final int weeks;
    private final Random random;

    public MarkThread(MarkBook markBook, int weeks)
    {
        this.markBook = markBook;
        this.weeks = weeks;
        random = new Random();
    }

    @Override
    public void run()
    {
        try
        {
            for (int i = 0; i < weeks; i++)
            {
                var count = random.nextInt(countStudents());
                for (int j = 0; j < count; j++)
                {
                    int mark = random.nextInt(MAX_MARK - MIN_MARK) + MIN_MARK;
                    markBook.setMark(getRandomStudent(), mark);
                    Thread.sleep(1);
                }
            }
        }
        catch (InterruptedException e)
        {
        }
    }

    private int countStudents()
    {
        return markBook.getStudents().size();
    }

    private Student getRandomStudent()
    {
        List<Student> students = markBook.getStudents();
        return students.get(random.nextInt(students.size()));
    }
}
