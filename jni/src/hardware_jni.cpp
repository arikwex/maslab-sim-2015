#include "hardware_jni_HardwareJNI.h"

#include <chrono>
#include <csignal>
#include <vector>
#include <pthread.h>

#include "lidar.h"
#include "lidar_range_sensor_data.h"
#include "localization.h"
#include "real_control.h"
#include "render.h"
#include "sensor_data.h"
#include "state_estimator.h"
#include "random.h"
#include "util.h"

pthread_t mainThread;
RealControl control;
loc::Particle best;

int running = 1;
void sig_handler(int signo) {
  if (signo == SIGINT) {
    running = 0;
  }
}

void *mainLoop(void *args) {
  Map testMap("maps/practice.map");
  StateEstimator estimator(testMap.getInitPose(), control);

  loc::ParticleFilter pf(testMap.getInitPose().x, testMap.getInitPose().y, testMap);
  while (running) {
    TimePoint curTime = chrono::system_clock::now();
    best = pf.update(LidarRangeSensorData(control));
    cout << "Best particle: " << best.pose.x/METERS_PER_UNIT - 2
	 << "," << best.pose.y/METERS_PER_UNIT - 3 << ","
	 << best.pose.theta << endl;
    cout << "Weight: " << best.weight.getProb() << endl;
    
    // Update our estimate
    RobotMotionDelta robotDelta = estimator.tick(curTime, nullptr);

    pf.step(robotDelta);
  }

  cout << "JNI exiting" << endl;

  return nullptr;
}

jint JNI_OnLoad(JavaVM *vm, void *reserved) {
  pthread_create(&mainThread, NULL, mainLoop, NULL);
  return JNI_VERSION_1_2;
}

void JNI_OnUnload(JavaVM *vm, void *reserved) {
  running = 0;
  pthread_join(mainThread, NULL);
}

void Java_hardware_jni_HardwareJNI_setLeftSpeed(JNIEnv *env, jclass cls, jdouble speed) {
  control.setLeftSpeed(speed);
}

void Java_hardware_jni_HardwareJNI_setRightSpeed(JNIEnv *env, jclass cls, jdouble speed) {
  control.setRightSpeed(speed);
}

jdouble Java_hardware_jni_HardwareJNI_getPoseX(JNIEnv *env, jclass cls) {
  return best.pose.x;
}

jdouble Java_hardware_jni_HardwareJNI_getPoseY(JNIEnv *env, jclass cls) {
  return best.pose.y;
}

jdouble Java_hardware_jni_HardwareJNI_getPoseTheta(JNIEnv *env, jclass cls) {
  return best.pose.theta;
}
