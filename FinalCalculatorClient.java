import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.*;

public class CalculatorClient extends Frame implements ActionListener {
    TextField text;
    Panel panel;
    String button[] = { "7", "8", "9", "4", "5", "6", "1", "2", "3", "0", "+", "-", "*", "/", "=", "TAB", "BACK", "CL" };
    Button btn[] = new Button[button.length];
    Socket socket;
    DataInputStream in;
    DataOutputStream out;

    public CalculatorClient() {
        try {
            socket = new Socket("localhost", 7896);
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font f = new Font("Helvetica", Font.PLAIN, 20);

        text = new TextField(20);
        text.setFont(f);
        panel = new Panel();
        add(text, "North");
        add(panel, "Center");
        panel.setLayout(new GridLayout(6, 3));

        for (int i = 0; i < button.length; i++) {
            btn[i] = new Button(button[i]);
            btn[i].setFont(f);
            btn[i].setForeground(Color.white);
            btn[i].setBackground(Color.black);
            btn[i].addActionListener(this);
            panel.add(btn[i]);
        }

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent we) {
                try {
                    out.writeUTF("exit");
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.exit(0);
            }
        });
    }
   private void evaluateExpression(String expression) {
    try {
        out.writeUTF(expression);

        double result = in.readDouble();
        text.setText(String.valueOf(result));
    } catch (IOException e) {
        e.printStackTrace();
    } catch (NumberFormatException e) {
        String errorMessage = "Invalid numeric value in the expression: " + e.getMessage();
        text.setText(errorMessage);
    }
}



    public void actionPerformed(ActionEvent ae) {
        try {
            String str = ae.getActionCommand();

            if (str.equals("=")) {
                String expression = text.getText();
                out.writeUTF(expression);

                double result = in.readDouble();
                text.setText(String.valueOf(result));
            } else if (str.equals("CL")) {
                text.setText("");
            } else if (str.equals("TAB")) {
                String currentText = text.getText();
                text.setText(text.getText() + " ");
            } else if (str.equals("BACK")) {
                String currentText = text.getText();
                if (!currentText.isEmpty()) {
                    text.setText(currentText.substring(0, currentText.length() - 1));
                }
            } else {
                text.setText(text.getText() + str );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        CalculatorClient client = new CalculatorClient();
        client.setTitle("Calculator Client");
        client.setLocation(400, 100);
        client.setSize(400, 550);
        client.setBackground(Color.BLACK);
        client.setForeground(Color.gray);
        client.setVisible(true);
    }
}
