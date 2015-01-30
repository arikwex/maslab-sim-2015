#include <chrono>
#include <cmath>
#include <csignal>
#include <vector>

#include "lidar.h"
#include "lidar_range_sensor_data.h"
#include "localization.h"
#include "real_control.h"
#include "render.h"
#include "sensor_data.h"
#include "state_estimator.h"
#include "random.h"
#include "util.h"

#if EDISON

using namespace std;

int running = 1;

void sig_handler(int signo) {
  if (signo == SIGINT) {
    running = 0;
  }
}

Map getTestMap() {
  return Map("maps/test.map");
}

int main(int argc, char** argv) {
  rassert(argc == 2) << "Must give map as argument";
  signal(SIGINT, sig_handler);

  Map testMap(argv[1]);
  RealControl control;
  StateEstimator estimator(testMap.getInitPose(), control);

  // TODO: Fix interface
  loc::ParticleFilter pf(testMap.getInitPose().x, testMap.getInitPose().y, testMap);
  while (running) {
    TimePoint curTime = chrono::system_clock::now();
    loc::Particle best = pf.update(LidarRangeSensorData(control));
    // cout << "Pose: " << truePose << endl;
    cout << "Best particle: " << best.pose.x/METERS_PER_UNIT - 2
	 << "," << best.pose.y/METERS_PER_UNIT - 3 << ","
	 << best.pose.theta << endl;
    cout << "Weight: " << best.weight.getProb() << endl;
    //control.setLeftSpeed(0.2);
    //control.setRightSpeed(0.1);
    
    // Update our estimate
    RobotMotionDelta robotDelta = estimator.tick(curTime, nullptr);

    pf.step(robotDelta);
  }

  cout << "Exiting" << endl;

  return 0;
}

#endif
