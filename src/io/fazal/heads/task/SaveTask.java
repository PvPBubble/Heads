package io.fazal.heads.task;

import io.fazal.heads.Main;

import java.util.concurrent.TimeUnit;

public class SaveTask extends Thread {

    public void run() {
        try {
            Thread.sleep(TimeUnit.MINUTES.toMillis(Main.getInstance().getConfig().getInt("Settings.SaveTaskFrequency")));
            long startTime = System.currentTimeMillis();
            Main.getInstance().getTokenManager().dumpToFile();
            Main.getInstance().log("§c[Heads] Saved all data to file in " + (System.currentTimeMillis() - startTime) + " ms");
        } catch (InterruptedException e) {
            Main.getInstance().log("§c[Heads] There was an error saving data from SaveTask");
            e.printStackTrace();
        }
    }

}