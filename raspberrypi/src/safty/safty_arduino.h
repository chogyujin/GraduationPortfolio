
/********************************************************
 *							*
 * File : safty_arduino.h				*
 * Date : 2017/9/17					*
 * Made by JH, SH					*
 *							*
 *******************************************************/

#ifndef _safty_arduino_H_
#define _safty_arduino_H_
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
#include <wiringSerial.h>
#include "types.h"

extern void arduinosetup();
extern void* arduinoloop(void);

#ifdef __cplusplus
}
#endif
#endif

