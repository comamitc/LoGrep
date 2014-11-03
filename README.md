LoGrep
======

logrep is a log keyphrase searching utility. Because logs naturally have some line content scattered with newlines (`\n`) for example in stacktraces, logrep concatenates those into one `String` object to provide context to the keyword.

###Prerequisites

jre 1.7+
java in you environment path (windows)

###Use

Configuration for LoGrep is inline with (and dependent on) the configuration needed for my other library: [uniform](https://github.com/comamitc/uniform/blob/master/readme.md)

Running the command is as easy as this:

```sh
./logrep.sh ERROR *.log
```

The above command will search all files in the current directory recursively for files matching the pattern "*.log" and look for the keyword ERROR. It will print to stdout matching log lines in chronological order.  This can be redirected to a file via native piping.