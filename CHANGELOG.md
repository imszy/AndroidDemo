# Changelog

All notable changes to the PomodoroTimer Android application will be documented in this file.

## [Unreleased]

### Added
- 详细活动历史记录功能
  - 增强活动记录，存储开始和结束时间
  - 添加按小时查看Pomodoro活动的详细视图
  - 点击日期可查看该日24小时活动时间线
  - 活动类型用不同颜色显示：番茄工作(红色)、短休息(浅绿色)、长休息(深绿色)

### Fixed
- 修复DailyDetailView中无法正确显示番茄时间、短休息和长休息记录的问题
- 改进日期匹配算法，保证记录可以正确按日期和小时过滤
- 增加调试日志和错误处理，提升应用稳定性
- 添加测试数据生成功能，方便验证功能正确性

## [0.4.0] - 2023-08-20

### Added
- Google Play商店发布准备
  - 添加隐私政策Activity和布局
  - 添加关于页面和应用版本信息
  - 添加菜单选项
  - 支持辅助功能访问
  - 添加应用描述和元数据

### Changed
- 优化构建配置
  - 启用R8代码混淆和资源压缩
  - 支持应用分发(App Bundle)格式
  - 更新应用ID为com.pomodorotimer.focus
  - 更新应用名称为"Pomodoro Focus"

## [0.3.0] - 2023-08-16

### Removed
- User-facing changelog feature
  - Deleted ChangelogManager class
  - Removed changelog dialog and button from UI
  - Simplified TimerPreferences class by removing changelog tracking

## [0.2.0] - 2023-08-15

### Added
- Customizable timer durations through settings dialog
  - User can set custom durations for Pomodoro, Short Break, and Long Break
  - Settings are persisted using SharedPreferences
- Device vibration on timer completion
  - Added vibration permission to AndroidManifest.xml
  - Implemented vibrate() method that triggers when any timer completes

### Changed
- Refactored timer mode switching to use settings from preferences
- Extracted timer settings into TimerPreferences class for better organization
- Updated UI to include Settings button

## [0.1.0] - 2023-08-14

### Added
- Initial implementation of Pomodoro Timer
- Basic Pomodoro technique functionality
  - 25-minute work sessions (Pomodoro)
  - 5-minute short breaks
  - 15-minute long breaks after 4 Pomodoros
- Modern UI with tomato-themed color scheme
- Timer modes selection (Pomodoro, Short Break, Long Break)
- Timer display with minutes and seconds
- Progress bar showing elapsed time
- Start/Pause and Reset buttons
- Session counter
- Auto-switching between work and break sessions
- Audio notification when timer completes
- Support for portrait and landscape orientations 