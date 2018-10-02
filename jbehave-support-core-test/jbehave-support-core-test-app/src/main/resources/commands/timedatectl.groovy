package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext

class timedatectl {
    @Usage("Dummy timedatectl command always returning empty string")
    @Command
    def main(InvocationContext context) {
        return ""
    }
}
