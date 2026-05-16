package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StopCommand implements Command {
    
    // DEFAULT CONSTRUCTOR
    public StopCommand() {}
    
    @Override
    public CommandType getType() {
        return CommandType.STOP;
    }
    
    @Override
    public String toString() {
        return "StopCommand{}";
    }
}