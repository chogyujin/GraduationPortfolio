# Driving assistance System using IoT
IoT를 차량 내부에 접목하여 운전자가 안전하고 편하게 운전할 수 있도록 도와줍니다.

## 소개
#### 개인화
* 정확한 Sensing을 위해 개인의 신체나 건강상태 등에 맞춰 서비스를 제공합니다.
#### 사고예방
* 졸음 상태를 감지해 다양한 Actuator를 적용함으로써 졸음운전 사고를 예방합니다.
#### 응급신고
* 위험 상황을 빠르게 파악하여 운전자가 신고할 수 없는 상황에서도 자동적으로 신고할 수 있습니다.

## 시스템 구성도
<img src="https://user-images.githubusercontent.com/33562226/51516598-4f220e80-1e5b-11e9-8229-41130ff5a520.PNG" width="800" height="380">

## 안드로이드 구성도
<img src="https://user-images.githubusercontent.com/33562226/51517464-323b0a80-1e5e-11e9-924a-e865b7a0010f.PNG" width="900" height="450">

## 주요 기능
#### 1. 개인화
<div>
<img src="https://user-images.githubusercontent.com/33562226/51518301-cdcd7a80-1e60-11e9-855f-f202ec0d7d9f.PNG" width="550" height="350">
     
<img src="https://user-images.githubusercontent.com/33562226/51518297-cc03b700-1e60-11e9-8a5f-423f0927249a.png" width="180" height="350">
</div>

- 운전자의 상태를 개인화하기 위해 30초 동안 의자와 사람 사이 거리, 심박 수, 호흡수를 측정합니다.
- 측정 결과에 따라 Actuator 판단 기준을 조정합니다.
- 질병 등 건강정보를 입력 받고, 질병에 따라 상태 판단 범위를 조정합니다.
#### 2. 졸음 운전 예방
<img src="https://user-images.githubusercontent.com/33562226/51518844-93fd7380-1e62-11e9-9519-fa2b0826f53b.PNG" width="550" height="350">

- 졸음 운전을 예방하기 위해 졸음 판단 단계를 2단계로 나누어 동작하도록 했습니다.
- 판단
  1단계 : 눈이 2sec 이상 감긴 경우
  2단계 : 운전자의 머리와 좌석 사이의 간격이 정상 범위를 넘어선 경우
          or 호흡 세기가 정상 범위의 70% 미만으로 감소한 경우
- 동작
  1단계 : 차량 내부 온도를 낮추고 의자에 Vibration을 동작 시킵니다.
  2단계 : 1단계 동작과 더불어 LED 조명 및 부저를 통한 알림을 통해 운전자의 졸음을 깨웁니다.
