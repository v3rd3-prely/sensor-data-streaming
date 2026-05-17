package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RotateCommand implements Command {
    private RotateDirection orientation;
    private float grade;
    
    // DEFAULT CONSTRUCTOR
    public RotateCommand() {}
    
    // Parameterized constructor
    public RotateCommand(RotateDirection orient, float g) {
        this.orientation = orient;
        this.grade = g;
    }
    
    @Override
    public CommandType getType() {
        return CommandType.ROTATE;
    }
    
    // Getters
    public RotateDirection getOrientation() {
        return this.orientation;
    }
    
    public float getGrade() {
        return this.grade;
    }
    
    // Setters
    public void setOrientation(RotateDirection orientation) {
        this.orientation = orientation;
    }
    
    public void setGrade(float grade) {
        this.grade = grade;
    }
    
    @Override
    public String toString() {
        return String.format("RotateCommand{orientation=%s, grade=%.1f°}", orientation, grade);
    }
}