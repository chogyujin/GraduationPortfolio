
/*****************************************************************************
 *
 * File Name: safty_sensor.h
 *
 * file creation : 2017/7/31
 *
 *****************************************************************************/

#ifndef _farm_sensor_H_
#define _farm_sensor_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include <wiringPi.h>

#define MAXTIMINGS 85


#define PUMP	21 // BCM_GPIO 5
#define FAN	22 // BCM_GPIO 6
#define DCMOTOR 23 // BCM_GPIO 13
#define RGBLEDPOWER  24 //BCM_GPIO 19
#define RED	7 // sample code wrong
#define GREEN	8 // sample code wrong
#define BLUE	9 // sample code wrong

#define LIGHTSEN_OUT 2  //gpio27 - J13 connect
#define BUZCONTROL 28


extern int wiringPicheck();
extern int read_dht22_dat_temp();
extern int read_dht22_dat_humid();
extern int get_light_sensor(void);

extern int act_waterpump_active(const int _use);
extern int act_fan_active(const int _use);
extern int act_dcmotor_active(const int _use);
extern int act_led_active(const int _use);
extern int act_buzcontrol(const int _use);

extern int get_temp_functionality();
extern int get_humid_functionality();
extern int get_light_functionality();
extern int get_pump_functionality();
extern int get_fan_functionality();
extern int get_dcmotor_functionality();
extern int get_rgbled_functionality();

extern void clearAllActuator();

#ifdef __cplusplus
}
#endif
#endif

