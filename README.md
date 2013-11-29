This is a simple akka project to test remote actor creation.

A Doubler actor D receives a number n, and uses n Incrementer (I1, I2, ... , In) actors to compute 2n :)

Here is how it works:
- an actor A sends a message to D with an Integer n
- D creates n Incrementers (I1, I2, ... , In)
- each Incrementer has a "next" actor, to which it will send the result of its computation
-- the next of In is A
-- the next of I(n-1) is In
-- ...
-- the next of I1 is I2
- D sends n to I1
- I1 sends n+1 to I2
- I2 sends n+2 to I3
- ...
- In sends n+n to A

Note: D knows that there m machines in the system (m akka nodes), and so it uses all of them by creating Incrementer on every node.
Incrementer Ii is created on node (i - 1) % nodes.size();