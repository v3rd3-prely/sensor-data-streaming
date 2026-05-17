package ro.tuiasi.ac.common;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommandTest {

    @Test
    void moveCommandShouldStoreDirectionAndDistance() {
        MoveCommand command = new MoveCommand(MoveDirection.FRONT, 50.0f);

        assertEquals(CommandType.MOVE, command.getType());
        assertEquals(MoveDirection.FRONT, command.getDirection());
        assertEquals(50.0f, command.getDistance());
    }

    @Test
    void moveCommandSettersShouldWork() {
        MoveCommand command = new MoveCommand();

        command.setDirection(MoveDirection.LEFT);
        command.setDistance(30.0f);

        assertEquals(MoveDirection.LEFT, command.getDirection());
        assertEquals(30.0f, command.getDistance());
    }

    @Test
    void rotateCommandShouldStoreOrientationAndGrade() {
        RotateCommand command = new RotateCommand(RotateDirection.RIGHT, 90.0f);

        assertEquals(CommandType.ROTATE, command.getType());
        assertEquals(RotateDirection.RIGHT, command.getOrientation());
        assertEquals(90.0f, command.getGrade());
    }

    @Test
    void rotateCommandSettersShouldWork() {
        RotateCommand command = new RotateCommand();

        command.setOrientation(RotateDirection.LEFT);
        command.setGrade(45.0f);

        assertEquals(RotateDirection.LEFT, command.getOrientation());
        assertEquals(45.0f, command.getGrade());
    }

    @Test
    void stopCommandShouldReturnStopType() {
        StopCommand command = new StopCommand();

        assertEquals(CommandType.STOP, command.getType());
        assertEquals("StopCommand{}", command.toString());
    }

    @Test
    void startCommandShouldReturnStartType() {
        StartCommand command = new StartCommand();

        assertEquals(CommandType.START, command.getType());
        assertEquals("StartCommand{}", command.toString());
    }
}