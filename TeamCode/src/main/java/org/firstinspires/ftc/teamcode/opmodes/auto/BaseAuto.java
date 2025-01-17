package org.firstinspires.ftc.teamcode.opmodes.auto;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.hardware.DcMotor;

import org.commandftc.RobotUniversal;
import org.commandftc.opModes.CommandBasedAuto;
import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.commands.DuckRoller.FancyDuckIndexCommand;
import org.firstinspires.ftc.teamcode.commands.IntakeMineralCommand;
import org.firstinspires.ftc.teamcode.commands.drive.RoadRunnerThread;
import org.firstinspires.ftc.teamcode.commands.drive.TurnCommand;
import org.firstinspires.ftc.teamcode.lib.DashboardUtil;
import org.firstinspires.ftc.teamcode.lib.StartingPosition;
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrainSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DucksSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.VisionSubsystem;

import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.InstantCommand;

public abstract class BaseAuto extends CommandBasedAuto {
    protected DriveTrainSubsystem driveTrain;
    protected ArmSubsystem armSubsystem;
    protected IntakeSubsystem intakeSubsystem;
    protected LiftSubsystem liftSubsystem;
    protected DucksSubsystem ducksSubsystem;
    protected VisionSubsystem vision;
    protected StartingPosition startingPosition;

    protected RoadRunnerThread thread;

    protected BaseAuto(StartingPosition startingPosition) {
        this.startingPosition = startingPosition;
    }
    private double lastTime = 0;

    @Override
    public void plan() {
        RobotUniversal.opModeType = RobotUniversal.OpModeType.Autonomous;
        RobotUniversal.startingPosition = startingPosition;
        driveTrain = new DriveTrainSubsystem();
        armSubsystem = new ArmSubsystem();
        intakeSubsystem = new IntakeSubsystem();
        liftSubsystem = new LiftSubsystem();
        vision = new VisionSubsystem();
        ducksSubsystem = new DucksSubsystem();

        thread = new RoadRunnerThread(driveTrain);

        driveTrain.setOdometryPosition(DriveTrainSubsystem.OdometryPosition.Down);
        armSubsystem.setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        armSubsystem.setPower(1);

        liftSubsystem.setRunMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftSubsystem.setPower(1);

        initialize();

        TelemetryPacket temp_packet = new TelemetryPacket();
        DashboardUtil.drawRobot(temp_packet.fieldOverlay(), driveTrain.getPoseEstimate());
        FtcDashboard.getInstance().sendTelemetryPacket(temp_packet);

        RobotUniversal.telemetryPacketUpdater = (packet) -> {
            packet.put("FrontLeftPower", driveTrain.getFrontLeftPower());
            packet.put("RearLeftPower", driveTrain.getRearLeftPower());
            packet.put("FrontRightPower", driveTrain.getFrontRightPower());
            packet.put("RearRightPower", driveTrain.getRearRightPower());
            packet.put("isBusy", driveTrain.isBusy());
            packet.put("hasFreight", intakeSubsystem.hasFreight());
            double time = getRuntime();
            packet.put("dt", time - lastTime);
            lastTime = time;

            FtcDashboard.getInstance().sendTelemetryPacket(packet);
        };
    }

    @Override
    public void onStart() {
        thread.schedule();
        //vision.stop();
        armSubsystem.setVerticalPosition(1);
    }

    @Override
    public void onEnd() {
        thread.end(true);
        RobotUniversal.endPosition = driveTrain.getPoseEstimate();
    }

    abstract public void initialize();

    protected Command turn(double angle) {
        return new TurnCommand(driveTrain, angle);
    }

    protected Command setDoor(IntakeSubsystem.DoorState state) {
        return new InstantCommand(() -> intakeSubsystem.setDoorState(state));
    }

    protected Command intake(double distance) {
        return new IntakeMineralCommand(driveTrain, armSubsystem, intakeSubsystem, distance);
    }

    protected Command rotateDuck(double spins){
        return new FancyDuckIndexCommand(ducksSubsystem,
                Constants.DucksConstants.maxPower - 0.4,
                Constants.DucksConstants.minPower - 0.1,
                Constants.DucksConstants.accelerationSpeed - 0.8,
                spins);
    }
}
