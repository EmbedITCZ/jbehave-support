package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext

class grep {
    @Usage("Dummy grep command always returning Europe/Prague")
    @Command
    def main(InvocationContext context) {
        return "Europe/Prague"
    }
}
