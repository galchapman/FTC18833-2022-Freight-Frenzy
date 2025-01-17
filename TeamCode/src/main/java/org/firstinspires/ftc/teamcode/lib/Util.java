package org.firstinspires.ftc.teamcode.lib;

public final class Util {
    public static double clamp(double low, double high, double value) {
        return Math.max(low, Math.min(high, value));
    }

    public static double clamp(double bound, double value) {
        return clamp(-bound, bound, value);
    }

    public static double minAbs(double a, double b) {
        return Math.abs(a) < Math.abs(b) ? a : b;
    }

    public static double maxAbs(double a, double b) {
        return Math.abs(a) > Math.abs(b) ? a : b;
    }
}
