LoGrep
======

logrep is a log keyphrase searching utility. Because logs naturally have some line content scattered with newlines (`\n`) for example in stacktraces, logrep concatenates those into one `String` object to provide context to the keyword.

###Use

Running the command is as easy as this:

```sh
./logrep.sh ERROR *.log
```

The above command will search all files in the current directory for the keyword ERROR and print them to standard out.  This can be redirected to a file via native piping.