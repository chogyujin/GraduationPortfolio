
 #############################################################################
 #
 # File Name: Makefile
 #
 # file creation : 2017/07/31
 # gratuation team : 2017 iot_home team
 #
 #############################################################################
.SUFFIXES : .c .o
CC = gcc
SHELL=/bin/sh
MAKE = make

INC = -I$(PWD)/include
LIBS = -L$(PWD)/libs
INSTALLDIR = $(PWD)/output
IOTLIB = $(PWD)/libs/iotlib/libiot.a
FACELIB = $(PWD)/libs/facedetect/facealldetect.a
SUBDIRS = libs src

export INC
export INSTALLDIR
export IOTLIB
export FACELIB

all :
	@for i in $(SUBDIRS); do \
    echo "make all in $$i..."; \
    (cd $$i; $(MAKE) $(MFLAGS) $(MYMAKEFLAGS) all); done

install:
	mkdir $(INSTALLDIR)
	@for i in $(SUBDIRS); do \
	echo "Installing in $$i..."; \
	(cd $$i; $(MAKE) $(MFLAGS) $(MYMAKEFLAGS) install); done
								 
clean:
	rm -rf $(INSTALLDIR)
	@for i in $(SUBDIRS); do \
	echo "Clearing in $$i..."; \
	(cd $$i; $(MAKE) $(MFLAGS) $(MYMAKEFLAGS) clean); done
															 
