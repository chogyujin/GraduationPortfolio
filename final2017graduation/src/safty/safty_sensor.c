
/************************************************
 *						*
 * File Name: safty_sensor.c			*
 *						*
 * file creation : 2017/7/31			*
 * made by KJ					*
 *						*
 ***********************************************/
#include "safty_sensor.h"
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <wiringPi.h>

#define RETRY 5
#define MOTION_IN 0 //gpio17
#define COLLISION 2 //gpio27 - J13 connect
#define trigPin 1   //gpio18
#define echoPin 29   //gpio21

int ret_humid, ret_temp;

static int DHTPIN = 7;
static int dht22_dat[5] = {0,0,0,0,0};

int get_temp_functionality()
{
	return 1;
}

int get_humid_functionality()
{
	return 1;
}

int get_light_functionality()
{
	return 1;
}

int get_pump_functionality()
{
	return 1;
}

int get_fan_functionality()
{
	return 1;
}

int get_dcmotor_functionality()
{
	return 1;
}

int get_rgbled_functionality()
{
	return 1;
}

int get_temperature_sensor()
{
	int received_temp;
	int _retry = RETRY;

	DHTPIN = 11;

	if (wiringPiSetup() == -1)
		exit(EXIT_FAILURE) ;
	
	if (setuid(getuid()) < 0)
	{
		perror("Dropping privileges failed\n");
		exit(EXIT_FAILURE);
	}
	while (read_dht22_dat_temp() == 0 && _retry--)
	{
		delay(500); // wait 1sec to refresh
	}
	received_temp = ret_temp ;
//	printf("Temperature = %d\n", received_temp);
	
	return received_temp;
}
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
int get_motiondetect()
{
	 if (wiringPiSetup () < 0) {
		fprintf (stderr, "Unable to setup wiringPi: %s\n", strerror (errno));
      		return -1;
	}
	pinMode(MOTION_IN,INPUT);

	if(digitalRead(MOTION_IN) == 1){
//		printf("Motion Detect!\n");
		return 1;
	}
	if(digitalRead(MOTION_IN) == 0){
//		printf("Not Move\n");
		return 0;
	}
}
int get_neardetect()
{
	 if (wiringPiSetup () < 0)
        {
                fprintf (stderr, "Unable to setup wiringPi: %s\n", strerror (errno));
                return -1;
        }
       	pinMode(COLLISION, INPUT);

        if(digitalRead(COLLISION) == 0){
//              printf("Carefull~~~~ oops \n");
		return 0;
		}
        if(digitalRead(COLLISION) == 1){
//              printf("Not Collioson... \n");
		return 1;
		}
}
int get_ultrasonic()
{
	int distance=0;
        int pulse = 0;

        long startTime;
        long travelTime;

        if(wiringPiSetup () == -1)
        {
                printf("Unable GPIO Setup");
                return 1;
        }

        pinMode (trigPin, OUTPUT);
        pinMode (echoPin, INPUT);

        digitalWrite (trigPin, LOW);
        usleep(2);
        digitalWrite (trigPin, HIGH);
        usleep(20);
        digitalWrite (trigPin, LOW);

        while(digitalRead(echoPin) == LOW);

        startTime = micros();

        while(digitalRead(echoPin) == HIGH);
        travelTime = micros() - startTime;

        distance = travelTime / 58;

//	printf( "Distance: %dcm\n", distance);

	return distance;
}

static uint8_t sizecvt(const int read)
{
  /* digitalRead() and friends from wiringpi are defined as returning a value
  < 256. However, they are returned as int() types. This is a safety function */

  if (read > 255 || read < 0)
  {
    printf("Invalid data from wiringPi library\n");
    exit(EXIT_FAILURE);
  }
  return (uint8_t)read;
}

int read_dht22_dat_temp()
{
  uint8_t laststate = HIGH;
  uint8_t counter = 0;
  uint8_t j = 0, i;

  dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;

  // pull pin down for 18 milliseconds
  pinMode(DHTPIN, OUTPUT);
  digitalWrite(DHTPIN, HIGH);
  delay(10);
  digitalWrite(DHTPIN, LOW);
  delay(18);
  // then pull it up for 40 microseconds
  digitalWrite(DHTPIN, HIGH);
  delayMicroseconds(40); 
  // prepar
  
  pinMode(DHTPIN, INPUT);

  // detect change and read data
  for ( i=0; i< MAXTIMINGS; i++) {
    counter = 0;
    while (sizecvt(digitalRead(DHTPIN)) == laststate) {
      counter++;
      delayMicroseconds(1);
      if (counter == 255) {
        break;
      }
    }
    laststate = sizecvt(digitalRead(DHTPIN));

    if (counter == 255) break;

    // ignore first 3 transitions
    if ((i >= 4) && (i%2 == 0)) {
      // shove each bit into the storage bytes
      dht22_dat[j/8] <<= 1;
      if (counter > 16)
        dht22_dat[j/8] |= 1;
      j++;
    }
  }

  // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
  // print it out if data is good
  if ((j >= 40) && 
      (dht22_dat[4] == ((dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3]) & 0xFF)) ) {
        float t, h;
		
        h = (float)dht22_dat[0] * 256 + (float)dht22_dat[1];
        h /= 10;
        t = (float)(dht22_dat[2] & 0x7F)* 256 + (float)dht22_dat[3];
        t /= 10.0;
        if ((dht22_dat[2] & 0x80) != 0)  t *= -1;
		
		ret_humid = (int)h;
		ret_temp = (int)t;
		//printf("Humidity = %.2f %% Temperature = %.2f *C \n", h, t );
		//printf("Humidity = %d Temperature = %d\n", ret_humid, ret_temp);
		
    return ret_temp;
  }
  else
  {
    //printf("Data not good, skip\n");
    return 0;
  }
}


int get_humidity_sensor()
{
	int received_humid;
	int _retry = RETRY;

	DHTPIN = 11;

	if (wiringPiSetup() == -1)
		exit(EXIT_FAILURE) ;
	
	if (setuid(getuid()) < 0)
	{
		perror("Dropping privileges failed\n");
		exit(EXIT_FAILURE);
	}

	while (read_dht22_dat_humid() == 0 && _retry--) 
	{
		delay(500); // wait 1sec to refresh
	}

	received_humid = ret_humid;
//	printf("Humidity = %d\n", received_humid);
	
	return received_humid;
}

int read_dht22_dat_humid()
{
	uint8_t laststate = HIGH;
	uint8_t counter = 0;
	uint8_t j = 0, i;

	dht22_dat[0] = dht22_dat[1] = dht22_dat[2] = dht22_dat[3] = dht22_dat[4] = 0;

	// pull pin down for 18 milliseconds
	pinMode(DHTPIN, OUTPUT);
	digitalWrite(DHTPIN, HIGH);
	delay(10);
	digitalWrite(DHTPIN, LOW);
	delay(18);
	// then pull it up for 40 microseconds
	digitalWrite(DHTPIN, HIGH);
	delayMicroseconds(40); 
	// prepare to read the pin
	pinMode(DHTPIN, INPUT);

	// detect change and read data
	for ( i=0; i< MAXTIMINGS; i++) 
	{
		counter = 0;
		while (sizecvt(digitalRead(DHTPIN)) == laststate) 
		{
			counter++;
			delayMicroseconds(1);
			if (counter == 255) {
			break;
		}
    }
    laststate = sizecvt(digitalRead(DHTPIN));

    if (counter == 255) break;

    // ignore first 3 transitions
    if ((i >= 4) && (i%2 == 0)) 
	{
		// shove each bit into the storage bytes
		dht22_dat[j/8] <<= 1;
		if (counter > 16)
			dht22_dat[j/8] |= 1;
		j++;
    }
}

  // check we read 40 bits (8bit x 5 ) + verify checksum in the last byte
  // print it out if data is good
	if ((j >= 40) && 
      (dht22_dat[4] == ((dht22_dat[0] + dht22_dat[1] + dht22_dat[2] + dht22_dat[3]) & 0xFF)) ) {
        float t, h;
		
        h = (float)dht22_dat[0] * 256 + (float)dht22_dat[1];
        h /= 10;
        t = (float)(dht22_dat[2] & 0x7F)* 256 + (float)dht22_dat[3];
        t /= 10.0;
        if ((dht22_dat[2] & 0x80) != 0)  t *= -1;
		
		ret_humid = (int)h;
		ret_temp = (int)t;
//		printf("Humidity = %.2f %% Temperature = %.2f *C \n", h, t );
//		printf("Humidity = %d Temperature = %d\n", ret_humid, ret_temp);
		
    return ret_humid;
	}
	else
	{
//		printf("Data not good, skip\n");
		return 0;
	}
}

int wiringPicheck(void)
{
	if (wiringPiSetup () == -1)
	{
		fprintf(stdout, "Unable to start wiringPi: %s\n", strerror(errno));
		return 1;
	}
	else
	{
		return 0;
	}
}

int get_light_sensor(void)
{
	// sets up the wiringPi library
	if (wiringPiSetup () < 0) 
	{
		fprintf (stderr, "Unable to setup wiringPi: %s\n", strerror (errno));
		return 1;
	}
	
	if(digitalRead(LIGHTSEN_OUT))	//day
		return 1;
	else //night
		return 0;

}

/*****************************************************************/
/*****sub devices control ***/
/*****************************************************************/

int act_waterpump_active(const int _use)
{
	if(wiringPicheck()) 
		printf("%s Fail (param :%d) \n", _use);

	pinMode (PUMP, OUTPUT);
	digitalWrite (PUMP, _use) ;
	
}

int act_fan_active(const int _use)
{
	if(wiringPicheck()) 
		printf("%s Fail (param :%d) \n", _use);

	pinMode (FAN, OUTPUT);
	digitalWrite (FAN, _use) ; 
	
}

int act_dcmotor_active(const int _use)
{
	if(wiringPicheck()) 
		printf("%s Fail (param :%d) \n", _use);

	pinMode (DCMOTOR, OUTPUT);
	digitalWrite(DCMOTOR, _use); 
}

int act_led_active(const int _use)
{
	//printf("set_rebled");
	if(wiringPicheck()) 
		printf("%s Fail (param :%d) \n", _use);

	pinMode(RGBLEDPOWER, OUTPUT);
	
	digitalWrite(RGBLEDPOWER, _use);
}

int act_buzcontrol(const int _use){
	if(wiringPicheck()) printf("[WiringPi]sensor check fail");
	
	pinMode(BUZCONTROL, OUTPUT);
	if(_use == 0 ){
		digitalWrite(BUZCONTROL, 0);
	}	
	else
	{
		digitalWrite(BUZCONTROL, 1);
	}
	return 0;
}

void clearAllActuator(){
	int _use = 0;
	pinMode (PUMP, OUTPUT);
	digitalWrite (PUMP, _use) ;
	pinMode (FAN, OUTPUT);
	digitalWrite (FAN, _use) ; 
	pinMode (DCMOTOR, OUTPUT);
	digitalWrite(DCMOTOR, _use); 
	pinMode(RGBLEDPOWER, OUTPUT);
	pinMode(RED, OUTPUT);
	pinMode(GREEN, OUTPUT);
	pinMode(GREEN, OUTPUT);
	pinMode(BLUE,OUTPUT);
	digitalWrite(RGBLEDPOWER, _use);
	digitalWrite(BLUE, _use); 
	pinMode(BUZCONTROL, OUTPUT);
	digitalWrite(BUZCONTROL, _use);

}
