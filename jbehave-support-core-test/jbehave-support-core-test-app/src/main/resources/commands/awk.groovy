package commands

import org.crsh.cli.Command
import org.crsh.cli.Usage
import org.crsh.command.InvocationContext

class awk {
    @Usage("Dummy awk command always returning foo bar")
    @Command
    def main(InvocationContext context) {
        return "some long string containing cdata in many Cdata forms." +
            "Such as correct one <![CDATA[1832300759061]]> and malformed <![[1832300759061]]> " +
            "and incomplete <![CDATA[ and duplicated correct one <![CDATA[1832300759061]]> with some additional information" +
            "Also some unexpected closing like ] and ]] also sharp ]]> "
    }
}
