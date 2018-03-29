import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Driver {

	public static void main(String[] args) throws FileNotFoundException {
		Processor p = new Processor();
		String input;
		File f;
		Scanner s = new Scanner(System.in);
		System.out.print("Enter a filename (including .txt) to execute its program >> ");
		input = s.nextLine().trim();
		f = new File(input);
		while (!f.exists()) {
			System.out.print("\nERROR: File not found." +
							   "\nConfirm that the file is spelled correctly and is in the current directory." +
							   "\nEnter a filename (including .txt) to execute its program >> ");
			input = s.nextLine().trim();
			f = new File(input);
		}
		System.out.println();
		p.runProgram(f);
		System.out.println("\nPROGRAM COMPLETE");
	}
}