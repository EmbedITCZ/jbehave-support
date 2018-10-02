package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext

import java.time.ZonedDateTime

class date {
    @Usage("Dummy date command always returning current unix timestamp")
    @Command
    def main(InvocationContext context) {
        return ZonedDateTime.now().toEpochSecond()
    }
}
