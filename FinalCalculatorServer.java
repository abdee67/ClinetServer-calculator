import java.io.*;
import java.io.IOException;
import java.net.*;

public class CalculatorServer {
    public static void main(String[] args) {
        try {
            int serverPort = 7896;
            ServerSocket serverSocket = new ServerSocket(serverPort);
            System.out.println("Calculator Server is running...");

            while (true) {
                Socket clientSocket = serverSocket.accept();
                new CalculatorServerThread(clientSocket).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class CalculatorServerThread extends Thread {
    private Socket clientSocket;
    private DataInputStream in;
    private DataOutputStream out;

    public CalculatorServerThread(Socket socket) {
        this.clientSocket = socket;
        try {
            in = new DataInputStream(clientSocket.getInputStream());
            out = new DataOutputStream(clientSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            while (true) {
                String expression = in.readUTF();

                if (expression.equals("exit")) {
                    break;
                }

                double result = evaluateExpression(expression);
                out.writeDouble(result);
            }

            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private double evaluateExpression(String expression) {
        try {
            // Assuming the expression is a simple calculation, split it into operands and operator
            String[] tokens = expression.split(" ");
            double operand1 = Double.parseDouble(tokens[0]);
            double operand2 = Double.parseDouble(tokens[2]);
            char operator = tokens[1].charAt(0);

            double result = 0;

            // Perform arithmetic operations based on the operator
            switch (operator) {
                case '+':
                    result = operand1 + operand2;
                    break;
                case '-':
                    result = operand1 - operand2;
                    break;
                case '*':
                    result = operand1 * operand2;
                    break;
                case '/':
                    if (operand2 != 0) {
                        result = operand1 / operand2;
                    } else {
                        System.out.println("Division by zero is not allowed.");
                    }
                    break;
            }

            return result;
        } catch (NumberFormatException e) {
    System.out.println("Invalid numeric value in the expression: " + e.getMessage());
}
catch (ArrayIndexOutOfBoundsException e) {
    System.out.println("Invalid expression format: Not enough operands or operators.");
}
            return 0;
        }
    }

