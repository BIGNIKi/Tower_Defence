import Core.MainCycle;

public final class Main {
    public static void main(String[] args)
    {
        MainCycle mainCycle = MainCycle.get();
        if(args.length > 0 && args[0].equals("NoUI"))
        {
            mainCycle.run(false);
        }
        else
        {
            mainCycle.run(true);
        }
    }
}
