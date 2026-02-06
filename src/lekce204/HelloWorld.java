
package lekce204;

import javax.swing.JButton;
import javax.swing.JFrame;

public class HelloWorld {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Hello World!");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JButton button = new JButton("click me");
        frame.add(button);
        frame.setSize(400,300);
        frame.setVisible(true);
    }
  
}
