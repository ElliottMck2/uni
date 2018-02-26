#include <mbed.h>
#include <EthernetInterface.h>
#include <rtos.h>

#include <C12832.h>
char buffer[1024]; /* 1k bytes */

DigitalOut LED[] = { /* initialise to 1==off */
    DigitalOut(LED_RED,1),
    DigitalOut(LED_GREEN,1),
    DigitalOut(LED_BLUE,1)
};
char *name[] = {"red","green","blue"};

void setLED(char *which, char *state) {
    int id;
    int logic;
    for( id=0 ; id<3 ; id++) {
        /* test for name match */
        if( strcmp(which, name[id])==0 ) break; /* exit loop with current id */
    }
    if( strcmp(state,"on")==0 )logic=0;
    if( strcmp(state,"off")==0 )logic=1;
    LED[id].write(logic);
    printf("led %d at %d \n", id, logic);
}

void process_message(char *buffer) {
    /* message format is key:value\n */
    char *key = buffer;  /* key is just at beginning of line */
    char *value = strchr(buffer,':'); /* find ':' seperator */
    *value = '\0'; /* mark end of key string */
    value += 1;  /* move to start of value part */
    char *eol = strchr(value, '\n'); /* find end of line */
    *eol = '\0';
    setLED(key, value);
}

int main() {
    C12832 lcd(D11, D13, D12, D7, D10);
    EthernetInterface eth;
    UDPSocket udp;

    printf("conecting on DHCP\n");
    eth.connect();
    const char *ip = eth.get_ip_address();
    printf("IP address is: %s\n", ip ? ip : "No IP");

    udp.open( &eth);

    /* Listen for UDP on port 65000 */
    udp.bind(65000);

    printf("listening on 65000\n");

    while(1){
        SocketAddress source;
        int len = udp.recvfrom( &source, buffer, sizeof(buffer));
        buffer[len]='\0';
        printf("message recieved\n%s\n", buffer);
        process_message(buffer);
    }
}
