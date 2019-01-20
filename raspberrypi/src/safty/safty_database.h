
/*****************************************************************************
 *
 * File Name: safty_database.h
 *
 * file creation : 2017/08/16
 *
 *****************************************************************************/

#ifndef _safty_database_H_
#define _safty_database_H_
#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>
#include <stdlib.h>
#include <stdint.h>
#include <sys/types.h>
#include <unistd.h>
#include <errno.h>
#include "types.h"

extern int get_health(DriverState* _driverState);
extern int set_health(DriverState* _driverState);
extern int get_emergency(Emergency* _emergency);
extern int set_emergency(Emergency* _emergency);
extern int set_userstandard(UserStandard* _userstandard);
extern int get_userstandard(UserStandard* _userstandard);
extern int confirm_id(Id* _id);
extern int join_id(Id* _id);

#ifdef __cplusplus
}
#endif
#endif

