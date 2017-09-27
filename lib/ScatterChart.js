import {PropTypes} from 'react';
import {
  requireNativeComponent,
  View
} from 'react-native';

import BarLineChartBase from './BarLineChartBase';
import {scatterData} from './ChartDataConfig';
import {nativeOnly} from "./ChartCallbackNativeOnly";

const iface = {
  name: 'ScatterChart',
  propTypes: {
    ...BarLineChartBase.propTypes,

    data: scatterData
  }
};

export default requireNativeComponent('RNScatterChart', iface, nativeOnly);
