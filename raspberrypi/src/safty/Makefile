
 ################################################################
 #								#
 # File Name: Makefile						#
 #								#
 # file creation : 2017/7/31					#
 # 2017/08/30 - join face detect lib				#
 #								#
 ################################################################
.SUFFIXES : .c .o
CC = gcc

INC += -I/usr/include/mysql
LIBS += -lstdc++ -lgfortran -lmysqlclient -L/usr/local/lib/ -lpthread -lwiringPi -lopencv_videoio -lopencv_core -lopencv_highgui -lopencv_objdetect -lopencv_imgproc 
CFLAGS = -g -c $(INC) 
OBJECTS = safty_main.o safty_sensor.o safty_database.o safty_arduino.o
SRCS = safty__main.c safty_sensor.c safty_database.c safty_arduino.c
DEFINES = 
TARGET = server_safty
OPENCV = -Wl,-rpath,'/usr/lib/uv4l/uv4lext/armv6l' 
LASPICAM = -lraspicam -lraspicam_cv
ARDOP = -DRaspberryPi

all : $(TARGET)
$(TARGET) : $(OBJECTS)
	g++ -o $@ $(OBJECTS) $(LASPICAM) $(IOTLIB) $(FACELIB) $(LIBS) $(ARDOP)
	
%.o : %.c
	$(CC) $(CFLAGS) -c $< -o $@ $(DEFINES)

install :
	cp -a $(TARGET) $(INSTALLDIR)/

clean :
	rm -rf $(OBJECTS) core
	rm $(TARGET)

new : 
	$(MAKE) clean 
	$(MAKE) 
 
