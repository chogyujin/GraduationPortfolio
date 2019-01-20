/****************************************
 *					*
 * File Name: safty_database.c		*
 *					*
 * file creation : 2017/08/16		*
 * made by KJ				*
 *					*
 ***************************************/
#include "iot_db.h"
#include "safty_query.h"
#include "safty_database.h"
#include <stdio.h>
#include <string.h>
#include <errno.h>
#include <stdlib.h>
#include <mysql.h>

extern int query_stat;
extern MYSQL *connection;

char _query[256] = {0};
int get_health(DriverState* _driverState)
{
	printf("call database : get_health\n");
	printf("driverState - id : %s\nname : %s\nage : %s\nsex : %s\ndisease : %s\n",_driverState->id,_driverState->name,_driverState->age,_driverState->sex,_driverState->disease);
	char temp[256] = {0};
        char temp2[32] = {0};
        sprintf(temp,"select name from tdrivervalue where id = '%s'",_driverState->id);
	iot_get_data_from_db(temp,_driverState->name);
	sprintf(temp,"select age from tdrivervalue where id='%s'",_driverState->id);
        iot_get_data_from_db(temp,_driverState->age);
	sprintf(temp,"select sex from tdrivervalue where id='%s'",_driverState->id);
	iot_get_data_from_db(temp,_driverState->sex);
	sprintf(temp,"select disease from tdrivervalue where id='%s'",_driverState->id);
        iot_get_data_from_db(temp,_driverState->disease);
//    1 : good, -1 : not good
	return 1;
}

int set_health(DriverState* _driverState)
{
	printf("call database : set_health\n");
	char temp[256] = {0};
// delete table before write
	sprintf(temp, "delete from tdrivervalue where id='%s'",_driverState->id);
	iot_send_query(temp);

        sprintf(temp, "insert into tdrivervalue values ('%s','%s','%s','%s','%s')",_driverState->id,_driverState->name,_driverState->age,_driverState->sex,_driverState->disease);
	int i = iot_insert_data_from_db(temp);
	if (i!=0) return -1;
	else return 1;
// _driverState init value
// _driverState > database(SQL) upload 
// return :
//	1 : good, -1 : not good
}
int get_emergency(Emergency* _emergency)
{
	printf("call database : get_emergency \n");
	char temp[256] = {0};
//	char temp2[64] = {0};
        sprintf(temp,"select name from temergencyvalue where id = '%s'",_emergency->id);
        iot_get_data_from_db(temp,_emergency->name);
	sprintf(temp,"select number from temergencyvalue where id='%s'",_emergency->id);
        iot_get_data_from_db(temp,_emergency->number);
        sprintf(temp,"select relation from temergencyvalue where id='%s'",_emergency->id);
        iot_get_data_from_db(temp,_emergency->relation);	
	return 1; 
}
int set_emergency(Emergency* _emergency)
{
	printf("call database : set_emergency\n");
	char temp[256] = {0};
	sprintf(temp, "delete from temergencyvalue where id='%s'", _emergency->id);
	iot_send_query(temp);

// printf("emergency id : %s\n",_emergency->id->id);
	sprintf(temp, "insert into temergencyvalue values ('%s','%s','%s','%s')",_emergency->id,_emergency->name,_emergency->number,_emergency->relation);
	int i = iot_insert_data_from_db(temp);
	
	if (i != 0) return -1;
	else return 1;
}
int get_userstandard(UserStandard* _userstandard)
{
	printf("call database : get_userStandard\n");
	char temp[256] = {0};
	char temp2[32] = {0};
	sprintf(temp,"select heightofeye from userstandard where id='%s'",_userstandard->id);
	iot_get_data_from_db(temp,temp2);
	_userstandard->heightOfEye = atoi(temp2);
	sprintf(temp,"select awayfromchair from userstandard where id='%s'",_userstandard->id);
        iot_get_data_from_db(temp,temp2);
	_userstandard->awayFromChair = atoi(temp2);
	sprintf(temp,"select normalbpm from userstandard where id='%s'",_userstandard->id);
        iot_get_data_from_db(temp,temp2);
	_userstandard->normalBPM = atoi(temp2);
	sprintf(temp,"select diffbpm from userstandard where id='%s'",_userstandard->id);
        iot_get_data_from_db(temp,temp2);
	_userstandard->diffBPM=atoi(temp2);
	sprintf(temp,"select normalbreath from userstandard where id='%s'",_userstandard->id);
        iot_get_data_from_db(temp,temp2);
	_userstandard->normalBreath = atoi(temp2);
	sprintf(temp,"select diffbreath from userstandard where id='%s'",_userstandard->id);
        iot_get_data_from_db(temp,temp2);
	_userstandard->diffBreath = atoi(temp2);
	sprintf(temp,"select maxbreath from userstandard where id='%s'",_userstandard->id);
        iot_get_data_from_db(temp,temp2);
	_userstandard->maxBreath = atoi(temp2);
	return 1;	
}
int set_userstandard(UserStandard* _userstandard)
{
	printf("call database : set_userstandard\n");
	char temp[256] = {0};
	sprintf(temp, "delete from userstandard where id='%s'",_userstandard->id);
	iot_send_query(temp);
	sprintf(temp,"insert into userstandard values ('%s','%d','%d','%d','%d','%d','%d','%d')",_userstandard->id,_userstandard->heightOfEye,_userstandard->awayFromChair,_userstandard->normalBPM,_userstandard->diffBPM,_userstandard->normalBreath,_userstandard->diffBreath,_userstandard->maxBreath);
	int i = iot_insert_data_from_db(temp);
	if(i !=0) return -1;
	else return 1;
}
int confirm_id(Id* _id)
{
	printf("call database : confirm_id\n");
	char temp[256] = {0};	
	char idconf[256] = {0};
	char passconf[256] = {0};
	int res;
//	sprintf(temp, "select 1 from information_schema.columnss where table_name = 'iotsafe' and table_name = 'tidvalues' and column_name = '%s'",_id->id);
	sprintf(temp, "select id from tidvalue where id = '%s'",_id->id);
	iot_get_data_from_db(temp,idconf);
	sprintf(temp, "select password from tidvalue where id = '%s'",_id->id);
	iot_get_data_from_db(temp,passconf);
	res = strcmp(_id->id,idconf);
	if (res != 0)
	{
		return -1;
	}
	else
	{
		if(strcmp(_id->password,passconf)==0) return 1;
		else return -2;
	}

// _id->id database table check.
// if table not exist / return -1
// else 
// 	_id->password <> talbe(SQL)->password 
// 	if collect / return 1
// 	else not collect / return -2
}
int join_id(Id* _id)
{
	char temp[256] = {0};
	printf("call databases : join_id \n");
	printf("id : %s || password : %s \n",_id->id,_id->password);
	sprintf(temp, "insert into tidvalue values ('%s','%s')",_id->id, _id->password);
	iot_insert_data_from_db(temp);
	sprintf(temp, "insert into tdrivervalue values ('%s','','','','')",_id->id);
	iot_insert_data_from_db(temp);
	sprintf(temp, "insert into temergencyvalue values ('%s','','','')",_id->id);
	iot_insert_data_from_db(temp);
	sprintf(temp, "insert into userstandard values ('%s','','','','','','','')",_id->id);
	iot_insert_data_from_db(temp);
	
	return 1;
// _id->id database table check.
// if table exist / return -1
// else make table / table(SQL)->password = _id->password
// table 
/****************************************************************************
*									    *
* password / name / age / sex / disease / name(contact) / number / relation *
*									    *	
****************************************************************************/
// return 1;
//	printf("join id called");
}
