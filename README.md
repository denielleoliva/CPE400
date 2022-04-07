# CPE400
<<<<<<< HEAD
@ryan was here

how to run
==========
mcs -out:output.exe *file*.cs
mono output.exe
mono output.exe *filepath* *#*

Ryan Do: Concurrency (see bottom)
    (3-28 works for 1 file at a time now make it x files at a time)
Jason do: checksum
Denielle: figured out how to send 1 at a time

buglist: 
double client problem
firewall problem? (maybe)

concurrency thoughts
====================
1. make multiple of (TcpClient client = new TcpClient("127.0.0.1", 1234);) <= these in an array
2. have a for loop that goes for file%connections + 1
3. have each connection send a file in the list


notes 4/7 - denielle:
- basic functions work
- file sending works
- heck yeah this took too long
- needs concurrency