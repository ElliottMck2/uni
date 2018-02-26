import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

public class UDPSendText extends JFrame {
    static public final long serialVersionUID = 1L;
    InetSocketAddress destination_address;

    static MessagePanel message;

    public static void main( String[] args ) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UDPSendText();
            }
        });
    }

    public UDPSendText() {
        super("Send Text via UDP");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        JPanel content = new JPanel(    );
        content.setLayout( new BoxLayout( content, BoxLayout.Y_AXIS) );

        content.add( new SocketAddressPanel() );

        content.add( new MessagePanel() );

        this.setContentPane(content);
        this.pack();
        this.setVisible(true);

    }

    static class SocketAddressPanel extends JPanel {
        public static final long serialVersionUID = 1L;
        static public JTextField ip;
        static private JTextField port;

        static public InetSocketAddress getSocketAddress() {
            return new InetSocketAddress(
                            ip.getText(),
                            Integer.parseInt( port.getText() )
                        );
        }

        public SocketAddressPanel() {
            super( new FlowLayout(FlowLayout.LEFT, 5, 0) );
            setBorder( BorderFactory.createTitledBorder("Internet Socket Address") );

            add( new JLabel("IP:") );
            ip = new JTextField("192.168.", 12);
            add(ip);
            add( new JLabel(" port:") );
            port = new JTextField("65000",5);
            add(port);
        }

        static public void transmit(String message) {
            try{
                DatagramSocket network = new DatagramSocket();
                byte[] payload = message.getBytes();
                DatagramPacket datagram = new DatagramPacket(
                                                payload, payload.length,
                                                getSocketAddress()
                                        );
                network.send(datagram);
            }catch(Exception e){}
        }
    }

    class MessagePanel extends JPanel implements ActionListener {
        public static final long serialVersionUID = 1L;
        JButton action;
        JTextField message;
        public MessagePanel() {
            this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
            this.setBorder( BorderFactory.createTitledBorder("Message") );
            message = new JTextField("Put some message here to send",32);
            this.add(message);
            message.addActionListener(this);
            action = new JButton("send");
            Dimension s = message.getPreferredSize();
            Dimension b = action.getPreferredSize();
            b.setSize( b.getWidth() , s.getHeight() );
            action.setPreferredSize(b);
            action.addActionListener(this);
            this.add(action);
        }
        public void actionPerformed(ActionEvent e) {
            try {
                UDPSendText.SocketAddressPanel.transmit( message.getText() );
            } catch (Exception exception ) {
            }
        }
    }


}
