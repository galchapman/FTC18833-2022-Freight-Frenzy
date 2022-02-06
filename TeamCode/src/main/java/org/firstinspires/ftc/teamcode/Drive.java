package org.firstinspires.ftc.teamcode;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.commandftc.opModes.CommandBasedTeleOp;
import org.firstinspires.ftc.teamcode.commands.DuckRoller.IndexDuckCommand;
import org.firstinspires.ftc.teamcode.commands.SetRobotArmsPosition;
import org.firstinspires.ftc.teamcode.commands.arm.RotateArmPowerCommand;
import org.firstinspires.ftc.teamcode.commands.arm.RotateArmToPositionCommand;
import org.firstinspires.ftc.teamcode.commands.arm.SetIntakeArmPositionCommand;
import org.firstinspires.ftc.teamcode.commands.drive.ArcadeDriveCommand;
import org.firstinspires.ftc.teamcode.commands.drive.DriveLeftCommand;
import org.firstinspires.ftc.teamcode.commands.drive.DriveRightCommand;
import org.firstinspires.ftc.teamcode.commands.drive.TankDriveCommand;
import org.firstinspires.ftc.teamcode.commands.intake.IntakeCommand;
import org.firstinspires.ftc.teamcode.commands.lift.RaiseLiftCommand;
import org.firstinspires.ftc.teamcode.commands.lift.SetLiftHeightCommand;
import org.firstinspires.ftc.teamcode.lib.DashboardUtil;
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrainSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DucksSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.Trigger;

@TeleOp(name = "Drive")
public class Drive extends CommandBasedTeleOp
{
    DriveTrainSubsystem driveTrain;
    LiftSubsystem liftSubsystem;
    ArmSubsystem armSubsystem;
    IntakeSubsystem intakeSubsystem;
    DucksSubsystem ducksSubsystem;
    // Drive Commands
    TankDriveCommand tankDriveCommand;
    ArcadeDriveCommand arcadeDriveCommand;
    DriveLeftCommand driveLeftCommand;
    DriveRightCommand driveRightCommand;
    // Lift Commands
    RaiseLiftCommand raiseLiftCommand;
    SetLiftHeightCommand resetLiftCommand;
    // Arm commands
    RotateArmPowerCommand rotateArmContinuouslyCommand;
    RotateArmToPositionCommand rotateArmToMiddleCommand;
    SetIntakeArmPositionCommand resetIntakeArmPositionCommand;
    // intake commands
    IntakeCommand intakeCommand;
    //Ducky command
    SequentialCommandGroup indexDuckCommand;
    //Multiple subsystem commands
    Command GoToIntakePositionCommand;
    SetRobotArmsPosition GoToScoringPositionCommand;

    private double getDriveSpeed() {
        if (gamepad1.left_trigger > 0)          return 0.5;
        else if (gamepad1.right_trigger > 0)    return 1;
        else                                    return 0.75;
    }

    private double getArmRotationPower() {
        double power = 0.5 * (gamepad2.right_trigger - gamepad2.left_trigger);
        if (armSubsystem.getAngle() > -80 && armSubsystem.getAngle() < 85)  return power;
        else if (power > 0 && armSubsystem.getAngle() < -80)                return power;
        else if (power < 0 && armSubsystem.getAngle() > 85)                 return power;
        else                                                                return 0;
    }

    private boolean canLowerArm() {
        return (-30 < armSubsystem.getAngle() && armSubsystem.getAngle() < 30) || liftSubsystem.getHeight() > 0.3;
    }

    public void saveArmsLocation() {
        if (Math.abs(armSubsystem.getAngle()) > 20)
            GoToScoringPositionCommand.setTarget(liftSubsystem.getHeight(), armSubsystem.getAngle(), armSubsystem.getVerticalPosition());
    }

    @Override
    public void assign() {
        driveTrain = new DriveTrainSubsystem();
        liftSubsystem = new LiftSubsystem();
        armSubsystem = new ArmSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        ducksSubsystem = new DucksSubsystem();

        armSubsystem.setVerticalPosition(0.2);

        tankDriveCommand = new TankDriveCommand(driveTrain, () -> -gamepad1.left_stick_y * getDriveSpeed(), () -> -gamepad1.right_stick_y * getDriveSpeed());
        arcadeDriveCommand = new ArcadeDriveCommand(driveTrain, () -> gamepad1.left_stick_x, () -> -gamepad1.left_stick_y, () -> gamepad1.right_stick_x);
        driveLeftCommand = new DriveLeftCommand(driveTrain, this::getDriveSpeed);
        driveRightCommand = new DriveRightCommand(driveTrain, this::getDriveSpeed);

        raiseLiftCommand = new RaiseLiftCommand(liftSubsystem, () -> -gamepad2.left_stick_y);
        resetLiftCommand = new SetLiftHeightCommand(liftSubsystem,0, 0.7);

        rotateArmContinuouslyCommand = new RotateArmPowerCommand(armSubsystem, this::getArmRotationPower);
        rotateArmToMiddleCommand = new RotateArmToPositionCommand(armSubsystem,0,1);
        resetIntakeArmPositionCommand = new SetIntakeArmPositionCommand(armSubsystem,0);

        intakeCommand = new IntakeCommand(intakeSubsystem, () -> gamepad2.right_stick_y);

        indexDuckCommand = new IndexDuckCommand(ducksSubsystem,1000,1).andThen(new WaitCommand(2));

        GoToIntakePositionCommand = new InstantCommand(
                () -> {saveArmsLocation(); intakeSubsystem.setDoorState(IntakeSubsystem.DoorState.Closed); })
                .andThen(new SetRobotArmsPosition(armSubsystem, liftSubsystem, 0, 1, 0, 1, 0));
        GoToScoringPositionCommand = new SetRobotArmsPosition(armSubsystem, liftSubsystem, 0, 1, 50, 0.6, 0.5);

        // DriveTrain commands
        driveTrain.setDefaultCommand(tankDriveCommand);
        gp1.x().whileHeld(arcadeDriveCommand);
        gp1.dpad_left().whileHeld(driveLeftCommand);
        gp1.left_bumper().whileHeld(driveLeftCommand);
        gp1.dpad_right().whileHeld(driveRightCommand);
        gp1.right_bumper().whileHeld(driveRightCommand);
        // Lift commands
        liftSubsystem.setDefaultCommand(raiseLiftCommand);
        // Arm command
        armSubsystem.setDefaultCommand(rotateArmContinuouslyCommand);

        gp2.left_bumper().whileHeld(() -> armSubsystem.setVerticalPosition(Math.min(armSubsystem.getVerticalPosition()+0.015, 0.65)));
        gp2.right_bumper().whileHeld(() -> armSubsystem.setVerticalPosition(Math.max(armSubsystem.getVerticalPosition()-0.015, 0)));

        gp2.b().whenActive(() -> armSubsystem.setVerticalPosition(1), armSubsystem);
        gp2.a().and(new Trigger(this::canLowerArm)).whenActive(() -> armSubsystem.setVerticalPosition(0), armSubsystem);
        gp2.x().whenActive(() -> armSubsystem.setVerticalPosition(0.4), armSubsystem);
        //gp2.y().whenActive(() -> armSubsystem.setVerticalPosition(0.4), armSubsystem);

        gp2.right_stick_button().whenPressed(GoToIntakePositionCommand);
        gp2.left_stick_button().whenPressed(GoToScoringPositionCommand);
        // Intake commands
        intakeSubsystem.setDefaultCommand(intakeCommand);
        gp2.y().whenPressed(() -> intakeSubsystem.toggleDoor());
        // Duck commands
        //gp2.y().whileHeld(indexDuckCommand);


        // Telemetry
        // No need for anything but update in loop because use of suppliers
        telemetry.addData("Runtime", this::getRuntime);
        telemetry.addData("dt(s)", this::dt);
        telemetry.addData("angle", armSubsystem::getAngle);
        telemetry.addData("lift height", liftSubsystem::getHeight);
        telemetry.addData("lift height sensor", liftSubsystem::getSensorHeight);
        telemetry.addData("lift height offset", LiftSubsystem.ticks2meters(liftSubsystem.getEncoderOffset()));
        telemetry.update();

        TelemetryPacket init_telemetry_packet = new TelemetryPacket();
        DashboardUtil.drawRobot(init_telemetry_packet.fieldOverlay(), driveTrain.getPoseEstimate());
        FtcDashboard.getInstance().sendTelemetryPacket(init_telemetry_packet);

        driveTrain.setOdometryPosition(0.45);
    }
}