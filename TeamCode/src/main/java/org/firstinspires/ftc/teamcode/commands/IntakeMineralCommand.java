package org.firstinspires.ftc.teamcode.commands;

import com.acmerobotics.roadrunner.trajectory.Trajectory;

import org.firstinspires.ftc.teamcode.Constants;
import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.DriveTrainSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.IntakeSubsystem;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class IntakeMineralCommand extends CommandBase {
    private final DriveTrainSubsystem m_driveTrain;
    private final ArmSubsystem m_armSubsystem;
    private final IntakeSubsystem m_intake;
    private final double MAX_DISTANCE;
    private Trajectory forwardTrajectory;
    private Status status;
    private long startTime;

    private enum Status {
        Forward,
        Backward,
        Finished
    }

    public IntakeMineralCommand(DriveTrainSubsystem driveTrain, ArmSubsystem armSubsystem, IntakeSubsystem intake, double max_distance) {
        m_driveTrain = driveTrain;
        m_armSubsystem = armSubsystem;
        m_intake = intake;
        MAX_DISTANCE = max_distance;

        addRequirements(m_driveTrain, m_armSubsystem, m_intake);
    }

    @Override
    public void initialize() {
        forwardTrajectory = m_driveTrain.trajectoryBuilder(m_driveTrain.getPoseEstimate(),
                DriveTrainSubsystem.getVelocityConstraint(Constants.DriveTrainConstants.MaxVelocity  * 0.4,
                        Constants.DriveTrainConstants.MaxAnglerVelocity,
                        Constants.DriveTrainConstants.TrackWidth))
                .forward(MAX_DISTANCE).build();

        status = Status.Forward;

        m_driveTrain.followTrajectoryAsync(forwardTrajectory);
        m_armSubsystem.setVerticalPosition(0);
        m_intake.intake(1);
        startTime = System.nanoTime();
    }

    @Override
    public void execute() {
        switch (status) {
            case Forward:
                if (m_intake.hasFreight() || (!m_driveTrain.isBusy() && (System.nanoTime() - startTime) > 0.25e+9)) {
                    status = Status.Backward;
                    m_driveTrain.stop();

                    Trajectory backwardTrajectory = m_driveTrain.trajectoryBuilder(m_driveTrain.getPoseEstimate()).lineTo(forwardTrajectory.start().vec()).build();
                    m_driveTrain.followTrajectoryAsync(backwardTrajectory);

                    startTime = System.nanoTime();
                    m_armSubsystem.setVerticalPosition(1);
                    m_intake.intake(0);
                }
                break;
            case Backward:
                if (!m_driveTrain.isBusy() && (System.nanoTime() - startTime) > 0.25e+9) {
                    status = Status.Finished;
                }
                break;
        }
    }

    @Override
    public boolean isFinished() {
        return status == Status.Finished;
    }
}
