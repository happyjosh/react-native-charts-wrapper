import {PropTypes} from 'react';
import {
  requireNativeComponent,
  View
} from 'react-native';

import BarLineChartBase from './BarLineChartBase';
import {combinedData} from './ChartDataConfig';
import {nativeOnly} from "./ChartCallbackNativeOnly";
import {floatLabelStyleConfig} from "./FloatLabelConfig";

const iface = {
  name: 'CombinedChart',
  propTypes: {
    ...BarLineChartBase.propTypes,

    data: combinedData,
    rightSelectLabel: PropTypes.shape({
      ...floatLabelStyleConfig,
    }),
    bottomSelectLabel: PropTypes.shape({
      ...floatLabelStyleConfig,
    }),
  }
};

export default requireNativeComponent('RNCombinedChart', iface, nativeOnly);
