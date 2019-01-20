
/*****************************************************************************
 *
 * File Name: parser.h
 *
 * file creation : 2017/7/31
 * team : 2017 iot_smart home team
 *
 *****************************************************************************/
#ifndef _parser_H_
#define _parser_H_
#include "types.h"

extern int parse_data(char * _parameter, Data * _data);
extern int parse_data_str(char * _parameter, DataSTR * _data);
extern int parse_message(char * data, Message * msg);

#endif

