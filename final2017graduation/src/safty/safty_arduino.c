/********************************************************
*							*
*	File : safty_arduino.c				*
*	Date : 2017/9/17				*
*	Make : JH, SH 					*
*							*
********************************************************/
 
#include <string.h> //for errno
#include "safty_arduino.h"
 
char device[]= "/dev/ttyACM0";
int fd;
unsigned long baud = 115200;
unsigned long time=0;
int BPM = 0;
int Breath = 0;
int Max_Breath = 0;
int One_Count = 0;
int convflag = 0; // 0 is bm , 1 is breath

extern UserState* userState;

 
void arduinosetup(){
 
  printf("[arduinossetup]ArduinoLoadd!\n");
  fflush(stdout);
 
  if ((fd = serialOpen (device, baud)) < 0){
    fprintf (stderr, "[ArduinoSetup]Unable to open serial device: %s\n", strerror (errno)) ;
    exit(1); //error
  }
 
  if (wiringPiSetup () == -1){
    fprintf (stdout, "[ArduinoSetup]Unable to start wiringPi: %s\n", strerror (errno)) ;
    exit(1); //error
  }
 
}

int conv(int* target, char append){
	int iRet = *target;
	iRet = iRet * 10;
	iRet = iRet + append - '0';
	return iRet;	
}

void* arduinoloop(void){
	while(1){
		if(millis()-time>=3000){
			serialPuts (fd, "Pong!\n");
			serialPutchar (fd, 65);
			time=millis();
		}

		if(serialDataAvail (fd)){
			char newChar = serialGetchar(fd);
			switch(newChar){
				case '.': 
					serialGetchar(fd);
					convflag = 0; 
					if(BPM < 200) userState->pulse = BPM;
					//if(BPM < 200) userState->pulse = 60 + BPM%20;
					if(userState->breath !=0) userState->breath = Breath;
					if(Max_Breath !=0) userState->max_Breath = Max_Breath;
					if(One_Count !=0) userState->one_Count = One_Count;
					//printf("arduino:%d %d %d %d\n", Breath, BPM, Max_Breath, One_Count);
					BPM = 0;
					Breath = 0;
					Max_Breath = 0;
					One_Count = 0;
					pthread_yield(NULL);
					break;
				case ',': 
					convflag++;
					break;
				default:
					if(convflag == 0){
						Breath = conv(&Breath, newChar);	
					}
					else if(convflag == 1){
						BPM = conv(&BPM, newChar);
					}
					else if(convflag == 2){
						Max_Breath = conv(&Max_Breath, newChar);
					}
					else if(convflag == 3){
						One_Count = conv(&One_Count, newChar);
					}
					else{
						printf("[conv]UnExpected case\n");
					}
					break;
			} 
			fflush(stdout);
		}
	}
}
