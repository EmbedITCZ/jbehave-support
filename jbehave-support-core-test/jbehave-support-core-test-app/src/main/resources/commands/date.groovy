package commands

import org.crsh.cli.Argument
import org.crsh.cli.Command
import org.crsh.cli.Required
import org.crsh.cli.Usage

import java.time.ZonedDateTime

class date {
    @Usage("Dummy date command returning current unix timestamp for +%s and -0600 for +%z")
    @Command
    def main(@Required @Argument String argument) {
        if (argument.equals("+%s")) {
            return ZonedDateTime.now().toEpochSecond()
        }
        if (argument.equals("+%z")) {
            return "-0600"
        }
    }
}
