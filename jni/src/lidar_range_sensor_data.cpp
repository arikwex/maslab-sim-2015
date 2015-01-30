#include "lidar_range_sensor_data.h"

#include "random.h"

LidarRangeSensorData::LidarRangeSensorData(const RealControl& control) : control(control) {
  if (sensors.empty()) {
    for (int i = 0; i < 360; ++i) {
      if (i % 10 != 0) continue;
      sensors.emplace_back(0.076, i*(2*PI/360.0), i*(2*PI/360.0));
    }
  }
}

Prob LidarRangeSensorData::computeProb(const RobotPose& pose, const Map &map) const {
  // cout << "sig done" << endl;
  Prob out = Prob::makeFromLinear(1.0);
  for (int i = 0; i < 360; ++i) {
    if (i % 10 != 0) continue;
    uint32_t rangeInt = control.lidar.getSample(i);
    int sensorIndex = i/10;
    rassert(sensorIndex < sensors.size());
    if (rangeInt == 0xFFFFFFFF) continue;
    double range = rangeInt/1000.0;
    // Skip invalid readings
    // TODO: Perhaps use a low-weighted correlation to expected 
    if (range < 0) continue;

    Vector origin = sensors[sensorIndex].getGlobalPos(pose);
    double sensorTheta = sensors[sensorIndex].getGlobalTheta(pose);
    Vector endpoint = getEndpoint(origin, sensorTheta, range);
    Vector endpointClamped = map.clampPoint(endpoint);
    Vector closest = map.getClosest(endpointClamped.x, endpointClamped.y);
    double d = dist(closest.x, closest.y, endpoint.x, endpoint.y);

    // TUNE: stddev of PDF should be increased for more stability, reduced for
    // a more dynamic response.
    out = Prob::andProb(out, Prob::makeFromLinear(gaussianPDF(1.0, d)));
  }
  return out;
}

vector<RobotVector> LidarRangeSensorData::sensors = {};
