package org.firstinspires.ftc.teamcode.opmodes.drive;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.DuckRoller.FancyDuckIndexCommand;
import org.firstinspires.ftc.teamcode.commands.drive.ArcadeDriveCommand;
import org.firstinspires.ftc.teamcode.commands.drive.FieldCentricArcadeDriveCommand;

import edu.wpi.first.wpilibj2.command.button.Trigger;

@TeleOp(name="Red Drive", group = "Drive")
@Config
public class RedDrive extends Drive {
    @Override
    public void assign() {
        super.assign();
        GoToScoringPositionCommand.setTarget(0.20, 50, 0.5);
        GoToSippingHubCommand.setTarget(0.395, -70, 0.57);
        fancyDuckIndexCommand = new FancyDuckIndexCommand(ducksSubsystem, Constants.DucksConstants.maxPower, Constants.DucksConstants.minPower, Constants.DucksConstants.accelerationSpeed, Constants.DucksConstants.redSpin);
        arcadeDriveCommand = new FieldCentricArcadeDriveCommand(driveTrain, () -> gamepad1.left_stick_y, () -> gamepad1.left_stick_x, () -> -gamepad1.right_stick_y, Math.toDegrees(-90));

        gp1.x.whenPressed(() -> driveTrain.setPose(new Pose2d(0, 0, Math.toRadians(-90))));

        driveTrain.setDefaultCommand(tankDriveCommand);
        gp1.y.whenPressed(fancyDuckIndexCommand);

        new Trigger(() -> gamepad1.a && !gamepad1.right_bumper).whileActiveContinuous(arcadeDriveCommand);

        new Trigger(() -> gamepad1.a && gamepad1.right_bumper).whileActiveContinuous((new ArcadeDriveCommand(driveTrain,() -> -0.15,
                () -> (gamepad1.left_stick_y > 0.1) ? 1 :
                        (gamepad1.left_stick_y < -0.1 ? -1 : 0), () -> 0)));

        telemetry.addData("index", ducksSubsystem::getCurrentPosition);
        telemetry.addData("index rotation", () -> ducksSubsystem.getCurrentPosition() / Constants.DucksConstants.ticks_per_rotation);

        new Trigger(() -> driveTrain.getLineColorSensorBrightness() > 200)
                .whileActiveContinuous(() -> gamepad1.rumble(500));
    }

    @Override
    public void updateFtcDashboardTelemetry(TelemetryPacket packet) {
        packet.put("heading", Math.toDegrees(driveTrain.getHeading()));
        packet.put("drive heading", Math.toDegrees(driveTrain.getDriveHeading() + driveTrain.getHeading()));
        packet.put("velocity", driveTrain.accel);
        packet.put("intake distance", intakeSubsystem.getDistance());
        FtcDashboard.getInstance().sendTelemetryPacket(packet);
    }
}
