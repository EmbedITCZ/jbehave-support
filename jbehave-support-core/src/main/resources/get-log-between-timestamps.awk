#!/usr/bin/awk -f

BEGIN {
  startTimestamp = ARGV[1];
  endTimestamp = ARGV[2];
  upperLimitTimestamp = ARGV[3];
  inputFile = ARGV[4];

  # delete non-file arguments
  delete ARGV[1];
  delete ARGV[2];
  delete ARGV[3];

  startLineNumber = -1;
  endLineNumber = -1;
}

$1 FS $2 >= startTimestamp && $1 FS $2 <= endTimestamp {
  if (startLineNumber == -1) {
    startLineNumber = NR;
  }
}

$1 FS $2 >= endTimestamp && $1 FS $2 <= upperLimitTimestamp {
  if (endLineNumber == -1) {
    endLineNumber = NR;
  }
}

END {
  # print startLineNumber;
  # print endLineNumber;

  # we did not find a start, so exit
  if (startLineNumber < 0) {
    exit 0;
  }

  # end line number is ocurrence of next timestamp after end timestamp, we want everything before
  endLineNumber--;

  # we did not find an end, so print everything until EOF
  if (endLineNumber < 0) {
    endLineNumber = NR;
  }

  # print log segment between start line number and end line number
  for (i = 0; i < startLineNumber; i++) {
    # use different pointer to inputFile than the current one
    getline line < inputFile;
  }

  for (i = 0; i <= endLineNumber - startLineNumber; i++) {
    print line;
    getline line < inputFile;
  }

  close(inputFile);
}
