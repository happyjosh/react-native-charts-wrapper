import {PropTypes} from 'react';
import {
  requireNativeComponent,
  View
} from 'react-native';

import BarLineChartBase from './BarLineChartBase';
import {lineData} from './ChartDataConfig';
import {nativeOnly} from "./ChartCallbackNativeOnly";

const iface = {
  name: 'LineChart',
  propTypes: {
    ...BarLineChartBase.propTypes,

    data: lineData,
  }
};

export default requireNativeComponent('RNLineChart', iface, nativeOnly);
