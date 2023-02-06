import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        VirtualMemoryManager vmm = new VirtualMemoryManager();
        Scanner scanner;

        if (args.length < 1 || args.length > 2) {
            System.err.println("You must provide an init file & optional file containing virtual addresses.");
            System.exit(-1);
        }

        if (args.length == 2) {
            vmm.init(args[0]);
            vmm.translateFromFile(args[1]);
        }

        if (args.length == 1) {
            vmm.init(args[0]);
            scanner = new Scanner(System.in);
            int virtualAddress;
            while (scanner.hasNextInt()) {
                virtualAddress = scanner.nextInt();
                System.out.println("**********************************************");
                System.out.println("Virtual Address:  " + virtualAddress);
                System.out.println("Physical Address: " + vmm.translateVAtoPA(virtualAddress));
                System.out.println("**********************************************");
            }
        }
    }
}