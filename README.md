# 项目特性简介

这是一个roguelike风格的对战小游戏，支持网络多人联机。

演示视频：

- 保存、加载和录像：https://www.bilibili.com/video/BV15iCKYREEG
- 并发的生物决策：https://www.bilibili.com/video/BV1HHCKYNEFL
- 网络通信：https://www.bilibili.com/video/BV14gCKYkEZ8

**技术要点：**

- **并发**：用多线程实现游戏中生物体的自主行为
  - 每个生物体的移动、攻击等行为决策使用独立的算法
  - 特别处理线程race condition（两个生物体不能占据同一个tile，对同一生物体的两个攻击行为应该先后发生作用，等）
- **构建**：支持项目自动化构建
  - 使用gradle进行所有第三方库的依赖管理和构建
- **测试**：编写junit单元测试用例
  - 代码测试覆盖率不低于50%（intellij IDEA + run with coverage）
- **IO**：提供游戏保存功能
  - 地图保存/地图加载
  - 进度保存/进度恢复
  - 游戏过程录制/回放
- **网络通信**：支持网络对战
  - 支持多方（大于两方）对战
  - 使用NIO Selector实现
  - 通信过程全局状态一致（所有玩家看到的游戏过程完全一样），可通过各方分别录制游戏过程后进行比对验证



