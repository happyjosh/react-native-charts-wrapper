import {PropTypes} from 'react';
import {
  View
} from 'react-native';

import ChartBase from './ChartBase';
import {yAxisIface} from './AxisIface';
import {floatLabelStyleConfig} from './FloatLabelConfig';

const iface = {
  propTypes: {
    ...ChartBase.propTypes,

    drawGridBackground: PropTypes.bool,
    gridBackgroundColor: PropTypes.number,

    drawBorders: PropTypes.bool,
    borderColor: PropTypes.number,
    borderWidth: PropTypes.number,

    maxVisibleValueCount: PropTypes.number,
    autoScaleMinMaxEnabled: PropTypes.bool,
    keepPositionOnRotation: PropTypes.bool,

    scaleEnabled: PropTypes.bool,
    scaleXEnabled: PropTypes.bool,
    scaleYEnabled: PropTypes.bool,
    dragEnabled: PropTypes.bool,
    pinchZoom: PropTypes.bool,
    doubleTapToZoomEnabled: PropTypes.bool,

    yAxis: PropTypes.shape({
      left: PropTypes.shape(yAxisIface),
      right: PropTypes.shape(yAxisIface)
    }),
    zoom: PropTypes.shape({
      scaleX: PropTypes.number.isRequired,
      scaleY: PropTypes.number.isRequired,
      xValue: PropTypes.number.isRequired,
      yValue: PropTypes.number.isRequired,
      axisDependency: PropTypes.oneOf(['LEFT', 'RIGHT'])
    }),

    scaleLimit: PropTypes.shape({
      scaleMinX: PropTypes.number,
      scaleMinY: PropTypes.number,
      scaleMaxX: PropTypes.number,
      scaleMaxY: PropTypes.number,
    }),

    floatYLabel: PropTypes.shape({
      ...floatLabelStyleConfig,
      value: PropTypes.number,
    }),

    floatYLine: PropTypes.shape({
      lineColor: PropTypes.number,
      lineWidth: PropTypes.number,
      enableDashLine: PropTypes.bool,
      dashLineLength: PropTypes.number,
      dashSpaceLength: PropTypes.number,
      dashPhase: PropTypes.number,
    })
  }
};

export default iface;
