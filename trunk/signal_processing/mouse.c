/*
 * mouse.c
 *
 *  Created on: Feb 19, 2011
 *      Author: mauro
 */

#include <stdlib.h>
#include <stdio.h>
#include <sys/ioctl.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <asm/types.h>
#include <fcntl.h>
#include <unistd.h>
#include <stdint.h>
#include <string.h>

#include <linux/input.h>

#define MOUSEFILE "/dev/input/event5"

#define test_bit(bit, array)    (array[bit/8] & (1<<(bit%8)))


int to_signed(n){
	return n - ((0x80 & n ) << 1);
}

 //  return n - ((0x80 & n) << 1)

int main()
{
	int fd;
	struct input_event ie;
	//uint8_t rel_bitmask[REL_MAX/8 + 1];


	if((fd = open(MOUSEFILE, O_RDONLY)) == -1) {
		perror("opening device");
		exit(EXIT_FAILURE);
	}

	while(read(fd, &ie, sizeof(struct input_event))) {




//					/* this means that the bit is set in the axes list */
//					printf("  Relative axis 0x%02x ", to_signed(ie.type));
//					switch ( ie.type)
//					{
//						case REL_X :
//							printf(" (X Axis)\n");
//							break;
//						case REL_Y :
//							printf(" (Y Axis)\n");
//							break;
//						case REL_Z :
//							printf(" (Z Axis)\n");
//							break;
//						case REL_HWHEEL :
//							printf(" (Horizontal Wheel)\n");
//							break;
//						case REL_DIAL :
//							printf(" (Dial)\n");
//							break;
//						case REL_WHEEL :
//							printf(" (Vertical Wheel)\n");
//							break;
//						case REL_MISC :
//							printf(" (Miscellaneous)\n");
//							break;
//						default:
//							printf(" (Unknown relative feature)\n");
//					}

			//		printf(" type %d \t code %d \t value %d \n",
				//			ie.type, ie.code, ie.value);

		printf("time %ld . %06ld \t type %d \t code %d \t value %d \n",
		       ie.time.tv_sec, ie.time.tv_usec, ie.type, ie.code, ie.value);
	}

	return 0;
}
