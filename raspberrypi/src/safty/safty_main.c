/****************************************************************
* File Name: safty_main.c					*
*								*
* file creation : 2017/7/31					*
* team : 2017 iot_smart home graduation team			*
* made by js							*
*								*
* 2017/8/1 - code clean up					*
*								*
* 2017/8/16 - reschedule networking, tcp/ip command confirm	*
* 2017/8/22 - main roop schedule				*
* 2017/9/25 - arduino added					*
* 2017/9/28 - thread scheduling, option added, remake print	*
* 2017/10/19 - calc moved
*								*
****************************************************************/

#include <stdio.h>			
#include <signal.h>
#include <sys/socket.h>	
#include <arpa/inet.h>		
#include <stdlib.h>		
#include <string.h>			
#include <unistd.h>		
#include <sys/time.h>
#include <sys/types.h>
#include "iot_db.h"
#include "types.h"
#include "parser.h"
#include "safty_sensor.h"
#include "safty_query.h"
#include "safty_arduino.h"

#define MAXCLIENT   10
#define MAXPENDING  5
#define DB_NAME "iotsafe"
#define SENSOR_LOG_INTERVAL 1*60*10
#define ACTUATOR_LOG_INTERVAL 1*60*10

#define FIRSTSTEP 60
#define SECONDSTEP 70
#define THRIDSTEP 80

typedef struct _TInfo
{
	unsigned int	uiUser;
	int		iSock;
	pthread_t	tID;
} TInfo;

void sig_handler(int signo);
void *SensorInterruptLoop(void *);
void *ClientRecv(void *);
void *networkLoop();
void alloc_memory();
void init_mutex();
void connectToDb();
void createSensorThread();
void createNetworkThread();
int parse_message(char * data, Message * msg);
int parse_data(char * parameter, Data * _data);
int parse_data_str(char * parameter, DataSTR * _data);
void broadcastEmergency();
void *clientSend(void* vp);
void reflectHealth();
void actuatorReflect();
void *actuatorThreadFunc(void *vp);
void *facedetect(void *pInst);
void createFaceDetectThread();
int calc_danger();
void appropriateActuToDanger(int _danger);
void breath_pulse_cleanup(void);
void printUserState();
void arduinosetup();
void* arduinoloop(void);
void createArduinoThread();
void* inputloop();
void* outputloop();
void createIOThread();
void updateUserDat();

unsigned int		uiUser;
TInfo *			stpLink[MAXCLIENT];
pthread_mutex_t		MLock;
pthread_mutex_t		ActuLock;
int 			servSock;

int 			int_loop_result;
int 			th_sensor_loop;
int 			th_network_loop;
int			th_actuator_loop;
int			th_facedetect_loop;
int			th_arduino_loop;
int			th_input_loop;
int			th_output_loop;

UserState *		userState;
UserStandard * 		userStandard;
Actuator *		actuator;
DriverState * 		driverState;
Emergency * 		emergency;
FaceResult *		faceresult;

int			emergencymode = 0;
int			dangerStep = 0;
int			dangerCause = NORMAL;

int			cameramode = 1;
int			actuFlag = 1;
int			sensorFlag = 1;
int			networkFlag = 1;
int			arduinoFlag = 1;
int			drivingFlag = 0;
int			printFlag = 1;
int			breathFlag = 1;
unsigned int 		faceCount = 0;

int main(int iArg, char *cpArg[])
{
	char _query[128] = { 0 };
	int iRet;
	char _choice;
	while((iRet = getopt(iArg,cpArg,"acsnrpb")) != -1){
		switch(iRet){
			case 'c':
				printf("[main]CameraMode Off\n");
				cameramode = 0; 
				break;
			case 'a':
				printf("[main]ActuMode Off\n");
				actuFlag = 0;
				break;
			case 'n':
				printf("[main]NetMode Off\n");
				networkFlag = 0;
				break;
			case 's':
				printf("[main]sensorMode Off\n");
				sensorFlag = 0;
				break;
			case 'r':
				printf("[main]arduinoMode Off\n");
				arduinoFlag = 0;
				break;
			case 'p':
				printf("[main]printMode Off\n");
				printFlag = 0;
				break;		
			case 'b':
				printf("[main]breath Off\n");
				breathFlag  = 0;
				break;
		}
	}
	
	alloc_memory();
	init_mutex();
	connectToDb();
	createSensorThread();
	createFaceDetectThread();
	createArduinoThread();
	createIOThread();
	createNetworkThread();
	signal(SIGINT, (void *)sig_handler);
	//end of init

	printf("[main]wait for thread terminamted....\n");
	if(networkFlag == 1){
		pthread_join(th_network_loop);		
		printf("[main] network terminated\n");		

	}
	if(sensorFlag == 1){
		pthread_join(th_sensor_loop);
		printf("[main] sensor terminated\n");		
	}
	if(arduinoFlag == 1)
	{
		pthread_join(th_arduino_loop);
		printf("[main] arduino terminated...\n");		
	}
	if(printFlag == 1)
	{
		pthread_join(th_output_loop);	
		printf("[main] output terminated\n");
	}
	pthread_join(th_facedetect_loop);	
	printf("[main] facedetect terminated\n");		
	pthread_join(th_input_loop);	
	printf("[main] input terminated\n");
			
	// deallocate memory
	free(userState);
	free(actuator);
	free(driverState);
	free(emergency);
	free(faceresult);
	free(userStandard);
	printf("[main] free all memory\n");
	
	return 0;
}

int calc_danger()
{
	static int sleepCount = 0;
	if(drivingFlag == 0) { dangerCause = NOTDRIVING; return 0; }
	if(userState->pulse < userStandard->normalBPM - userStandard->diffBPM ) { dangerCause = LOWBPM;  return 3; }
	if(userState->near == 1) { dangerCause = NOTINCHAIR; return 3; }
	if(userState->ultrasonic > userStandard->awayFromChair + 30 && userState->ultrasonic < userStandard->awayFromChair + 60){ dangerCause = HEADTOOFAR; return 3; }
	if(breathFlag == 1){
		if(userState->one_Count < 2 ){ dangerCause = NOBREATH; return 3; }
 	}

	if(userState->ultrasonic > userStandard->awayFromChair + 10){ dangerCause = HEADFAR; return 2; }

	if(breathFlag == 1){
		if(userState->max_Breath < (int)(userStandard->maxBreath*0.7)){ dangerCause = WEEKBREATH; return 2; }
	}
	if(faceresult->height != 0 && faceresult->eyeResult[0].height == 0){ 
		sleepCount++; 
		if(sleepCount > 3) sleepCount = 3;
	}
	else{
		sleepCount = 0;
	}
	if(sleepCount > 2){ dangerCause = EYECLOSED; return 1; }

	dangerCause = NORMAL;
	return 0;
}

void appropriateActuToDanger(_beforeStep)
{
	if(_beforeStep != dangerStep)
	{
		//printf("[AppopriateActu]dangerStepChanged!\n");
		printUserState();
		pthread_mutex_lock(&ActuLock);
		actuator->use_buz = 0;
		actuator->use_fan = 0;
		actuator->use_pump = 0;
		actuator->use_led = 0;
		switch(dangerStep){ 
			case 0:
				printf("Emergency Mode Off\n");
				emergencymode = 0;
				dangerStep = 0;
				break;
			case 1:
				printf("Danger : 1st step\n");
				actuator->use_pump = 1; 
				actuator->use_fan = 1;
				dangerStep = 1;
				emergencymode = 1;
				break;
			case 2:
				printf("Danger : 2nd step\n");
				actuator->use_pump = 1; 
				actuator->use_fan = 1;
				actuator->use_buz = 1; 
				actuator->use_led = 1;
				dangerStep = 2;
				break;
			case 3:
				printf("Danger : 3rd step\n");
				actuator->use_buz = 1; 
				broadcastEmergency();
				dangerStep = 3;
				break;
			default:
				printf("[%s]Unexpected case!\n");
				break;
		}
		pthread_mutex_unlock(&ActuLock);
		actuatorReflect();
	}
}

void sig_handler(int signo)
{
	int iRet;
	int nClient;
	printf("[SIG_Handler] called!!!!!!!!!!\n");		
	if(networkFlag == 1){
		for(nClient=0; nClient < uiUser; nClient++){
			close(stpLink[nClient]->iSock);
		}
		// servClose
		close(servSock);
		printf("[SIG_Handler] network terminating...\n");		
		pthread_cancel(th_network_loop);	

	}
	// DB control
	iot_disconnect_from_db();
	// Program will be terminated. collect thread 
	printf("[SIG_Handler] actuator terminating...\n");		
	if(actuFlag == 1){
		clearAllActuator();
	}
	if(sensorFlag == 1){
		printf("[SIG_Handler] sensor terminating...\n");		
		pthread_cancel(th_sensor_loop);
	}
	if(arduinoFlag == 1)
	{
		printf("[SIG_Handler] arduino terminating...\n");		
		pthread_cancel(th_arduino_loop);
	}
		
	printf("[SIG_Handler] facedetect terminating...\n");		
	clean_up((void*)NULL);
	pthread_cancel(th_facedetect_loop);
	pthread_cancel(th_input_loop);	
	pthread_cancel(th_output_loop);	
	printf("[SIG_Handler] io terminating....\n");		
	return;
}

void alloc_memory(){
	userState = (UserState *)malloc(sizeof(UserState));
	memset(userState, 0x0, sizeof(UserState));

	driverState = (DriverState *)malloc(sizeof(DriverState));
	memset(driverState, 0x0, sizeof(DriverState));

	emergency = (Emergency *)malloc(sizeof(Emergency));
	memset(emergency, 0x0, sizeof(Emergency));

	actuator = (Actuator *)malloc(sizeof(Actuator));
	memset(actuator, 0x0, sizeof(Actuator));

	faceresult = (FaceResult *)malloc(sizeof(FaceResult));
	memset(faceresult, 0x0, sizeof(FaceResult));
	
	userStandard = (UserStandard *)malloc(sizeof(UserStandard));
	memset(userStandard, 0x0, sizeof(UserStandard));
	userStandard->heightOfEye = 30;
	userStandard->awayFromChair = 10;
	userStandard->normalBPM = 75;
	userStandard->diffBPM = 25;
	userStandard->normalBreath = 4;
	userStandard->diffBreath = 4;
	userStandard->maxBreath = 100;
	
}
void init_mutex(){
	int iRet;
	printf("[main]mutex init...\n");
	iRet = pthread_mutex_init(&MLock, NULL);
	if (iRet != 0)
	{
		printf("[main]mutex MLock init failed!!\n");
		exit(0);
	}
	iRet = pthread_mutex_init(&ActuLock, NULL);
	if (iRet != 0)
	{
		printf("[main]mutex ActuLock init failed!!\n");
		exit(0);
	}
}

void connectToDb() {
	int iRet;
	char _tmpquery[128] = { 0 };

	printf("[main]connect to DB...\n");
	//DB control 
	iot_connect_to_db(DB_NAME);

	printf("[%s]validate database...\n", __func__);
	iot_send_query(QUERY_CREATE_TABLE_ID_VALUE);
	iot_send_query(QUERY_CREATE_TABLE_DRIVER_VALUE);
	iot_send_query(QUERY_CREATE_TABLE_EMERGENCY_VALUE);
	iot_send_query(QUERY_CREATE_TABLE_USERSTANDARD);
}

void actuatorReflect()
{
	int iRet;
	if(actuFlag == 0) {
		printf("[actuatorReflect]actuflag mode off\n");
		return;
	}
	
	iRet = wiringPicheck();
	if(iRet != 0)
	{
		printf("[actuatorReflect][createActuatorThread]Unable to start wiringPi\n");
		exit(0);
	}

	iRet = sizeof(actuator);
	if(iRet <= 0){
		printf("[actuatorReflect]actuator not allocated\n");
		return;
	}
	pthread_mutex_lock(&MLock);
	iRet = pthread_create(&th_actuator_loop, 0, actuatorThreadFunc, &int_loop_result);
	if (iRet != 0)
	{
		printf("[actuatorReflect]Actuator thread create failed!!\n");
		exit(0);
	}
	pthread_mutex_unlock(&MLock);
}

void *actuatorThreadFunc(void *vp)
{
	int iRet;
	act_waterpump_active(actuator->use_pump);
	act_fan_active(actuator->use_fan);
	act_dcmotor_active(actuator->use_dcmotor);
	act_led_active(actuator->use_led);
	act_buzcontrol(actuator->use_buz);	
}

void createSensorThread(){
	int iRet;

	printf("[createSensorThread]starting Sensor thread...\n");
	
	iRet = sizeof(userState);
	if(iRet <= 0){
		printf("[SensorLoop]sensor not allocated\n");
		return;
	}
	pthread_mutex_lock(&MLock);
	iRet = pthread_create(&th_sensor_loop, 0, SensorInterruptLoop, &int_loop_result);
	if (iRet != 0)
	{
		printf("[main]Sensor thread create failed!!\n");
		exit(0);
	}
	pthread_mutex_unlock(&MLock);
}

void *SensorInterruptLoop(void *vp)
{
	int tmp;
	printf("[SensotInterruptLoop]Sensor Start\n");
	if(sensorFlag == 1){
		while (1){
			userState->temperature = get_temperature_sensor();
			userState->humid = get_humidity_sensor();
			userState->near = get_neardetect();
			userState->ultrasonic = get_ultrasonic();
			if(userState->ultrasonic > 1000){
				userState->ultrasonic = 0;
			}
			pthread_yield(NULL);		
		}
	}
	else{
		printf("[SensotInterruptLoop]Sensor fixed to demo\n");
		userState->temperature = 25;
		userState->humid = 40;
		userState->near = 0;
		userState->ultrasonic = 30;
	}
}

void createNetworkThread(){
	int iRet;
	if(networkFlag == 0){
		printf("[createNetworkThread]networknode off\n");
		return;
	}
	printf("[main]starting Network thread...\n");
	pthread_mutex_lock(&MLock);
	iRet = pthread_create(&th_network_loop, 0, networkLoop, &int_loop_result);
	if (iRet != 0)
	{
		printf("[main]Network thread create failed!!\n");
		exit(1);
	}
	pthread_mutex_unlock(&MLock);
}

void *networkLoop() {
	unsigned short echoServPort = 11000;	
	struct sockaddr_in echoServAddr;
	struct sockaddr_in echoClntAddr;
	TInfo stTempInfo;
	unsigned int clntLen;
	int iRet;

	/*********************create socket********************/
	printf("[network]create socket...\n");
	servSock = socket(AF_INET, SOCK_STREAM, IPPROTO_TCP);
	if (0 > servSock)
	{
		printf("[network]socket() failed");
		exit(0);
	}

	/*********************bind socket********************/
	printf("[network]bind socket...\n");
	memset(&echoServAddr, 0, sizeof(echoServAddr));
	echoServAddr.sin_family = AF_INET;
	echoServAddr.sin_addr.s_addr = htonl(INADDR_ANY);
	echoServAddr.sin_port = htons(echoServPort);
	iRet = bind(servSock, (struct sockaddr *)&echoServAddr, sizeof(echoServAddr));
	if (0 > iRet)
	{
		close(servSock);
		printf("[network]bind() failed\n");
		exit(0);
	}

	/*********************listen socket********************/
	printf("[network]listening socket...\n");
	iRet = listen(servSock, MAXPENDING);
	if (0 > iRet)
	{
		close(servSock);
		printf("[network]listen() failed\n");
		exit(0);
	}

	/*********************start accepting********************/
	printf("starting server...\n");
	clntLen = sizeof(echoClntAddr);
	uiUser = 0;
	while (1)
	{
		printf("[network]get ready for new connecting...\n");
		stTempInfo.iSock = accept(servSock, (struct sockaddr *)&echoClntAddr, &clntLen);
		if (0 > stTempInfo.iSock)
		{
			printf("[network]accept() failed \n");
			continue;
		}
		printf("[network]Handling client ip : %s\n", inet_ntoa(echoClntAddr.sin_addr));
		printf("[network]Handling client port : %d\n", ntohs(echoClntAddr.sin_port));
		printf("[network]Handling client socket number : %d\n", stTempInfo.iSock);
		pthread_mutex_lock(&MLock);
		stTempInfo.uiUser = uiUser;
		pthread_create(&stTempInfo.tID, 0, ClientRecv, &stTempInfo);
		++uiUser;
		pthread_mutex_unlock(&MLock);

		while (0 != stTempInfo.iSock);
		printf("connected user  : %d\n", uiUser);
	}
	close(servSock);
}

void *ClientRecv(void *vp)
{
	unsigned char	ucBuff[UCBUFFMAX];
	unsigned char	ucSBuff[UCBUFFMAX];
	unsigned int	uiCnt;
	int		iRet;
	TInfo		stMyInfo = *((TInfo *)vp);
	Message * _message;
	DataSTR * _data;
	char * _result;
	
	Id id;

	_data = (DataSTR *)malloc(sizeof(DataSTR));
	_message = (Message *)malloc(sizeof(Message));
	_result = (char*)malloc(sizeof(char)*20);

	memset(_data, 0x0, sizeof(DataSTR));
	memset(_message, 0x0, sizeof(Message));
	memset(_result, 0x0, sizeof(char)*20);

	stpLink[stMyInfo.uiUser] = &stMyInfo;
	((TInfo *)vp)->iSock = 0;

	while (1)
	{
		iRet = read(stMyInfo.iSock, ucBuff, UCBUFFMAX);
		if (1 > iRet)
		{
			break;
		}
		memset(_message, 0x0, sizeof(Message));
		iRet = parse_message(ucBuff, _message);
		memset(ucBuff, 0x0, sizeof(char)*UCBUFFMAX);

		switch (_message->_command) {
			case GET_STATUS:
				//printf("[Cli]GET_STATUS\n");
				iRet = sprintf(ucSBuff, "%d|%d|%d,%d,%d,%d,%d,%d", _message->_apptype, _message->_command, (int)userState->temperature, (int)userState->humid, userState->pulse, userState->one_Count, (dangerStep >= 3)?0:dangerStep, (dangerStep==3)?1:0 );  
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;
	
			case GET_HEALTH:
				//printf("[Cli]GET_HEALTH\n");
				memcpy(&driverState->id, (void*)&id.id, sizeof(char)*20);
				get_health(driverState);
				//printf("[Cli][SNDMSG]id:%s, name:%s, age:%d, sex:%s, disease:%s\n",driverState->id, driverState->name, driverState->age, driverState->sex, driverState->disease);
				iRet = sprintf(ucSBuff, "%d|%d|%s,%s,%s,%s", _message->_apptype, _message->_command, driverState->name, driverState->age, driverState->sex, driverState->disease);
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;

			case SET_HEALTH:
				//printf("[Cli]SET_HEALTH\n");
				memset(_data, 0x0, sizeof(DataSTR));
				iRet = parse_data_str(_message->_data, _data);
				memcpy(driverState->name, _data->_data1, sizeof(char)*20);
				memcpy(driverState->age, _data->_data2, sizeof(char)*20);
				memcpy(driverState->sex, _data->_data3, sizeof(char)*10);
				memcpy(driverState->disease, _data->_data4, sizeof(char)*20);
				memcpy(driverState->id, &id.id, sizeof(char)*20);
				set_health(driverState);
				reflectHealth();

				iRet = sprintf(ucSBuff, "%d|%d", _message->_apptype, _message->_command);
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;

			case UPDATE_USER :
				memcpy(&userStandard->id, (void*)&id.id, sizeof(char)*20);
				updateUserDat(userStandard->id);
				iRet = sprintf(ucSBuff, "%d|%d|%d,%d", _message->_apptype, _message->_command, userStandard->normalBPM, userStandard->normalBreath);
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;
			case GET_HISTORY:
				//printf("[Cli]GET_HISTORY\n");
				
				memcpy(&driverState->id, (void*)&id.id, sizeof(char)*20);
				get_health(driverState);

				memcpy(&emergency->id, (void*)&id.id, sizeof(char)*20);
				get_emergency(emergency);
				//printf("[Cli][SNDMSG]ID:%s, Name:%s, number:%s, relation:%s\n",emergency->id, emergency->name, emergency->number, emergency->relation);
				// end of make stub
				iRet = sprintf(ucSBuff, "%d|%d|%s,%s,%s,%s,%s,%s,%s,%s", _message->_apptype, _message->_command, driverState->name, driverState->age, driverState->sex, driverState->disease, emergency->name, emergency->number, emergency->relation);
				write(stMyInfo.iSock, ucSBuff, iRet);
				
				break;

			case CONFIRM_ID:
				//printf("[Cli]CONFIRM_ID\n");
				memset(_data, 0x0, sizeof(DataSTR));
				iRet = parse_data_str(_message->_data, _data);
				memcpy((void*)&id.id, _data->_data1, sizeof(char)*20);
				memcpy((void*)&id.password, _data->_data2, sizeof(char)*20);
				iRet = confirm_id(&id);
				//printf("[CliRev]Confirm Result:%d\n",iRet);
				memcpy(&userStandard->id, (void*)&id.id, sizeof(char)*20);
				get_userstandard(userStandard);
				if(iRet == 1){
					iRet = sprintf(ucSBuff, "%d|%d|%d,%d,%d", _message->_apptype, _message->_command, iRet, userStandard->normalBPM, userStandard->normalBreath);
				}
				else{
					iRet = sprintf(ucSBuff, "%d|%d|%d", _message->_apptype, _message->_command, iRet);
				}
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;

			case JOIN_ID:
				//printf("[Cli]JOIN_ID\n");
				memset(_data, 0x0, sizeof(DataSTR));
				iRet = parse_data_str(_message->_data, _data);
				memcpy((void*)&id.id, _data->_data1, sizeof(char)*20);
				memcpy((void*)&id.password, _data->_data2, sizeof(char)*20);
				iRet = join_id(&id);
				iRet = sprintf(ucSBuff, "%d|%d|%d", _message->_apptype, _message->_command, iRet);
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;

			case GET_PHONE_NUM:
				//printf("[Cli]GET_PHONE_NUM\n");
				memcpy(&emergency->id, (void*)&id.id, sizeof(char)*20);
				get_emergency(emergency);

				iRet = sprintf(ucSBuff, "%d|%d|%s,%s,%s", _message->_apptype, _message->_command, emergency->name, emergency->number, emergency->relation);
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;
			case SET_PHONE_NUM:
				printf("[Cli]SET_PHONE_NUM\n");
				memset(_data, 0x0, sizeof(DataSTR));
				iRet = parse_data_str(_message->_data, _data);
				memcpy((void*)emergency->id, &id.id, sizeof(char)*20);
				memcpy((void*)emergency->name, _data->_data1, sizeof(char)*20);
				printf("SET_PHONE_point1\n");
				memcpy((void*)emergency->number, _data->_data2, sizeof(char)*20);
				printf("SET_PHONE_point2\n");
				memcpy((void*)emergency->relation, _data->_data3, sizeof(char)*10);
				set_emergency(emergency);
				iRet = sprintf(ucSBuff, "%d|%d", _message->_apptype, _message->_command);
				write(stMyInfo.iSock, ucSBuff, iRet);
				break;

			case FAN_ACT: 
				printf("[Cli]FAN_ACT\n");
				memset(_data, 0x0, sizeof(DataSTR));
				iRet = parse_data_str(_message->_data, _data);
				actuator->use_fan = atoi(_data->_data1);
				actuatorReflect();
				break;
		}
	
	#ifdef USE_BROADCAST
		for (uiCnt = 0; uiUser>uiCnt; ++uiCnt)
		{
			if (&stMyInfo == stpLink[uiCnt])
			{
				continue;
			}
			write(stpLink[uiCnt]->iSock, ucSBuff, iRet);
			flush();
		}
	#endif
		pthread_yield(NULL);
	} // end of loop

	  /*********************client terminated********************/
	printf("client thread will terminated. \n");
	free(_data);
	free(_message);
	pthread_mutex_lock(&MLock);
	--uiUser;
	stpLink[stMyInfo.uiUser] = stpLink[uiUser];
	stpLink[stMyInfo.uiUser]->uiUser = stMyInfo.uiUser;
	pthread_mutex_unlock(&MLock);
	close(stMyInfo.iSock);
	return 0;
}

void reflectHealth(){
	if(strcmp(driverState->disease,"0")==0){
		printf("[reflectHealth]DriverHeath!\n");
		userStandard->diffBPM = 25;
		userStandard->diffBreath = 4;
	}
	if(strcmp(driverState->disease,"1")==0){
		printf("[reflectHealth]1!\n");
		userStandard->normalBPM += 20;
		userStandard->diffBPM = 20;
	}
	if(strcmp(driverState->disease,"2")==0){
		printf("[reflectHealth]2\n");
		userStandard->normalBPM += 10;
		userStandard->diffBPM = 20;
	}
	if(strcmp(driverState->disease,"3")==0){
		printf("[reflectHealth]3!\n");
		userStandard->normalBPM -= 10;
		userStandard->diffBPM = 20;
	}
	if(strcmp(driverState->disease,"4")==0){
		printf("[reflectHealth]4!\n");
		userStandard->normalBreath = 7;
		userStandard->diffBreath = 4;
	}
	set_userstandard(userStandard);	
}

void broadcastEmergency(){
	unsigned char	ucSBuff[UCBUFFMAX];
	int nClient;
	int iRet;
	pthread_t tempThreadNum;

	if(networkFlag == 0){
		printf("[broadcastEmergency]networkmode Off\n");
		return;
	}
	printf("[main][braodcastEmergency] Send Emergency to Client total:%d\n",uiUser);
	iRet = sprintf(ucSBuff, "%d|%d|", 0x0, EMERGENCY);
	for(nClient=0; nClient < uiUser; nClient++){
		printf("[main][braodcastEmergency] Send Emergency to Client - %d\n",nClient);
		write(stpLink[nClient]->iSock, ucSBuff, iRet);
	}
	printf("[main][braodcastEmergency] end of broadcasting\n");
}

void createFaceDetectThread(){
	int iRet;
	printf("[main]starting FaceDetect thread...\n");
	pthread_mutex_lock(&MLock);
	iRet = pthread_create(&th_facedetect_loop, 0, &facedetect, &int_loop_result);
	if(iRet != 0)
	{
		printf("[main]starting FaceDetect thread...\n");
		exit(1);
	}
	pthread_mutex_unlock(&MLock);
}

void createArduinoThread(){
	int iRet;
	if(arduinoFlag == 0){
		printf("[createArduinoThread]arduinomode Off\n");
		return;
	}
	printf("[main]starting Arduino thread...\n");
	arduinosetup();
	pthread_mutex_lock(&MLock);
	iRet = pthread_create(&th_arduino_loop, 0, arduinoloop, &int_loop_result);
	if (iRet != 0)
	{
		printf("[main]Arduino thread create failed!!\n");
		exit(1);
	}
	pthread_mutex_unlock(&MLock);
}
void createIOThread(){
	int iRet;
	printf("[main]starting I/O thread...\n");
	pthread_mutex_lock(&MLock);
	iRet = pthread_create(&th_input_loop, 0, inputloop, &int_loop_result);
	if (iRet != 0)
	{
		printf("[main]input thread create failed!!\n");
		exit(1);
	}
	if(printFlag == 1){
		iRet = pthread_create(&th_output_loop, 0, outputloop, &int_loop_result);
		if (iRet != 0)
		{
			printf("[main]output thread create failed!!\n");
			exit(1);
		}
	}
	pthread_mutex_unlock(&MLock);
}

void* inputloop(){
	char tmp;
	while(1){
		scanf(" %c", &tmp);
		switch(tmp){
			case 'd': drivingFlag = 1; break;
			case 'p': drivingFlag = 0; break;
			case 'b': 
				if(breathFlag == 1) breathFlag = 0; 
				else breathFlag = 1;
				break;
			case 'q': sig_handler(SIGINT); break;
			default : printf("[ioloop] Unexpected input\n"); break;
		}
	}	
}
void* outputloop(){
	int beforeStep = 0;
	while(1){
		beforeStep = dangerStep;
		dangerStep = calc_danger();
		appropriateActuToDanger(beforeStep);
		printUserState();
		sleep(1);
	}	
}

void printUserState(){
	printf("\n\n----------------State View----------------\n");
	if(drivingFlag == 1){ printf("Driving... input 'p' to stop\n"); }
	else{ printf("Car Stopped... input 'd' to drive\n"); }

	printf("UserState : %d\t", dangerStep);
	printf("Emergency:");
	switch(dangerCause){
		case 0x200: printf("normal"); break;
		case 0x201: printf("not in driving"); break;
		case 0x202: printf("bpm too low!"); break;
		case 0x203: printf("driver is not in chair!"); break;
		case 0x204: printf("head too far from chair!"); break;
		case 0x205: printf("breath force too week!"); break;
		case 0x206: printf("driver eye closed!"); break;
		case 0x207: printf("breath too low"); break;
		case 0x208: printf("head far from chair"); break;
		case 0x209: printf("cannot find breath"); break;
	}
	printf("\nTemp:%0.2f\t Humid:%0.2f \n", userState->temperature, userState->humid);
	printf("pulse:%dBPM\t stan:%dBPM\n",userState->pulse, userStandard->normalBPM);
	printf("Breath: %d/min\t stan:%d/min\n" , 3*userState->one_Count, 3*userStandard->normalBreath);
	printf("BreathForce:%d\t min Force:%d\n", userState->max_Breath, userStandard->maxBreath);
	printf("Head Dist:%d\t stan Dist:%d\n", userState->ultrasonic, userStandard->awayFromChair);
	printf("head Size:%d\n", faceresult->height);
	printf("Eye Size:%d\t stan eye size:%d\n", faceresult->eyeResult[0].height, userStandard->heightOfEye);
	printf("connected android:%d\n",uiUser);
	printf("------------------------------------------\n\n");
}

void updateUserDat(char* _id){
	int i=0;
	int nSample = 40;
	UserStandard sampleUserStandard;	
	memset(&sampleUserStandard, 0x0, sizeof(UserStandard));
	for(;i<nSample;i++){
		sampleUserStandard.heightOfEye += faceresult->eyeResult[0].height;
		sampleUserStandard.awayFromChair += userState->ultrasonic;
		sampleUserStandard.normalBPM += userState->pulse;
		sampleUserStandard.normalBreath += userState->one_Count;
		if(sampleUserStandard.maxBreath < userState->max_Breath) {
			sampleUserStandard.maxBreath = userState->max_Breath;
		}
		printf("[updateUserDat]Do Profit.....\n"); 
		sleep(1);	
		pthread_yield(NULL);		
	}
	userStandard->heightOfEye = sampleUserStandard.heightOfEye / nSample;
	userStandard->awayFromChair = sampleUserStandard.awayFromChair / nSample;
	userStandard->normalBPM = sampleUserStandard.normalBPM / nSample;
	userStandard->normalBreath = sampleUserStandard.normalBreath / nSample;
	userStandard->maxBreath = sampleUserStandard.maxBreath;
	printf("[updateUserDat]Profit End\nheightOfEye = %d, awayFromChair = %d, normalBPM : %d, diffBPM : %d, normalBreath = %d, diffBreath = %d\n",userStandard->heightOfEye, userStandard->awayFromChair, userStandard->normalBPM, userStandard->diffBPM, userStandard->normalBreath, userStandard->diffBreath);
	memcpy(&(userStandard->id), _id, sizeof(char)*20);
	set_userstandard(userStandard);	
	reflectHealth();
}
