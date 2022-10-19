import java.io.*;
import java.util.*;

public class CPU {

   public static class CPUWrapper {
      // default mode is user mode
      public int ac = 0; // accumulator register
      public int pc = 0; // program counter register
      public int ir = 0; // instruction register
      public int sp = 1000; // stack pointer register
      public int x = 0; // register x
      public int y = 0; // register y
      public int timer; // interrupt timer
      public int icounter = 0; // counts the instructions
      public String mode = "user";
      public PrintWriter writer; // writer variable that is in a text output stream
      public Scanner reader; // reader variable scans input from the reader
      public Scanner memErrs; // memory error scanner variable

      public CPUWrapper(Scanner reader, PrintWriter writer, int timer) {
         this.reader = reader;
         this.writer = writer;
         this.timer = timer;
      }

      // the fetch method reads the program counter and fetches the instruction to the ir
      public int fetch() {
         ir = read(pc);
         pc += 1;
         return ir;
      }

      // pushing in the value from the stack and writing in the data from the stack
      public void push(int data) {
         sp -=1;
         write(sp, data);
      }

      // popping off the value from the stack and adding space to it
      public int pop() {
         int val = read(sp);
         sp += 1;
         return val;
      }

      // kernelMode switches from user mode to kernel mode
      private void kernelMode() {
         mode = "kernel";
         int temp = sp;
         sp = 2000;
         push(temp);
         push(pc);
      }

      // debug method for memory
      public void memDebug() {
         while (memErrs.hasNextLine()) {
            System.out.println(memErrs.nextLine());
         }
      }

      // request to read from memory at that address
      public int read(int addr) {
         if(mode.equals("user") && addr >= 1000) {
            System.err.println("Memory violation: accessing system address " + addr + " in user mode ");
            System.exit(-1);
         }
         writer.printf("read,%d\n", addr);
         writer.flush();
         return Integer.parseInt(reader.nextLine());
      }

      // writing in the value at that address
      public void write(int addr, int val) {
         writer.printf("write,%d,%d\n", addr, val);
         writer.flush();
      }

      // 1 load value into ac
      void op1() {
           ir = fetch();
           ac = ir;
      }
      // 2 read from ir into ac
      void op2() {
           ir = fetch();
           ac = read(ir);
      }
      // 3 read value from address of ir, then read from that value into ac
      void op3() {
          ir = fetch();
          int val = read(ir);
          ac = read(val);
      }
      // 4 read from ir+x address into ac
      void op4() {
          ir = fetch();
          ac = read(ir+x);
      }
      // 5 read from ir+y address into ac
      void op5() {
          ir = fetch();
          ac = read(ir+y);
      }
      // 6 read from sp+x address into ac
      void op6() {
          ac = read(sp + x);
      }
      // 7 write ac to memory @ ir address
      void op7() {
          ir = fetch();
          write(ir,ac);
      }
      // 8 load random int [1,100] into ac
      void op8() {
          Random random = new Random();
          ac = random.nextInt(100) + 1;
      }
      // 9 if port = 1, print ac; else 2, print ac as char
      void op9() {
           ir = fetch();
           int port = ir;
           if(port == 1) System.out.print(ac);
           else if (port == 2) System.out.print((char)ac);
      }
      // 10 ac = ac + x
      void op10() {
          ac += x;
      } 
      // 11 ac = ac + y
      void op11() {
          ac += y;
      }
      // 12 ac = ac - x
      void op12() {
          ac -= x;
      }
      // 13 ac = ac - y
      void op13() {
          ac -= y;
      }
      // 14 copy ac into x
      void op14() {
          x = ac;
      }
      // 15 copy x into ac
      void op15() {
          ac = x;
      }
      // 16 copy ac into y
      void op16() {
          y = ac;
      }
      // 17 copy y into ac
      void op17() {
          ac = y;
      }
      // 18 copy ac into sp
      void op18() {
          sp = ac;
      }
      // 19 copy sp into ac
      void op19() {
          ac = sp;
      }
      // 20 jump to instruction
      void op20() {
         ir = fetch();
         pc = ir;
      }
      // 21 only jump to instruction if ac = 0
      void op21() {
         ir = fetch();
         if(ac==0) pc = ir;
      }
      // 22 only jump to instruction if ac != 0
      void op22() {
         ir = fetch();
         if(ac != 0) pc = ir;
      }
      // 23 push pc to stack then jump to instruction
      void op23() {
         ir = fetch();
         push(pc);
         pc = ir;
      }
      // 24 jump to return address off stack
      void op24() {
    	  pc = pop();
      }
      // 25 x = x+1
      void op25() {
    	  x++;
      }
      // 26 x = x-1
      void op26() {
    	  x--;
      }
      // 27 push ac to stack
      void op27() {
    	  push(ac);
      }
      // 28 pop ac off stack
      void op28() {
    	  ac = pop();
      }
      // 29 syscall switches mode
      void op29() {
         if(mode.equals("user")) {
             kernelMode();
             pc = 1500;
         }
      }
      // 30 restore user mode pc / sp
      void op30() {
           pc = pop();
           sp = pop();
           mode = "user";
      }
      // 50 exit
      void op50() {
	  System.exit(1);
      }

      // checking to see if there is an interrupt & if so, then change modes
      void checkInterruptTimer() {
            icounter++;
            if ((icounter % timer) == 0) {
               if (mode.equals("user")) {
                  icounter = 0;
                  kernelMode();
                  pc = 1000;
               }
            }
      }

      public void pLoop() {

         // while the processing loop is true
         while (true) {
            
            // fetch opcode
            ir = fetch();
	    //System.out.println("opcode " + ir);

            // determine & execute opcode 
	    if(ir==1) op1();
            else if(ir==2) op2();
            else if(ir==3) op3();
            else if(ir==4) op4();
            else if(ir==5) op5();
            else if(ir==6) op6();
            else if(ir==7) op7();
            else if(ir==8) op8();
            else if(ir==9) op9();
            else if(ir==10) op10();
            else if(ir==11) op11();
            else if(ir==12) op12();
            else if(ir==13) op13();
            else if(ir==14) op14();
            else if(ir==15) op15();
            else if(ir==16) op16();
            else if(ir==17) op17();
            else if(ir==18) op18();
            else if(ir==19) op19();
            else if(ir==20) op20();
            else if(ir==21) op21();
            else if(ir==22) op22();
            else if(ir==23) op23();
            else if(ir==24) op24();
            else if(ir==25) op25();
            else if(ir==26) op26();
            else if(ir==27) op27();
            else if(ir==28) op28();
            else if(ir==29) op29();
            else if(ir==30) op30();
            else if(ir==50) op50();
             
            // check for interrupt timer time-out (if it happens => switch mode)
	    checkInterruptTimer();
         }
      }
   }

   public static void main(String[] args) throws IllegalArgumentException {
      String cmdArr[] = { "java", "Memory", args[0] };
      if (args.length==0)
         throw new IllegalArgumentException("You forgot to put any arguments!");
      if (args.length==1)
         throw new IllegalArgumentException("You forgot to put in an interrupt timer value");

      try {
         // create process executing java Memory on the input file
         Process p = Runtime.getRuntime().exec(cmdArr);

         PrintWriter memwriter = new PrintWriter(p.getOutputStream());
         Scanner memreader = new Scanner(p.getInputStream());
         Scanner memErrs = new Scanner(p.getErrorStream());
         // run with specified interrupt timer (args[1])
         CPUWrapper cpu_obj = new CPUWrapper(memreader, memwriter, Integer.parseInt(args[1])); // cpu object
         cpu_obj.memErrs = memErrs;
         cpu_obj.pLoop();
      } catch (IOException t) {
         t.printStackTrace();
         System.exit(1);
      }
   }
}