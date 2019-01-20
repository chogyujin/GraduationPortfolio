
/************************************************
 *						*
 * File Name: safty_query.h			*
 *						*
 * file creation : 2017/7/31			*
 * team : 2017 iot_smart home graduation team	*
 * redefine by KJ				*
 *						*
 ***********************************************/

#ifdef __cplusplus
extern "C" {
#endif

#define QUERY_CREATE_TABLE_SENSOR_VALUE "CREATE TABLE IF NOT EXISTS tsaftysensorvalue (id INT AUTO_INCREMENT PRIMARY KEY, date DATE, time TIME, temperature FLOAT, humid FLOAT, light INT)"
#define QUERY_CREATE_TABLE_SENSOR_CHECK "CREATE TABLE IF NOT EXISTS tsaftysensorcheck (id INT AUTO_INCREMENT PRIMARY KEY, date DATE, time TIME, temperature INT, humid INT, light INT)"
#define QUERY_CREATE_TABLE_ACTURATOR_VALUE "CREATE TABLE IF NOT EXISTS tsaftyactuoperate (id INT AUTO_INCREMENT PRIMARY KEY, date DATE, time TIME, pump INT, fan INT, dcmotor INT, rgbled INT)"
#define QUERY_CREATE_TABLE_ACTURATOR_CHECK "CREATE TABLE IF NOT EXISTS tsaftyactucheck (id INT AUTO_INCREMENT PRIMARY KEY, date DATE, time TIME, pump INT, fan INT, rgbled INT)"
#define QUERY_CREATE_TABLE_SETTING "CREATE TABLE IF NOT EXISTS tsaftysetting (id INT AUTO_INCREMENT PRIMARY KEY, hour INT, min INT, period INT)"
#define QUERY_SELECT_SENSOR_DATA "SELECT date, time, temperature, humid FROM tsaftysensorvalue WHERE time >= DATE_ADD(NOW(), INTERVAL-1 HOUR)"
#define QUERY_INSERT_SENSOR_DATA "INSERT INTO  tsaftysensorvalue values (null ,now(), now(), %f, %f, %d)"
#define QUERY_INSERT_SENSOR_CHECK "INSERT into tsaftysensorcheck values (null, now(), now(), %f, %f, %d)"
#define QUERY_INSERT_ACTUATOR_VALUE "INSERT into tsaftyactuoperate values (null, now(), now(), %d, %d, %d, %d)"
#define QUERY_INSERT_ACTUATOR_CHECK "INSERT into tsaftyactucheck values (null, now(), now(), %d, %d, %d)"
#define QUERY_INSERT_SETTING "INSERT into tsaftysetting values (null, %d, %d, %d)"
#define QUERY_UPDATE_SETTING "UPDATE tsaftysetting SET hour=%d,min=%d,period=%d WHERE id = 1"
#define QUERY_SELECT_SETTING "SELECT hour,min, period from tsaftysetting WHERE id =1"

#define QUERY_SELECT_COUNT_SETTING "SELECT COUNT(*) FROM tsaftysetting"

////
#define QUERY_CREATE_TABLE_ID_VALUE "CREATE TABLE IF NOT EXISTS tidvalue (id CHAR(20) PRIMARY KEY, password CHAR(20))"  
#define QUERY_CREATE_TABLE_DRIVER_VALUE "CREATE TABLE IF NOT EXISTS tdrivervalue (id CHAR(20)  PRIMARY KEY, name CHAR(20), age CHAR(10), sex CHAR(10), disease CHAR(20))"
#define QUERY_CREATE_TABLE_DRIVER_CHECK "CREATE TABLE IF NOT EXISTS tdrivercheck (id INT PRIMARY KEY, name INT, age INT, sex INT, disease INT)"
#define QUERY_CREATE_TABLE_EMERGENCY_VALUE "CREATE TABLE IF NOT EXISTS temergencyvalue (id CHAR(20) PRIMARY KEY, name CHAR(20), number CHAR(20), relation CHAR(20))"
#define QUERY_CREATE_TABLE_EMERGENCY_CHECK "CREATE TABLE IF NOT EXISTS temergencycheck (id INT PRIMARY KEY, name INT, number INT, disease INT)"
#define QUERY_INSERT_DRIVER_DATA "INSERT INTO tdrivervalue values (null , %s, %d, %s, %s)"
#define QUERY_INSERT_DRIVER_CHECK "INSERT INTO tdrivercheck values (null, %d, %d, %d, %d)"
#define QUERY_INSERT_EMERGENCY_DATA "INSERT INTO temergencyvalue values (null, %s, %d, %s)"

#define QUERY_CREATE_TABLE_USERSTANDARD "create table if not exists userstandard (id CHAR(20) PRIMARY KEY, heightofeye int, awayfromchair int, normalbpm int, diffbpm int, normalbreath int, diffbreath int, maxbreath int)"
#ifdef __cplusplus
}
#endif

