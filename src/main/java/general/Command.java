package general;

public class Command {

    public int code;
    public String[] args;
    public long time;

    public Command(int code, String[] args, long time) {
        this.code = code;
        this.args = args;
        this.time = time;
    }

    public Command(int code, long time) {
        new Command(code, null, time);
    }
}
