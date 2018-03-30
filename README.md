# CPUsim

Andrew Szoke  
Fall 2017

---

## Project Description

This project is a Java-based simulation of a computer processor, with its associated modules and logic blocks. Three sample programs are provided in the form of "test" text files, which are loaded into the program through the console. Other custom programs may be created and run through the program.

## Instruction Encoding

The following table lists the operations supported by the processor and their instruction encodings. Variables denoted by `#VALUE` are modified IEEE 754 floating point numbers with an 8-bit biased exponent and a mantissa whose length fills the remainder of the 32-bit instruction. Instructions must be encoded by hand before being fed into the program.

Opcode | Instruction Name | Format | Meaning
---|---|----|----
00001 | Set | SET Ri, #VALUE | Ri ← #VALUE
00010 | Get | GET Ri | Return Ri
00011 | Move | MOV Ri, Rj | Ri ← Rj
00100 | Add | ADD Ri, Rj, Rk | Ri ← Rj + Rk
00101 | Subtract | SUB Ri, Rj, Rk | Ri ← Rj - Rk
00110 | Negate | NEG Ri, Rj | Ri ← -Rj
00111 | Multiply | MULT Ri, Rj, Rk | Ri ← Rj \* Rk
01000 | Divide | DIV Ri, Rj, Rk | Ri ← Rj / Rk
01001 | Floor | FLR Ri, Rj | Ri ← floor(Rj)
01010 | Ceiling | CEIL Ri, Rj | Ri ← ceil(Rj)
01011 | Round | RND Ri, Rj | Ri ← round(Rj)
01100 | Absolute Val | ABS Ri, Rj | Ri ← \|Rj\|
01101 | Invert | INV Ri, Rj | Ri ← 1 / Rj
01110 | Min | MIN Ri, Rj, Rk | Ri ← min(Rj, Rk)
01111 | Max | MAX Ri, Rj, Rk | Ri ← max(Rj, Rk)
10000 | Power | PWR Ri, Rj, #VALUE | Ri ← Rj^(#VALUE)
10001 | Sine | SIN Ri, Rj | Ri ← sin(Rj)
10010 | Cosine | COS Ri, Rj | Ri ← cos(Rj)
10011 | Tangent | TAN Ri, Rj | Ri ← tan(Rj)
10100 | Exponential | EXP Ri, Rj | Ri ← e^(Rj)
10101 | Natural Log | LOG Ri, RjE | Ri ← ln(Rj)
10110 | Square Root | SQRT Ri, Rj | Ri ← √Rk
10111 | No Operation | NOP | —
11000 | Add Immediate | ADDI Ri, Rj, #VALUE | Ri ← Rj + #VALUE
11001 | Subtract Imm. | SUBI Ri, Rj, #VALUE | Ri ← Rj - #VALUE
11010 | Multiply Imm. | MULTI Ri, Rj, #VALUE | Ri ← Rj \* #VALUE
11011 | Divide Imm. | DIVI Ri, Rj, #VALUE | Ri ← Rj / #VALUE

## Setup

To run the program, simply compile the Java classes and run the Driver class. The class will then ask you for the filename of the program you wish to run.

## Test File Descriptions

* `test1.txt`: Computes the force (in Newtons) required to move a 100 kg object up a 30 degree ramp at constant speed.
* `test2.txt`: Finds the gravitational force of two masses (2.3 kg and 5.7 kg) separated by a distance of 13.2 m.
* `test3.txt`: Computes the quadratic formula for a = 2, b = 5, and c = 2.
