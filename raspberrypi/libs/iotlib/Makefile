
 #############################################################################
 #
 # File Name: Makefile
 #
 # file creation : 2014/12/26
 # team : 2017 graduation iot_smart home team
 #
 #############################################################################
.SUFFIXES : .c .o
CC = gcc
AR = ar

INC += -I/usr/include/mysql
LIBS += -L/usr/lib/i386-linux-gnu/
CFLAGS = -c $(INC) 
OBJECTS = iot_db.o parser.o
SRCS = iot_db.o parser.o
TARGET = libiot.a

all : $(TARGET)

$(TARGET) : $(OBJECTS)
	$(AR) rcs $@ -o $(OBJECTS)

%.o : %.c
	$(CC) $(CFLAGS) $< -o $@ $(DEFINES)

clean :
	rm -rf $(OBJECTS) $(TARGET) core

install :
	@echo "Installing in iot Finished"\

new : 
	$(MAKE) clean 
	$(MAKE) 

