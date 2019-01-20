#########################################################
#							#
#  File Name : Makefile					#
#  file create : 2017/??/??				#
#  maked by : 2017 iot home graduation team		#
#							#
#########################################################

.SURRIXES : .cpp .o
CC = g++
AR = ar

LIBS += -lopencv_core -lopencv_highgui -L/usr/lib/uv4l/uv4lext/armv6l -luv4lext
LINK =  -Wl,-rpath,'/usr/lib/uv4l/uv4lext/armv6l'
OPENCV = `pkg-config opencv --cflags --libs`

TARGET = facealldetect.a
OBJS = facealldetect.o
SRCS = facealldetect.cpp

all : $(TARGET)

$(TARGET) : $(OBJS)
	$(AR) rcs $@ $(OBJS) 

%.o : %.cpp
	$(CC) -c $< -o $@ $(DEFINES) $(OPENCV) 

install :
	@echo "Installing in facedetect Finished" \

clean :
	rm -rf $(OBJS) $(TARGET) core
