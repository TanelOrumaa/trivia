package command;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.NoSuchElementException;

public class CommandQueue {

    static final Logger LOG = LoggerFactory.getLogger(CommandQueue.class);

    LinkedList<Command> commands;

    public CommandQueue() {
        commands = new LinkedList<>();
    }

    /**
     * Checks if there are commands in the list. If there are, compares time with servertime and if the command
     * should be run, returns the command.
     * @param servertime Long with current servertime.
     * @return Command object if the first command should be executed, null otherwise.
     */
    public Command poll(Long servertime) {
        try {
            Command command = commands.getFirst();
            if (command.time <= servertime) {
                return commands.removeFirst();
            }
            return null;
        } catch (NoSuchElementException e) {
            return null;
        }
    }

    /**
     * If the queue is empty, add the command to the first spot, otherwise add it to correct position in the list
     * so that the list is ordered by time.
     * @param command Command object.
     */
    public void add(Command command) {
        if (commands.size() == 0) {
            commands.add(command);
        } else {
            for (int i = 0; i < commands.size(); i++) {
                if (commands.get(i).time > command.time) {
                    commands.add(i, command);
                    return;
                }
            }
            commands.add(command);
        }
    }

    public int size() {
        return commands.size();
    }

    @Override
    public String toString() {
        return "CommandQueue has " + this.size() + " commands:" + commands;
    }

    public void clear() {
        commands.clear();
    }
}
