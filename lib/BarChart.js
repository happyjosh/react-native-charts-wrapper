import {PropTypes} from 'react';
import {
  requireNativeComponent,
  View
} from 'react-native';

import BarLineChartBase from './BarLineChartBase';
import {barData} from './ChartDataConfig';
import {nativeOnly} from "./ChartCallbackNativeOnly";

const iface = {
  name: 'BarChart',
  propTypes: {
    ...BarLineChartBase.propTypes,

    drawValueAboveBar: PropTypes.bool,
    drawBarShadow: PropTypes.bool,

    data:  barData
  }
};

export default requireNativeComponent('RNBarChart', iface, nativeOnly);
