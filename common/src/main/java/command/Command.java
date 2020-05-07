package command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

public class Command {

    static final Logger LOG = LoggerFactory.getLogger(Command.class);

    public int code;
    public String[] args;
    public long time;

    public Command(int code, String[] args, long time) {
        LOG.debug("Creating a new command with parameters: " + code + ", " + Arrays.toString(args) + ", " + time);
        this.code = code;
        this.args = args;
        this.time = time;
    }

    public Command(int code, long time) {
        this(code, null, time);
    }

    public Command(int code, String[] params) {
        this(code, params, System.currentTimeMillis());
    }

    @Override
    public String toString() {
        return "[" + code + " : " + Arrays.toString(args) + " : " + time + "]";
    }
}
