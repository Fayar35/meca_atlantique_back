package meca.atlantique.fanuc;

import com.sun.jna.Native;

public class FanucApiProvider {
    private static FanucApi instance = (FanucApi) Native.load("Fwlib32", FanucApi.class);

    public static FanucApi getInstance() {
        return instance;
    }
}
