package org.firstinspires.ftc.teamcode.commands.arm;

import org.firstinspires.ftc.teamcode.subsystems.ArmSubsystem;
import org.firstinspires.ftc.teamcode.subsystems.LiftSubsystem;

import java.util.function.DoubleSupplier;

import edu.wpi.first.wpilibj2.command.CommandBase;

public class RotateArmCommand extends CommandBase {
    public final ArmSubsystem m_ArmSubsystem;
    private final DoubleSupplier m_powerSupplier;

    public RotateArmCommand(ArmSubsystem armSubsystem, DoubleSupplier supplier) {
        m_ArmSubsystem = armSubsystem;
        m_powerSupplier = supplier;

        addRequirements(m_ArmSubsystem);
    }

    @Override
    public void execute() {
        m_ArmSubsystem.setPower(m_powerSupplier.getAsDouble());
    }

}