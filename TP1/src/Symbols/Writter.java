package Symbols;

public class Writter
{
    private final Object lock = new Object();
    private char currentCharacter;
    private int characterCount;

    public void Write(char character)
    {
        synchronized (lock)
        {
            if (currentCharacter == character)
            {
                try
                {
                    lock.wait();
                } catch (InterruptedException e)
                {
                }
            }
            System.out.print(character);
            characterCount++;
            if (characterCount % 10 == 0) {
                System.out.print('\n');
            }
            currentCharacter = character;
            lock.notify();
        }
    }
}
