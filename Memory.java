import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Memory {
	// initializing an array to store 2000 numbers
	static int memArr[] = new int[2000];

	public static void main(String[] args) {
		try{
			// reading the input file from process.exec(java Memory <file>)
			Scanner fscan = new Scanner(new File(args[0]));
			int i = 0; // index starting at 0
			while (fscan.hasNextLine()) {
				String ln = fscan.nextLine();
				if (ln.length() == 0 || ln.charAt(0) < '0' || ln.charAt(0) > '9') {
					
					if (ln.length() != 0 && ln.charAt(0) == '.')
						i = Integer.parseInt(ln.substring(1));
					continue;
				}
				// loading instructions from file into memArr
				int instruction = Integer.parseInt(ln.split(" ")[0]);
				memArr[i++] = instruction;
			}
			// closing the file
			fscan.close();

		} catch (FileNotFoundException e) {
			System.exit(1);
		}

		// reading CPU's request for read/write privileges to Memory
		Scanner CPUReqReader = new Scanner(System.in);
		while (CPUReqReader.hasNextLine()) {
			String[] ln = CPUReqReader.nextLine().split(",");
			System.err.println(ln);
			switch (ln[0]) {
				// case for reading to Memory
				case "read":
					System.out.println(memArr[Integer.parseInt(ln[1])]);
					break;
				// case for writing to Memory
				case "write":
					memArr[Integer.parseInt(ln[1])] = Integer.parseInt(ln[2]);
					break;
			}
		}
		CPUReqReader.close();
	}
}
