
# CS 143B Project 2: Virtual Memory
This project implements a virtual memory (VM) system that utilizes segmentation and paging. A simulated main memory and disk are also used. The VM accepts virtual addresses (VAs) and translates them to physical addresses (PAs) according to the contents of the segment table (ST) and page tables (PTs). For simplicity, the system supports exactly one process. Thus, the contents of main memory correspond to the data used by the process along with memory management data structures (i.e., ST and PTs). Additionally, the system 

Main memory is organized as an array of 524,288 integers (2MB), each corresponding to an addressable word. To facilitate paging, main memory is divided into 1024 frames of 512 words. Frames 0 and 1 are reserved for the process' segment table. Segment table entries (STEs) require two words. Consequently, the segment table accommodates 512 STEs.

The disk is organized as a two-dimensional array of integers corresponding to 512 word blocks. For example, the disk may be represented as `D[B][512]`, where `B` is the block number to be accessed. The disk may only be accessed one block at a time (e.g., the  `read_block(...)` routine) and is not word addressable.

Since the system employs demand paging, pages and PTs may not be resident in main memory when an address translation begins. In such cases, a page fault is raised, the corresponding page or PT is read from disk into main memory, the ST or PT is updated to reflect the now resident data, and the address translation continues (assuming a valid VA).


# Building & Running the Virtual Memory System
Running the manager using the instructor provided input files:
```sh
javac *.java && java VMMDriver /path/to/init-file.txt /path/to/input-file.txt
```
Running the manager and interacting with the shell by providing your own VAs:
```sh
javac *.java && java VMMDriver -i /path/to/init-file.txt
```