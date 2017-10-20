/**
 * Created by xudong on 02/03/2017.
 */

import React, {Component} from 'react';
import {
  UIManager, View, Text, StyleSheet, processColor, ToastAndroid, findNodeHandle, Touchable,
  TouchableWithoutFeedback, TouchableOpacity
} from 'react-native';

import {CombinedChart} from 'react-native-charts-wrapper';
import update from 'immutability-helper';
import _ from 'lodash';
import {barData, candleData, line2Data, lineData} from "./testData";

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'stretch',
    backgroundColor: 'transparent'
  }
});

export default class Combined extends Component {

  constructor() {
    super();
    this.state = {
      data1: {
        lineData: {
          dataSets: [
            {
              values: lineData,
              label: 'Sine function',

              config: {
                drawValues: false,
                colors: [processColor('#ff9500')],
                mode: "CUBIC_BEZIER",
                drawCircles: false,
                lineWidth: 1,
                axisDependency: 'RIGHT',
                highlightEnabled: false,
              }
            },
            {
              values: line2Data,
              label: 'Sine function',

              config: {
                drawValues: false,
                colors: [processColor('#007aff')],
                mode: "CUBIC_BEZIER",
                drawCircles: false,
                lineWidth: 1,
                axisDependency: 'RIGHT',
                highlightEnabled: false,
              }
            }
          ],
        },
        candleData: {
          dataSets: [{
            values: candleData,
            label: 'AAPL',
            config: {
              highlightColor: processColor('black'),
              highlightLineWidth: 1,

              shadowColor: processColor('black'),
              shadowWidth: 1,
              shadowColorSameAsCandle: true,
              increasingColor: processColor('#71BD6A'),
              increasingPaintStyle: 'fill',
              decreasingColor: processColor('#D14B5A'),
              axisDependency: 'RIGHT',
            }
          }],
        },
      },
      data2: {
        barData: {
          dataSets: [
            {
              values: barData,
              label: 'Zero line dataset',
              config: {
                color: processColor('blue'),
                axisDependency: 'RIGHT',
              }
            }
          ],
        }
      },
      rightSelectLabel: {
        enabled: true,
        textSize: 10,
        textColor: processColor('white'),
        backgroundColor: processColor('blue'),
        paddingLeft: 5,
        paddingTop: 2,
        paddingRight: 5,
        paddingBottom: 2,
      },
      bottomSelectLabel: {
        enabled: true,
        textSize: 10,
        textColor: processColor('white'),
        backgroundColor: processColor('blue'),
        paddingLeft: 5,
        paddingTop: 2,
        paddingRight: 5,
        paddingBottom: 2,
      },
      // xAxis1: {},
      // yAxis1: {},
      // xAxis2: {},
      // yAxis2: {},
    };

  }

  componentDidMount() {
    this.setState(
      update(this.state, {
          xAxis1: {
            $set: {
              drawGridLines: true,
              position: 'BOTTOM',
              labelCount: 8,
              labelCountForce: true,
              textSize: 10,
            }
          },
          yAxis1: {
            $set: {
              left: {
                enabled: false,
              },
              right: {
                drawGridLines: true,
                valueFormatter: '#',
                labelCount: 6,
                labelCountForce: true,
                textSize: 10,
                limitLines: [{
                  limit: 100,
                  lineColor: processColor('red'),
                  enableDashLine: true,
                  dashLineLength: 3,
                  dashSpaceLength: 6,
                  dashPhase: 0,
                },],
              }
            }
          },
          xAxis2: {
            $set: {
              enabled: false
            }
          },
          yAxis2: {
            $set: {
              left: {
                enabled: false,
              },
              right: {
                valueFormatter: '#',
                labelCount: 2,
                labelCountForce: true,
                textSize: 10,
              }
            }
          },
          floatYLabel1: {
            $set: {
              enabled: true,
              textSize: 10,
              textColor: processColor('white'),
              backgroundColor: processColor('red'),
              paddingLeft: 5,
              paddingTop: 2,
              paddingRight: 5,
              paddingBottom: 2,
              value: 100,
            }
          },
        }
      ));

    //对其两个图标
    this.getChart1ExtraOffset();
  }

  static displayName = 'Combined';

  handleSelect(event) {
    let entry = event.nativeEvent;
    if (entry == null) {
      this.setState({...this.state, selectedEntry: null})
    } else {
      this.setState({...this.state, selectedEntry: JSON.stringify(entry)})
    }
  }

  getChart1ExtraOffset() {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.refs['chart1']), // 找到与NativeUI组件对应的JS组件实例
      UIManager.RNCombinedChart.Commands.getExtraOffset,
      null
    );
  }

  //得到图标1的位置，使图标2与之对齐
  handleChartExtraOffset(event, chartRef) {
    console.log('handleChart1ExtraOffset');

    let {extraLeftOffset, extraRightOffset} = event.nativeEvent;
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.refs[chartRef]), // 找到与NativeUI组件对应的JS组件实例
      UIManager.RNCombinedChart.Commands.setExtraOffset,
      [extraLeftOffset, extraRightOffset]
    );

  }

  //调整图表2跟随图表1
  handleChartMatrixChange(event, chartRef) {
    let {matrix} = event.nativeEvent;
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.refs[chartRef]), // 找到与NativeUI组件对应的JS组件实例
      UIManager.RNCombinedChart.Commands.changeMatrix,
      matrix
    );
  }

  stopAllChartsDeceleration() {
    this.stopChartDeceleration('chart1');
    this.stopChartDeceleration('chart2');
  }

  stopChartDeceleration(chartRef) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.refs[chartRef]), // 找到与NativeUI组件对应的JS组件实例
      UIManager.RNCombinedChart.Commands.stopDeceleration,
      null
    );
  }

  render() {
    return (
      <View
        style={{flex: 1}}
        onStartShouldSetResponder={(event) => {
          this.stopAllChartsDeceleration();
          return false;
        }}
      >

        {/*<View style={{height: 80}}>*/}
        {/*<Text> selected entry</Text>*/}
        {/*<Text> {this.state.selectedEntry}</Text>*/}
        {/*</View>*/}


        <CombinedChart
          ref='chart1'
          style={{flex: 2}}
          data={this.state.data1}
          marker={this.state.marker}
          description={{text: '1111'}}
          legend={this.state.legend}
          xAxis={this.state.xAxis1}
          yAxis={this.state.yAxis1}
          floatYLabel={this.state.floatYLabel1}
          maxVisibleValueCount={16}//屏幕内放大到多少数量时可以显示值
          autoScaleMinMaxEnabled={true}
          doubleTapToZoomEnabled={false}
          // zoom={{scaleX: 2, scaleY: 1, xValue: 1, yValue: 1}}//默认缩放
          scaleYEnabled={false}
          // pinchZoom={true}
          rightSelectLabel={this.state.rightSelectLabel}
          bottomSelectLabel={this.state.bottomSelectLabel}
          onSelect={this.handleSelect.bind(this)}
          onMatrixChange={(event) => this.handleChartMatrixChange(event, 'chart2')}
          onGetExtraOffset={(event) => this.handleChartExtraOffset(event, 'chart2')}
        />

        <CombinedChart
          ref='chart2'
          style={{flex: 1}}
          data={this.state.data2}
          xAxis={this.state.xAxis2}
          yAxis={this.state.yAxis2}
          description={{text: '2222'}}
          maxVisibleValueCount={16}//屏幕内放大到多少数量时可以显示值
          doubleTapToZoomEnabled={false}
          autoScaleMinMaxEnabled={true}
          zoom={this.state.zoom2}
          legend={{enabled: false}}
          onMatrixChange={(event) => this.handleChartMatrixChange(event, 'chart1')}
        />
      </View>
    );
  }
}