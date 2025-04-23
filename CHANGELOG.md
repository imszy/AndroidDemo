# Changelog

All notable changes to the PomodoroTimer Android application will be documented in this file.

## [Unreleased]

### Added
- 详细活动历史记录功能
  - 增强活动记录，存储开始和结束时间
  - 添加按小时查看Pomodoro活动的详细视图
  - 点击日期可查看该日24小时活动时间线
  - 活动类型用不同颜色显示：番茄工作(红色)、短休息(浅绿色)、长休息(深绿色)
  - 点击具体活动记录可显示详细时间信息对话框，包括开始时间、结束时间和持续时长

### Changed
- 简化活动历史日历视图，所有活动日期统一使用绿色标记
- 改进日历视图界面，添加提示引导用户点击日期查看详情
- 优化设置对话框布局
  - 调整深色模式和语言下拉列表的宽度，确保内容完全显示
  - 重新平衡文本和控件的空间分配，提升用户体验

### Fixed
- 修复DailyDetailView中无法正确显示番茄时间、短休息和长休息记录的问题
- 修复活动记录显示开始和结束时间相同的问题，现在正确显示实际持续时间
- 修复活动记录不遵循用户自定义时长设置的问题，现在记录将反映用户在设置中调整的时长
- 修复设置对话框中出现重复"语言"选项的问题，并增加语言下拉列表宽度确保选项完整显示

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