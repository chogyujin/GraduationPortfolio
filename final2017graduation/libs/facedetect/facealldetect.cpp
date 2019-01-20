/************************************************************************
*									*
*  File : facealldetect.cpp                   				*
*  Make : 2017 iot home graduation team					*
*  Date : 2017/8/29							*
*  made by JS								*
*  2017/9/7 - patch for piCam						*
*									*
************************************************************************/


#include <opencv2/opencv.hpp>
#include <opencv2/highgui.hpp>
#include <raspicam/raspicam_cv.h>
#include <stdio.h>
#include <stdlib.h>
#include <string>
#include "../../include/types.h"
#include <csignal>
#include <iostream>

#define maxFaceSizeY 640
#define maxFaceSizeX 480
#define maxEyeSizeY maxFaceSizeY/6
#define maxEyeSizeX maxFaceSizeX/5

#define minFaceSizeY maxFaceSizeY/6
#define minFaceSizeX maxFaceSizeX/6
#define minEyeSizeY minFaceSizeY/6
#define minEyeSizeX minFaceSizeX/5

#define minNeighbors 3
#define scaleFactor 1.6

using namespace std;
using namespace cv;


CascadeClassifier face;
CascadeClassifier eye;

extern FaceResult* faceresult;
extern int cameramode;

extern "C" {

raspicam::RaspiCam_Cv cap;

void clean_up(void *arg)
{
	cout << "[faceDetect] Clear Camera" << endl;
	cap.release();
	destroyAllWindows();
	cout << "[faceDetect] Clear Camera Finished!!" << endl;	
}

void* facedetect(void* pInst)
{
    printf("[FaceDetect]facedetect thread created\n");

    String face_cascade = "/usr/local/share/OpenCV/haarcascades/haarcascade_frontalface_default.xml";
    String eye_cascade = "/usr/local/share/OpenCV/haarcascades/haarcascade_eye.xml";

    cap.set(CV_CAP_PROP_FORMAT, CV_8UC3);
    cap.set(CV_CAP_PROP_FRAME_WIDTH, 640);
    cap.set(CV_CAP_PROP_FRAME_HEIGHT, 480);


    if(!cap.open()){
        cerr << "\n[FaceDetect]Can't Open Camera\n" << endl;
        return NULL;
    }

    if(!face.load(face_cascade) || !eye.load(eye_cascade)){
	cout << "\n[FaceDetect]Cascade file open failed\n" << endl;
	return NULL;
    }
    if(cameramode == 1 ) {
	namedWindow("Face", 1);
    }
    while(1)
    {
        bool frame_valid = true;
	bool detected = false;
        Mat frame_original;
        Mat frame;

        try{
            //cap >> frame_original;           
	    cap.grab();
	    cap.retrieve(frame_original);
        }
	catch(Exception& e){
            cerr << "\n[FaceDetet]Execption occurred.\n" << endl;
            frame_valid = false;
        }
	// test
	//Size tmpSize = frame_original.size();
	//cout << "size" << tmpSize.height << ", " << tmpSize.width << endl;
	//cout << "loop start" << endl;
        if(frame_valid)
	{
		faceresult->height = 0;
		faceresult->width = 0;
		faceresult->y = 0;
		faceresult->x = 0;
		faceresult->eyeResult[0].width = 0;
		faceresult->eyeResult[0].height = 0;
		faceresult->eyeResult[0].y = 0;
		faceresult->eyeResult[0].x = 0;
		faceresult->eyeResult[1].width =0;	
		faceresult->eyeResult[1].height = 0;
		faceresult->eyeResult[1].y = 0;
		faceresult->eyeResult[1].x = 0;
	    //cout << " frame_valid" << endl;
            try{

                Mat grayframe;
                cvtColor(frame_original, grayframe, CV_BGR2GRAY);
                equalizeHist(grayframe, grayframe);

                vector<Rect> face_pos;

		//cout << "point1" << endl;
                face.detectMultiScale(grayframe, face_pos, scaleFactor, minNeighbors, 0 | CV_HAAR_SCALE_IMAGE, Size(minFaceSizeY, minFaceSizeX), Size(maxFaceSizeY,maxFaceSizeX)); // frame is 480x640

		//cout << "point2" << endl;
                for(int i=0;i<face_pos.size();i++){
                    Point lb(face_pos[i].x + face_pos[i].width, face_pos[i].y + face_pos[i].height); 
    	            Point tr(face_pos[i].x, face_pos[i].y);
		    faceresult->height = face_pos[i].height;
		    faceresult->width = face_pos[i].width;
		    faceresult->y = face_pos[i].y;
		    faceresult->x = face_pos[i].x;
	
                //region of eye detect
		    
		    vector<Rect> eye_pos;
		    
		    Mat roi = grayframe(face_pos[i]);
		    eye.detectMultiScale(roi, eye_pos, scaleFactor, minNeighbors, 0 | CV_HAAR_SCALE_IMAGE, Size(minEyeSizeY, minEyeSizeX), Size(maxEyeSizeY,maxEyeSizeX));
		    if(eye_pos.size() > 1 ){
                    	rectangle(frame_original, lb, tr, Scalar(0, 255, 0), 3, 4, 0);
			faceresult->eyeResult[0].width = eye_pos[0].width;
			faceresult->eyeResult[0].height = eye_pos[0].height;
			faceresult->eyeResult[0].y = eye_pos[0].y;
			faceresult->eyeResult[0].x = eye_pos[0].x;
		    }
		    else{
                  	rectangle(frame_original, lb, tr, Scalar(0, 0, 255), 3, 4, 0);
			faceresult->eyeResult[0].width = 0;
			faceresult->eyeResult[0].height = 0;
			faceresult->eyeResult[0].y = 0;
			faceresult->eyeResult[0].x = 0;
		    }
		    

		}
		/*
		if(detected == false){
			faceresult->height = 0;
			faceresult->width = 0;
			faceresult->y = 0;
			faceresult->x = 0;
			faceresult->eyeResult[0].width = 0;
			faceresult->eyeResult[0].height = 0;
			faceresult->eyeResult[0].y = 0;
			faceresult->eyeResult[0].x = 0;
			faceresult->eyeResult[1].width =0;	
			faceresult->eyeResult[1].height = 0;
			faceresult->eyeResult[1].y = 0;
			faceresult->eyeResult[1].x = 0;
		}
*/
		/*else
		{
		     cout << "[FaceDetect]face-"<<faceresult->faceHeight<<"/"<<faceresult->faceWidth
			<<", eyeLeft-"<<faceresult->eyeHeight[0]<<"/"<<faceresult->eyeWidth[0]
			<<", eyeRight-"<<faceresult->eyeHeight[1]<<"/"<<faceresult->eyeWidth[1]<<endl;
		}*/
                if(cameramode == 1){
			imshow("Face", frame_original);
		}
            }
	    catch(Exception& e){
                cerr << "Exception occurred. face" << endl;
		cout << "[FaceDetect]Error" << endl;
            }
            if(waitKey(10) >= 0) continue;
        }
	cout << "end loop" << endl;
    }

    return NULL;
}

}



