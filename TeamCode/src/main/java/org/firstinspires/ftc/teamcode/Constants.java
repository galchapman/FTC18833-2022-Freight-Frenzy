package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.roadrunner.geometry.Pose2d;

import org.opencv.core.Rect;

public final class Constants {
    public static final class MotorConstants {
        public static final class RevHDHexMotor {
            public static final int ticks_per_revolution = 28;
            public static final int freeSpeedRPM = 6000;
            public static final int revolution_per_second = freeSpeedRPM / 60;
        }

        public static final class REVThroughBoreEncoder {
            public static final int ticks_per_revolution = 8192;
        }
    }

    @Config
    public static final class DriveTrainConstants {
        public final static double ticks_per_revolution =
                MotorConstants.REVThroughBoreEncoder.ticks_per_revolution;

        public final static double WheelDiameter = 0.096;
        public final static double HorizontalOdometryWheelDiameter = 0.06;

        public final static double GearRatio = 20;

        public final static double TrackWidth = 0.259;
        public final static double MaxVelocity = 1.45; //MotorConstants.RevHDHexMotor.revolution_per_second / GearRatio * WheelDiameter * Math.PI;
        public static double MaxAccel = 4;
        public final static double MaxAnglerVelocity = MaxVelocity / TrackWidth;
        public final static double MaxAnglerAccel = Math.toRadians(180);

        public final static double odometry_wheel_ticks_to_meters = HorizontalOdometryWheelDiameter * Math.PI / ticks_per_revolution;

        public final static double kV = 0.5903;
        public final static double kStatic = 0.12282;
        public final static double kA = 0.00256;

        @Config
        public final static class OdometryConstants {
            public static Pose2d frontLeftWheelPosition = new Pose2d(0.16357, -0.07);
            public static Pose2d frontRightWheelPosition = new Pose2d(0.16357, 0.07);

            public static Pose2d horizontalWheelPosition = new Pose2d(-0.15152, 0, Math.toRadians(-90));
        }
    }

    public static final class LiftConstants {
        public static final int inverse_motor_gear = 80;
        public static final int ticks_per_motor_revolution = MotorConstants.RevHDHexMotor.ticks_per_revolution * inverse_motor_gear;
        public static final double gear = 15f / 10f;
        public static final int ticks_per_revolution = (int)(ticks_per_motor_revolution / gear);
        public static final double gear_radios = 0.0205;
        public static final double distance_per_revolution = 2 * Math.PI * gear_radios;
        public static final double distance_per_tick = distance_per_revolution / ticks_per_revolution;

        public static final double lower_plate_height = 0.165;
        public static final double sensor_height = 0.087;
    }

    public static final class ArmConstants {
        public static final int gear = 70;
        public static final int motorGear = MotorConstants.RevHDHexMotor.ticks_per_revolution;
    }

    public static final class IntakeConstants {
        public static final double intakeThreshold = 0.05;
    }

    public static final class DucksConstants {
        public static final double biggerWheelDiameter = 15 * 0.0254;
        public static final double wheelDiameter = 0.059;
        public static final double ticks_per_rotation =
                MotorConstants.RevHDHexMotor.ticks_per_revolution * 12
                        * biggerWheelDiameter / wheelDiameter;
    }

    @Config
    public static final class VisionConstants {
        public final static int camera_width = 1280;
        public final static int camera_height = 800;

        public static Rect FarRedARect = new Rect(700, 550, 225, 200);
        public static Rect FarRedBRect = new Rect(1150, 550, 50, 200);
        public static Rect NearRedARect = new Rect();
        public static Rect NearRedBRect = new Rect();
        public static Rect FarBlueBRect = new Rect(0, 550, 130, 200);
        public static Rect FarBlueCRect = new Rect(355, 550, 225, 200);
        public static Rect NearBlueBRect = new Rect();
        public static Rect NearBlueCRect = new Rect();

        public static double HueThresholdLow = 55;
        public static double HueThresholdHigh = 95;
        public static double SaturationThresholdLow = 77;
        public static double SaturationThresholdHigh = 255;
        public static double ValueThresholdLow = 37;
        public static double ValueThresholdHigh = 255;
    }
}
