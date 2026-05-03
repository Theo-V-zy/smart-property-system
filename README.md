# 智慧物业移动服务平台

本项目是《移动软件开发技术》课程期末考查项目，选题为“掌上物业”。系统围绕业主与物业工作人员两类用户展开，提供账号认证、报修报失、问题反馈、工单处理、服务评价、失物招领、小区通知与个人资料维护等功能，目标是实现一个具备完整业务闭环的移动端物业服务平台。

## 项目亮点

- 双角色体系：区分业主和物业人员，不同身份拥有不同首页入口与业务权限
- 工单闭环：支持提交、处理、查看进度、完成评价的完整流程
- 前后端分离：前端聚焦移动体验，后端负责鉴权、业务规则和数据持久化
- 报告配套完整：附带 HTML 版实验报告，便于直接导出 PDF 提交

## 技术栈

- 前端：Vue 3 + Vite + Vant + Pinia + Axios
- 后端：Spring Boot 3 + MyBatis
- 数据库：MySQL 8

## 目录结构

```text
.
├── report/                         # 实验报告 HTML
├── smart-property-backend/         # Spring Boot 后端
└── smart-property-frontend/        # Vue 3 移动端前端
```

## 功能模块

### 业主端

- 登录与注册
- 报修、报失、问题反馈提交
- 我的工单记录查看
- 服务评价
- 小区通知查看
- 失物招领查看
- 个人资料维护与修改密码

### 物业端

- 登录与注册
- 工单列表与处理详情
- 工单状态更新与回复业主
- 小区通知发布
- 失物招领发布
- 个人资料维护与修改密码

## 环境要求

- Node.js 18+
- JDK 17+
- Maven 3.9+
- MySQL 8.x

## 数据库初始化

初始化脚本位置：

- `smart-property-backend/src/main/resources/db/schema.sql`

默认会创建数据库 `smart_property`，并插入一组演示数据。

后端数据库配置已改为“环境变量优先”的方式，支持以下变量：

- `MYSQL_HOST`
- `MYSQL_PORT`
- `MYSQL_DATABASE`
- `MYSQL_USERNAME`
- `MYSQL_PASSWORD`

如果不设置这些变量，默认使用本地开发配置：

- Host：`127.0.0.1`
- Port：`3306`
- Database：`smart_property`
- Username：`root`
- Password：`we005218`

如果你准备将项目公开到 GitHub，建议把这些值替换为你自己的本地测试配置，不要直接复用敏感账号。

## 启动方式

### 1. 启动后端

先打包：

```bash
cd smart-property-backend
mvn -q -DskipTests package
```

再运行：

```bash
cd smart-property-backend
java -jar target/smart-property-backend-1.0.0.jar
```

后端默认端口：`8081`

### 2. 启动前端

```bash
cd smart-property-frontend
npm install
npm run dev
```

前端默认地址：`http://127.0.0.1:5173`

## 演示账号

以下为项目内置测试账号，仅用于课程演示：

- 业主账号：`owner01`
- 业主密码：`123456`
- 物业账号：`staff01`
- 物业密码：`123456`

## 实验报告

实验报告已整理为 HTML 文件：

- `report/smart-property-report.html`

可直接在浏览器中打开并导出为 PDF。

## 开源说明

本项目适合作为课程项目学习与交流使用，已补充：

- 根目录 `.gitignore`
- `MIT` 开源许可证
- 适合 GitHub 展示的项目说明文档

如果你准备上传到 GitHub，建议不要提交 `node_modules`、`target`、IDE 配置和本地缓存文件。

## License

This project is licensed under the MIT License.
