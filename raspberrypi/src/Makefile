
 #############################################################################
 #
 # File Name: Makefile
 #
 # file creation : 2017/7/31
 # team : 2017 iot_smart home graduation team
 #
 #############################################################################
.SUFFIXES : .c .o
CC = gcc

INC += 
LIBS +=
CFLAGS = -g -c $(INC) 
DEFINES = 
SUBDIRS = safty 

all : $(TARGET)
	@for i in $(SUBDIRS); do \
    echo "make all in $$i..."; \
    (cd $$i; $(MAKE) $(MFLAGS) $(MYMAKEFLAGS) all); done

install:
	@for i in $(SUBDIRS); do \
	echo "Installing in $$i..."; \
	(cd $$i; $(MAKE) $(MFLAGS) $(MYMAKEFLAGS) install); done

clean :
	@for i in $(SUBDIRS); do \
	echo "Clearing in $$i..."; \
	(cd $$i; $(MAKE) $(MFLAGS) $(MYMAKEFLAGS) clean); done

new : 
	$(MAKE) clean 
	$(MAKE) 

