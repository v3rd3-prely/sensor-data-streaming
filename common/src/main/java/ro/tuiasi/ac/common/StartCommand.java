package ro.tuiasi.ac.common;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class StartCommand implements Command {
    
    // DEFAULT CONSTRUCTOR
    public StartCommand() {}
    
    @Override
    public CommandType getType() {
        return CommandType.START;
    }
    
    @Override
    public String toString() {
        return "StartCommand{}";
    }
}