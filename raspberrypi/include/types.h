/************************************************************************
 *									*
 * File Name: types.h							*
 *									*
 * file creation : 2017/7/31						*
 * team : 2017 iot_smart home graduation team				*
 * made by JS								*
 * 2017/8 - clean up, redefine to iotsafe				*
 * 2017/9/18 make new section to UserState for heartbeat, breath	*
 *									*	
 ***********************************************************************/

#ifndef _types_H_
#define _types_H_

#define TYPE_TRUE   1
#define TYPE_FALSE  0
#define ERROR_PARSE -1
#define ERROR_NULL  -2
#define UCBUFFMAX 2000

enum APP_TYPE { DRIVE };

//messages 
#define GET_STATUS      0x101
#define GET_HEALTH	0x102
#define SET_HEALTH	0x103
#define GET_HISTORY     0x104
#define CONFIRM_ID	0x105
#define JOIN_ID		0x106
#define GET_PHONE_NUM	0x107
#define SET_PHONE_NUM	0x108
#define EMERGENCY	0x109
#define FAN_ACT		0x110
#define UPDATE_USER	0x111

#define NORMAL		0x200
#define NOTDRIVING 	0x201
#define LOWBPM	 	0x202
#define NOTINCHAIR	0x203
#define HEADTOOFAR 	0x204
#define WEEKBREATH	0x205
#define EYECLOSED	0x206
#define LOWBREATH	0x207
#define HEADFAR		0x208
#define NOBREATH	0x209

typedef struct _Data
{
    int _data1;
    int _data2;
    int _data3;
    int _data4;
    int _data5;
    int _data6;
}Data;

typedef struct _DataSTR
{
    char _data1[16];
    char _data2[16];
    char _data3[16];
    char _data4[16];
    char _data5[16];
    char _data6[16];
}DataSTR;


typedef struct _Message
{
    enum APP_TYPE _apptype;
    unsigned int _command;
    char      _data[128];
}Message;

typedef struct _Id
{
	char id[20];
	char password[20];
}Id;


typedef struct _UserState
{	
	char id[20];
        float temperature;
        float humid;
        int light;
        int motion;
        int near; //0 : normal
        int ultrasonic;
	int pulse;
	int breath;
	int max_Breath;
	int one_Count;
} UserState;

typedef struct _UserStandard
{
	char id[20];
	int heightOfEye;
	int awayFromChair;
	int normalBPM;
	int diffBPM;
	int normalBreath;
	int diffBreath;
	int maxBreath;
} UserStandard;

typedef struct _DriverState
{
	char id[20];
        char name[20];
        char age[10];
        char sex[10];
        char disease[20];
} DriverState;

typedef struct _Emergency
{
	char id[20];
        char name[20];
        char number[20];
        char relation[10];
}Emergency;

typedef struct _Actuator
{
        int use_pump;
        int use_fan;
        int use_dcmotor;
        int use_led;
	int use_buz;
}Actuator;


typedef struct _eyeResult
{
	int height;
	int width;
	int y;
	int x;
}EyeResult;

typedef struct _faceResult
{
	int height;
	int width;
	int y;
	int x;
	EyeResult eyeResult[2];
}FaceResult;
#endif

