package ro.tuiasi.ac.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

/**
 * Test class for command implementations including {@link MoveCommand},
 * {@link RotateCommand}, {@link StopCommand}, and {@link StartCommand}.
 * Verifies command type identification,
 * field storage, and setter functionality.
 *
 * @author Your Name
 */
public final class CommandTest {

    /** Test distance value for move commands. */
    private static final float TEST_MOVE_DISTANCE = 50.0f;

    /** Updated distance value for move command setter test. */
    private static final float UPDATED_MOVE_DISTANCE = 30.0f;

    /** Test angle value for rotate commands. */
    private static final float TEST_ROTATE_ANGLE = 90.0f;

    /** Updated angle value for rotate command setter test. */
    private static final float UPDATED_ROTATE_ANGLE = 45.0f;

    /**
     * Private constructor to prevent instantiation.
     * This is a test utility class.
     */
    private CommandTest() {
        // Test class - no instantiation needed
    }

    /**
     * Tests that MoveCommand correctly stores direction and distance values.
     * Verifies that the command type is MOVE and that constructor parameters
     * are properly stored.
     *
     * @see MoveCommand#MoveCommand(MoveDirection, float)
     * @see MoveCommand#getType()
     * @see MoveCommand#getDirection()
     * @see MoveCommand#getDistance()
     */
    @Test
    void moveCommandShouldStoreDirectionAndDistance() {
        MoveCommand command = new MoveCommand(
                MoveDirection.FRONT, TEST_MOVE_DISTANCE);

        assertEquals(CommandType.MOVE, command.getType());
        assertEquals(MoveDirection.FRONT, command.getDirection());
        assertEquals(TEST_MOVE_DISTANCE, command.getDistance());
    }

    /**
     * Tests that MoveCommand setters correctly update fields.
     * Verifies that direction and distance can be modified after construction.
     *
     * @see MoveCommand#setDirection(MoveDirection)
     * @see MoveCommand#setDistance(float)
     */
    @Test
    void moveCommandSettersShouldWork() {
        MoveCommand command = new MoveCommand();

        command.setDirection(MoveDirection.LEFT);
        command.setDistance(UPDATED_MOVE_DISTANCE);

        assertEquals(MoveDirection.LEFT, command.getDirection());
        assertEquals(UPDATED_MOVE_DISTANCE, command.getDistance());
    }

    /**
     * Tests that RotateCommand correctly stores orientation and grade values.
     * Verifies that the command type is ROTATE and that constructor parameters
     * are properly stored.
     *
     * @see RotateCommand#RotateCommand(RotateDirection, float)
     * @see RotateCommand#getType()
     * @see RotateCommand#getOrientation()
     * @see RotateCommand#getGrade()
     */
    @Test
    void rotateCommandShouldStoreOrientationAndGrade() {
        RotateCommand command = new RotateCommand(
                RotateDirection.RIGHT, TEST_ROTATE_ANGLE);

        assertEquals(CommandType.ROTATE, command.getType());
        assertEquals(RotateDirection.RIGHT, command.getOrientation());
        assertEquals(TEST_ROTATE_ANGLE, command.getGrade());
    }

    /**
     * Tests that RotateCommand setters correctly update fields.
     * Verifies that orientation and grade can be modified after construction.
     *
     * @see RotateCommand#setOrientation(RotateDirection)
     * @see RotateCommand#setGrade(float)
     */
    @Test
    void rotateCommandSettersShouldWork() {
        RotateCommand command = new RotateCommand();

        command.setOrientation(RotateDirection.LEFT);
        command.setGrade(UPDATED_ROTATE_ANGLE);

        assertEquals(RotateDirection.LEFT, command.getOrientation());
        assertEquals(UPDATED_ROTATE_ANGLE, command.getGrade());
    }

    /**
     * Tests that StopCommand correctly identifies its type.
     * Verifies that the command type is STOP and that toString returns
     * the expected string representation.
     *
     * @see StopCommand#getType()
     * @see StopCommand#toString()
     */
    @Test
    void stopCommandShouldReturnStopType() {
        StopCommand command = new StopCommand();

        assertEquals(CommandType.STOP, command.getType());
        assertEquals("StopCommand{}", command.toString());
    }

    /**
     * Tests that StartCommand correctly identifies its type.
     * Verifies that the command type is START and that toString returns
     * the expected string representation.
     *
     * @see StartCommand#getType()
     * @see StartCommand#toString()
     */
    @Test
    void startCommandShouldReturnStartType() {
        StartCommand command = new StartCommand();

        assertEquals(CommandType.START, command.getType());
        assertEquals("StartCommand{}", command.toString());
    }
}
