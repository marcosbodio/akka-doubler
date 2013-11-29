This is a simple akka project to test remote actor creation.

A Doubler actor D receives a number n, and uses n Incrementer (I1, I2, ... , In) actors to compute 2n :)

Here is how it works:
- an actor A sends a message to D with an Integer n
- D creates n Incrementers (I_1, I_2, ... , I_n)
- each Incrementer has a "next" actor, to which it will send the result of its computation
 - the next of I_n is A
 - the next of I_(n-1) is In
 - ...
 - the next of I_1 is I_2
- D sends n to I_1
- I_1 sends n+1 to I_2
- I_2 sends n+2 to I_3
- ...
- I_n sends n+n to A

Note: D knows that there m machines in the system (m akka nodes), and so it uses all of them by creating Incrementer on every node.
Incrementer I_i is created on node (i - 1) % nodes.size();