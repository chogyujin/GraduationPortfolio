// facealldetect.h
#ifndef _FACEALLDETECT_H
#define _FACEALLDETECT_H

#include <opencv2/opencv.hpp>
#include <opencv2/highgui.hpp>
#include <stdio.h>
#include <stdlib.h>
#include <csignal.h>
#include <iostream>

#ifdef __cplusplus
extern "C"{
#endif

void* facedetect(void *pInst);
void clean_up(void *arg);

#ifdef __cplusplus
}
#endif

#endif
