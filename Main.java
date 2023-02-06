public class Main {
    public static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Missing program arguments.");
            System.err.println("You must provide an init file & file containing virtual addresses.");
            System.exit(-1);
        }

        String initFile = args[0];
        String vaFile = args[1];

        VirtualMemoryManager vmm = new VirtualMemoryManager();
        vmm.init(initFile);
        vmm.translateFromFile(vaFile);
    }
}
