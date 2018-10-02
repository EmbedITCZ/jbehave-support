package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext

class sed {
    @Usage("Dummy sed simulation always returning 2 4")
    @Command
    def main(InvocationContext context) {
        return "2 4"
    }
}
