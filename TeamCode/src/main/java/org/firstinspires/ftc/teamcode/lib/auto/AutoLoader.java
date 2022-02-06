package org.firstinspires.ftc.teamcode.lib.auto;

import org.firstinspires.ftc.teamcode.lib.auto.commands.SetCommand;

import java.io.BufferedReader;
import java.io.FileReader;

import edu.megiddo.lions.Interpreter;
import edu.megiddo.lions.SyntaxException;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.SequentialCommandGroup;

public class AutoLoader {
    private Interpreter<Command> interpreter = new Interpreter<>();

    public AutoLoader() {
        interpreter.registerCommand("set", new SetCommand());
    }

    private Command load(String file) {
        StringBuilder sb = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            br.close();
        } catch (Exception ignored) {
            return null;
        }

        try {
            return new SequentialCommandGroup(interpreter.interpret(sb.toString()).toArray(new Command[0]));
        } catch (SyntaxException e) {
            return null;
        }
    }
}
