/* $Revision: 1.8.4.1 $ */
/*
 *	engdemo.c
 *
 *	This is a simple program that illustrates how to call the MATLAB
 *	Engine functions from a C program.
 *
 * Copyright 1984-2003 The MathWorks, Inc.
 * All rights reserved
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
#include "engine.h"
#define  BUFSIZE 256

#include <linux/input.h>

#define MOUSEFILE "/dev/input/event5"

int main()

{
	Engine *ep;
	mxArray *y = NULL, *result = NULL,*x = NULL;
	char buffer[BUFSIZE+1];
	int time[100000];

	/*
	 * Start the MATLAB engine locally by executing the string
	 * "matlab"
	 *
	 * To start the session on a remote host, use the name of
	 * the host as the string rather than \0
	 *
	 * For more complicated cases, use any string with whitespace,
	 * and that string will be executed literally to start MATLAB
	 */
	if (!(ep = engOpen("\0"))) {
		fprintf(stderr, "\nCan't start MATLAB engine\n");
		return EXIT_FAILURE;
	}

	/*
	 * PART I
	 *
	 * For the first half of this demonstration, we will send data
	 * to MATLAB, analyze the data, and plot the result.
	 */

	/* 
	 * Create a variable for our data
	 */
	y = mxCreateDoubleMatrix(1, 100000, mxREAL);
    x = mxCreateDoubleMatrix(1, 100000, mxREAL);
	memcpy((void *)mxGetPr(y), (void *)time, sizeof(time));
    memcpy((void *)mxGetPr(x), (void *)time, sizeof(time));
   
    
	/*
	 * Place the variable T into the MATLAB workspace
	 */
	engPutVariable(ep, "y", y);
    engPutVariable(ep, "x", x);
   
    /*
	 * use fgetc() to make sure that we pause long enough to be
	 * able to see the plot
	 */
	printf("Hit return to start aquiring \n\n");
	fgetc(stdin);
    
	int fd;
	struct input_event ie;


	if((fd = open(MOUSEFILE, O_RDONLY)) == -1) {
		perror("opening device");
		exit(EXIT_FAILURE);
	}
    
    
    
    double m=0;
    
     while pressure > 1 
    
	while(read(fd, &ie, sizeof(struct input_event))) {

        if(ie.type==2){
             m=ie.value;
            if(ie.code==0){
               
                engEvalString(ep, "y=[y m];");      
            }
            if(ie.code==1){
        
                engEvalString(ep, "x=[x m];");  
            }
      
        }
		printf("time %ld . %06ld \t type %d \t code %d \t value %d \n",
		       ie.time.tv_sec, ie.time.tv_usec, ie.type, ie.code, ie.value);
        
        
        /*
	 * Plot the result
	 */
    engEvalString(ep, "figure(1)");
	engEvalString(ep, "plot(y);");
	engEvalString(ep, "title('Pressure');");
	engEvalString(ep, "xlabel('Time');");
	engEvalString(ep, "ylabel('Pressure (y)');");
    engEvalString(ep, "hold on;");
    engEvalString(ep, "drawnow;");

        
        
	}

	
	
	/*
	 * We're done for Part I! Free memory, close MATLAB engine.
	 */
	printf("Done.\n");
	mxDestroyArray(y);
    mxDestroyArray(x);
	engEvalString(ep, "close;");

	
	return EXIT_SUCCESS;
}







