#include <mbed.h>
#include <EthernetInterface.h>
#include <rtos.h>


int main() {
    DigitalIn sw(SW2);
    EthernetInterface eth;
    UDPSocket udp;
    char buffer[256];

    printf("conecting \n");
    eth.connect();
    const char *ip = eth.get_ip_address();
    printf("IP address is: %s\n", ip ? ip : "No IP");

    udp.open( &eth);
    SocketAddress source;
    /* YOU will have to hardwire the IP address in here */
    SocketAddress server("192.168.70.36",4445);

    while(1){
        printf("press sw2\n");
        while( sw.read() ) /* 1 is not pressed */ ;

        printf("sending empty packet to '%d' at '%s'\n",  server.get_port(), server.get_ip_address() );
        nsapi_size_or_error_t r = udp.sendto( server, buffer, sizeof(buffer));
        printf("sent %d bytes\n",r);

        printf("recieveing...\n");
        int len = udp.recvfrom( &source, buffer, sizeof(buffer));
        buffer[len]='\0';
        printf("from %s\n", source.get_ip_address());
        printf("  at %d\n", source.get_port());
        printf("data %s\n", buffer);
        printf("----\n");
    }
}
