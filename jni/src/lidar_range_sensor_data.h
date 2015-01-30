#ifndef LIDAR_RANGE_SENSOR_DATA_H
#define LIDAR_RANGE_SENSOR_DATA_H

#include "real_control.h"
#include "robot.h"
#include "sensor_data.h"

class LidarRangeSensorData : public SensorData {
 public:
  static vector<RobotVector> sensors;
  const RealControl& control;

  LidarRangeSensorData();  
  Prob computeProb(const RobotPose& pose, const Map &map) const override;
};

#endif
