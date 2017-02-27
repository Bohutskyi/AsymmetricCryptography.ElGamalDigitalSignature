import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("-sign")) {
                new DigitalSignature(args[1]).run();
            }
        } else if (args.length == 3) {
            if (args[0].equalsIgnoreCase("-check")) {
                System.out.println(DigitalSignature.isCorrect(args[1], args[2]) ? "Sign is right" : "Sign is not right");
            }
        }
//        Scanner scanner = new Scanner(System.in);
//        String buffer;
//        do {
//            System.out.println("1. To sign");
//            System.out.println("2. To check sign");
//            System.out.println("0. To quit");
//            buffer = scanner.nextLine();
//            if (buffer.equalsIgnoreCase("0")) {
//                break;
//            }
//            switch (buffer) {
//                case "1":
//                    System.out.print("Enter file name: ");
//                    new DigitalSignature(scanner.nextLine()).run();
//                    break;
//                case "2":
//                    System.out.print("Enter file name: ");
//                    System.out.println(DigitalSignature.isCorrect(scanner.nextLine()) ? "Sign is right" : "Sign is not right");
//                    break;
//                default:
//                    System.out.println("Invalid input. Try again");
//                    break;
//            }
//        } while (true);
        System.out.println("Goodbye");
    }

}
