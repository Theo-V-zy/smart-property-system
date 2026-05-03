create database if not exists smart_property default character set utf8mb4 collate utf8mb4_general_ci;
use smart_property;

drop table if exists user_session;
drop table if exists service_evaluation;
drop table if exists service_order;
drop table if exists lost_found_item;
drop table if exists notice;
drop table if exists sys_user;

create table sys_user (
    id bigint primary key auto_increment comment '主键',
    role varchar(20) not null comment '身份 OWNER/STAFF',
    username varchar(50) not null comment '登录账号',
    password_hash varchar(64) not null comment '密码摘要',
    name varchar(50) not null comment '姓名',
    phone varchar(20) not null comment '手机号',
    community_name varchar(100) default null comment '小区名称',
    address varchar(255) default null comment '住址',
    business_scope varchar(100) default null comment '负责业务',
    avatar varchar(500) default null comment '头像',
    created_at datetime not null comment '创建时间',
    updated_at datetime not null comment '更新时间',
    unique key uk_user_role_username (role, username)
) comment='用户表';

create table notice (
    id bigint primary key auto_increment comment '主键',
    title varchar(100) not null comment '标题',
    content text not null comment '内容',
    publisher_id bigint not null comment '发布人ID',
    publisher_name varchar(50) not null comment '发布人',
    pinned tinyint default 0 comment '是否置顶',
    created_at datetime not null comment '创建时间'
) comment='小区通知表';

create table lost_found_item (
    id bigint primary key auto_increment comment '主键',
    type varchar(20) not null comment 'FOUND/LOST',
    title varchar(100) not null comment '标题',
    description text not null comment '描述',
    pickup_location varchar(255) not null comment '地点',
    contact_name varchar(50) not null comment '联系人',
    contact_phone varchar(20) not null comment '联系电话',
    images_json longtext comment '图片JSON',
    status varchar(20) not null comment '状态',
    publisher_id bigint not null comment '发布人ID',
    publisher_name varchar(50) not null comment '发布人',
    created_at datetime not null comment '创建时间',
    updated_at datetime not null comment '更新时间'
) comment='失物招领表';

create table service_order (
    id bigint primary key auto_increment comment '主键',
    order_no varchar(40) not null comment '工单号',
    owner_id bigint not null comment '业主ID',
    owner_name varchar(50) not null comment '业主姓名',
    phone varchar(20) not null comment '联系电话',
    category varchar(30) not null comment '业务类型',
    subtype varchar(50) not null comment '问题类型',
    description text not null comment '问题描述',
    images_json longtext comment '图片JSON',
    address varchar(255) not null comment '住址',
    status varchar(20) not null comment '状态',
    reply varchar(500) default null comment '物业回复',
    handler_id bigint default null comment '处理人ID',
    handler_name varchar(50) default null comment '处理人',
    handle_record text comment '处理记录',
    created_at datetime not null comment '创建时间',
    updated_at datetime not null comment '更新时间'
) comment='报修报失反馈工单表';

create table service_evaluation (
    id bigint primary key auto_increment comment '主键',
    order_id bigint not null comment '工单ID',
    owner_id bigint not null comment '业主ID',
    service_score int not null comment '服务态度',
    quality_score int not null comment '维修质量',
    speed_score int not null comment '处理速度',
    content varchar(500) not null comment '评价内容',
    images_json longtext comment '图片JSON',
    created_at datetime not null comment '创建时间',
    unique key uk_order_id (order_id)
) comment='服务评价表';

create table user_session (
    id bigint primary key auto_increment comment '主键',
    user_id bigint not null comment '用户ID',
    token varchar(100) not null comment '登录凭证',
    expires_at datetime not null comment '过期时间',
    created_at datetime not null comment '创建时间',
    unique key uk_token (token)
) comment='登录会话表';

insert into sys_user(role, username, password_hash, name, phone, community_name, address, business_scope, avatar, created_at, updated_at)
values
('OWNER', 'owner01', md5('123456'), '张三', '13812345678', '幸福花园小区', '1栋1单元101', null, '', now(), now()),
('STAFF', 'staff01', md5('123456'), '李师傅', '13912345678', null, null, '维修、保洁', '', now(), now());

insert into notice(title, content, publisher_id, publisher_name, pinned, created_at)
values
('关于5月园区电梯维护的通知', '5月3日 09:00-12:00 将对1栋与2栋电梯进行例行检修，请居民合理安排出行。', 2, '李师傅', 1, now()),
('小区便民服务周启动', '本周物业服务中心提供水电咨询、钥匙代配和家政对接服务，欢迎到前台登记。', 2, '李师傅', 0, now());

insert into lost_found_item(type, title, description, pickup_location, contact_name, contact_phone, images_json, status, publisher_id, publisher_name, created_at, updated_at)
values
('FOUND', '黑色钱包', '在2号楼门厅拾到黑色钱包一个，请失主联系物业认领。', '2号楼门厅前台', '李师傅', '13912345678', '[]', 'PUBLISHED', 2, '李师傅', now(), now()),
('FOUND', '儿童书包', '游乐区长椅发现蓝色儿童书包，内有水杯与绘本。', '儿童游乐区服务台', '李师傅', '13912345678', '[]', 'PUBLISHED', 2, '李师傅', now(), now());

insert into service_order(order_no, owner_id, owner_name, phone, category, subtype, description, images_json, address, status, reply, handler_id, handler_name, handle_record, created_at, updated_at)
values
('WX202605011001', 1, '张三', '13812345678', 'REPAIR', '水管漏水', '厨房水槽下方持续渗水，希望尽快安排维修。', '[]', '幸福花园小区 1栋1单元101', 'PROCESSING', '已安排维修人员上门，请保持电话畅通。', 2, '李师傅', '已电话确认情况，明日上午携带配件上门。', now(), now()),
('WX202605011002', 1, '张三', '13812345678', 'FEEDBACK', '灯光昏暗', '单元门口照明较暗，夜间回家不方便。', '[]', '幸福花园小区 1栋1单元101', 'COMPLETED', '问题已处理完成，欢迎评价。', 2, '李师傅', '已更换感应灯与灯泡，夜间亮度恢复正常。', now(), now());

insert into service_evaluation(order_id, owner_id, service_score, quality_score, speed_score, content, images_json, created_at)
values
(2, 1, 5, 5, 4, '师傅上门很及时，处理态度认真，整体很满意。', '[]', now());
