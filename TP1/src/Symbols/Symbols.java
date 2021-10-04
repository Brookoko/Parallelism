package Symbols;

public class Symbols
{
    private static final int NUMBER_OF_SYMBOLS = 100;

    public static void main(String[] args) throws InterruptedException
    {
        Writter writter = new Writter();
        Thread minusThread = new SymbolThread(writter, NUMBER_OF_SYMBOLS, '-');
        Thread barThread = new SymbolThread(writter, NUMBER_OF_SYMBOLS, '|');
        minusThread.start();
        barThread.start();
        minusThread.join();
        barThread.join();
        System.out.flush();
    }
}

