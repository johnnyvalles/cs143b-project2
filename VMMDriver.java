import java.util.Scanner;

public class VMMDriver {
    public static void main(String[] args) {
        if (args.length == 2 && args[0].equals("-i")) {
            VMMDriver.VMMInteractiveMode(args[1]);
        } else if (args.length == 2) {
            VMMDriver.VMMGradingMode(args[0], args[1]);
        } else {
            System.err.println("usage: java VMMDriver /path/to/init-file.txt /path/to/input-file.txt");
            System.err.println("usage: java VMMDriver -i /path/to/init-file.txt");
            System.exit(1);
        }
    }
    
    private static void VMMGradingMode(String initFileName, String inputFileName) {
        VirtualMemoryManager vmm = new VirtualMemoryManager();
        vmm.init(initFileName);
        vmm.translateFromFile(inputFileName);
    }
    
    private static void VMMInteractiveMode(String initFileName) {
        VirtualMemoryManager vmm = new VirtualMemoryManager();
        Scanner in = new Scanner(System.in);
        String shellPrompt = "[vmm-driver]$ ";
        String line;
        String[] tokens;
        
        vmm.init(initFileName);
        
        System.out.print(shellPrompt);
        while (in.hasNextLine()) {
            line = in.nextLine();
            tokens = line.split("\\s+");
            
            if (tokens.length > 0) {
                if (tokens.length == 2 && tokens[0].equals("va")) {
                    int virtualAddress = Integer.parseInt(tokens[1]);
                    System.out.println("va " + virtualAddress);
                }
            }
            
            System.out.print(shellPrompt);
        }

        in.close();
    }
}