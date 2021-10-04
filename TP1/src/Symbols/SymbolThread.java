package Symbols;

public class SymbolThread extends Thread
{
    private final Writter writter;
    private final int numberOfSymbols;
    private final char symbol;

    public SymbolThread(Writter writter, int numberOfSymbols, char symbol)
    {
        this.writter = writter;
        this.numberOfSymbols = numberOfSymbols;
        this.symbol = symbol;
    }

    @Override
    public void run()
    {
        for (int i = 0; i < numberOfSymbols; i++)
        {
            writter.Write(symbol);
        }
    }
}
