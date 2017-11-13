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
import {barData, bottomLineData, candleData, line2Data, lineData, times} from "./testData";
import ChartBinder from './chartBinder'

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'stretch',
    backgroundColor: 'transparent'
  }
});

export default class Combined extends Component {
  _viewHeights = {chart1: 0, chart2: 0};

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
                highlightEnabled: false,
              }
            }
          ],
        },
        lineData: {
          dataSets: [
            {
              values: bottomLineData,
              label: 'Sine function',

              config: {
                drawValues: false,
                colors: [processColor('#ff9500')],
                mode: "CUBIC_BEZIER",
                drawCircles: false,
                lineWidth: 1,
                axisDependency: 'RIGHT',
                highlightColor: processColor('black'),
                highlightLineWidth: 1,
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
              labelCountForce: false,
              textSize: 10,
              valueFormatter: {
                type: 'simpleTime',
                values: times
              }
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
              drawGridLines: true,
              position: 'BOTTOM',
              labelCount: 8,
              labelCountForce: true,
              textSize: 10,
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

    //对齐两个图表
    // this.alignCharts();

    setTimeout(() => {
      // console.log(findNodeHandle(this.refs['chart1']));
      const manualYOffset = this._viewHeights.chart1;
      ChartBinder.bindChart(findNodeHandle(this.refs['chart1']), findNodeHandle(this.refs['chart2']), manualYOffset);
    }, 500);
  }

  handleSelect(event, chartRef) {
    let {entry, highlight} = event.nativeEvent;
    if (event.nativeEvent && highlight) {
      //选中
      let manualYOffset = 'chart1' === chartRef ? -this._viewHeights.chart1 : this._viewHeights.chart1;

      //高亮联动
      UIManager.dispatchViewManagerCommand(
        findNodeHandle(this.refs[chartRef]), // 找到与NativeUI组件对应的JS组件实例
        UIManager.RNCombinedChart.Commands.highlightByOthers,
        [highlight.x, highlight.y, highlight.touchY, manualYOffset]
      );
    } else {
      //取消选中
      UIManager.dispatchViewManagerCommand(
        findNodeHandle(this.refs[chartRef]), // 找到与NativeUI组件对应的JS组件实例
        UIManager.RNCombinedChart.Commands.hideHighlight,
        null
      );
    }
  }

  _initExtraOffsets = {chart1Left: 0, chart1Right: 0, chart2Left: 0, chart2Right: 0};

  /*对齐图表*/
  alignCharts() {
    this.getChart1ExtraOffset();
  }

  getChart1ExtraOffset() {
    this.getChartExtraOffset('chart1');
  }

  getChart2ExtraOffset() {
    this.getChartExtraOffset('chart2');
  }

  /*得到表1的偏移量,保存，并开始获取图表2的偏移量*/
  handleChart1ExtraOffset(event) {
    // this.handleChartExtraOffset(event, 'chart2');
    let {extraLeftOffset, extraRightOffset} = event.nativeEvent;
    this._initExtraOffsets.chart1Left = extraLeftOffset;
    this._initExtraOffsets.chart1Right = extraRightOffset;

    this.getChart2ExtraOffset();
  }

  /*得到表2的偏移量，开始计算需要调整的偏移量*/
  handleChart2ExtraOffset(event) {
    let {extraLeftOffset, extraRightOffset} = event.nativeEvent;
    this._initExtraOffsets.chart2Left = extraLeftOffset;
    this._initExtraOffsets.chart2Right = extraRightOffset;

    //临时变量解构，避免调用时代码过长
    let {chart1Left, chart1Right, chart2Left, chart2Right} = this._initExtraOffsets;

    if (chart2Left < chart1Left) {
      this.setChartOneExtraOffset('chart2', 'left', chart1Left - chart2Left);
    } else {
      this.setChartOneExtraOffset('chart1', 'left', chart2Left - chart1Left);
    }
    if (chart2Right < chart1Right) {
      this.setChartOneExtraOffset('chart2', 'right', chart1Right - chart2Right);
    } else {
      this.setChartOneExtraOffset('chart1', 'right', chart2Right - chart1Right);
    }
  }

  setChartOneExtraOffset(chartRef, type, offset) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.refs[chartRef]), // 找到与NativeUI组件对应的JS组件实例
      UIManager.RNCombinedChart.Commands.setOneExtraOffset,
      [type, offset]
    );
  }

  getChartExtraOffset(chartRef) {
    UIManager.dispatchViewManagerCommand(
      findNodeHandle(this.refs[chartRef]), // 找到与NativeUI组件对应的JS组件实例
      UIManager.RNCombinedChart.Commands.getExtraOffset,
      null
    );
  }

  //调整图表2跟随图表1
  handleChartMatrixChange(event, chartRef) {
    console.log('handleChartMatrixChange');

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

        {/*<View style={{height: 20}}>*/}
        {/*<Text> selected entry</Text>*/}
        {/*<Text> {this.state.selectedEntry}</Text>*/}
        {/*</View>*/}


        <CombinedChart
          onLayout={(event) => {
            this._viewHeights.chart1 = event.nativeEvent.layout.height
          }}
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
          onSelect={(event) => console.log(event.nativeEvent)}
          // onSelect={(event) => this.handleSelect(event, 'chart2')}
          // onMatrixChange={(event) => this.handleChartMatrixChange(event, 'chart2')}
          // onGetExtraOffset={(event) => this.handleChart1ExtraOffset(event)}
          onSingleTapped={(event) => {
            console.log('click');
          }}
        />

        <CombinedChart
          onLayout={(event) => {
            this._viewHeights.chart2 = event.nativeEvent.layout.height
          }}
          ref='chart2'
          style={{flex: 1}}
          data={this.state.data2}
          xAxis={this.state.xAxis2}
          yAxis={this.state.yAxis2}
          description={{text: '2222'}}
          maxVisibleValueCount={16}//屏幕内放大到多少数量时可以显示值
          doubleTapToZoomEnabled={false}
          autoScaleMinMaxEnabled={true}
          // zoom={this.state.zoom2}
          legend={{enabled: false}}
          // onSelect={(event) => this.handleSelect(event, 'chart1')}
          // onMatrixChange={(event) => this.handleChartMatrixChange(event, 'chart1')}
          // onGetExtraOffset={(event) => this.handleChart2ExtraOffset(event)}
          rightSelectLabel={this.state.rightSelectLabel}
        />
      </View>
    );
  }
}