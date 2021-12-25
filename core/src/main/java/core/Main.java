package core;

import entities.job.MainWindow;

public final class Main
  {
    public static void main(String[] args)
    {
      var window = MainWindow.get();
      window.Run();
    }
  }
