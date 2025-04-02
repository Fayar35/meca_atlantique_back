package meca.atlantique.hurco;

public class MainTest {
    public static void main(String[] args) {
        System.out.println(MTConnectApi.getPrgStatus("192.168.0.113", (short) 5000));
        System.out.println(MTConnectApi.getPrgName("192.168.0.113", (short) 5000));
    }
}
