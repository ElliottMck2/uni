import javax.swing.*;
import javax.swing.text.*;
import java.awt.*;
import java.net.*;
import java.io.*;
import java.awt.event.*;

public class UDPCommandLED extends JFrame {
    static public final long serialVersionUID = 1L;
    InetSocketAddress destination_address;

    public static void main( String[] args ) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new UDPCommandLED();
            }
        });
    }

    public UDPCommandLED() {
        super("Send Text via UDP");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel content = new JPanel( );
        content.setLayout( new BoxLayout( content, BoxLayout.Y_AXIS) );

        content.add(  new SocketAddressPanel() );
        content.add( new LEDPanel("MBED"));
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

    class LEDPanel extends JPanel implements ItemListener {
        public static final long serialVersionUID = 1L;
        String[] leds = {"red", "green", "blue"};
        String title;
        public LEDPanel(String title) {
            this.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 0));
            this.setBorder( BorderFactory.createTitledBorder(title) );
            this.title = title;

            for(String d : leds){
                JCheckBox b = new JCheckBox(d);
                b.addItemListener(this);
                this.add(b);
            }
        }
        public void itemStateChanged(ItemEvent event) {
            JCheckBox source = (JCheckBox)event.getItem();
            StringBuffer message = new StringBuffer("");
            message.append(source.getText()+':');
            switch( event.getStateChange() ) {
                case ItemEvent.SELECTED:
                    message.append("on");
                    break;
                case ItemEvent.DESELECTED:
                    message.append("off");
                    break;
            }
            message.append('\n');
            UDPCommandLED.SocketAddressPanel.transmit( message.toString() );
        }
    }
}
