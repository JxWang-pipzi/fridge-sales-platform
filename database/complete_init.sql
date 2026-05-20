-- =============================================
-- 冰箱销售网站完整数据库初始化脚本
-- 数据库：fridge_sales
-- 更新时间：2026-03-05
-- =============================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS fridge_sales DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE fridge_sales;

-- =============================================
-- 1. 用户表（sys_user）
-- =============================================
DROP TABLE IF EXISTS sys_user;
CREATE TABLE sys_user (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    username VARCHAR(50) NOT NULL COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密存储）',
    phone VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    email VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    avatar VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色（user-普通用户，admin-管理员）',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_username (username),
    KEY idx_phone (phone),
    KEY idx_email (email),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表';

-- =============================================
-- 2. 角色表（sys_role）
-- =============================================
DROP TABLE IF EXISTS sys_role;
CREATE TABLE sys_role (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    role_name VARCHAR(50) NOT NULL COMMENT '角色名称',
    role_key VARCHAR(50) NOT NULL COMMENT '角色权限字符串',
    role_sort INT DEFAULT 0 COMMENT '显示顺序',
    status TINYINT DEFAULT 1 COMMENT '角色状态（1正常 0停用）',
    del_flag TINYINT DEFAULT 0 COMMENT '删除标志（0代表存在 2代表删除）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色信息表';

-- =============================================
-- 3. 权限/菜单表（sys_permission）
-- =============================================
DROP TABLE IF EXISTS sys_permission;
CREATE TABLE sys_permission (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    permission_name VARCHAR(50) NOT NULL COMMENT '权限名称',
    parent_id BIGINT DEFAULT 0 COMMENT '父权限ID',
    order_num INT DEFAULT 0 COMMENT '显示顺序',
    path VARCHAR(200) DEFAULT NULL COMMENT '路由地址',
    component VARCHAR(255) DEFAULT NULL COMMENT '组件路径',
    query VARCHAR(255) DEFAULT NULL COMMENT '路由参数',
    is_frame TINYINT DEFAULT 1 COMMENT '是否为外链（0是 1否）',
    is_cache TINYINT DEFAULT 0 COMMENT '是否缓存（0缓存 1不缓存）',
    menu_type CHAR(1) DEFAULT '' COMMENT '菜单类型（M目录 C菜单 F按钮）',
    visible TINYINT DEFAULT 0 COMMENT '菜单状态（0显示 1隐藏）',
    status TINYINT DEFAULT 0 COMMENT '菜单状态（0正常 1停用）',
    perms VARCHAR(100) DEFAULT NULL COMMENT '权限标识',
    icon VARCHAR(100) DEFAULT '#' COMMENT '菜单图标',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    remark VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='权限/菜单表';

-- =============================================
-- 4. 用户和角色关联表（sys_user_role）
-- =============================================
DROP TABLE IF EXISTS sys_user_role;
CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户和角色关联表';

-- =============================================
-- 5. 角色和权限关联表（sys_role_permission）
-- =============================================
DROP TABLE IF EXISTS sys_role_permission;
CREATE TABLE sys_role_permission (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    permission_id BIGINT NOT NULL COMMENT '权限ID',
    PRIMARY KEY (role_id, permission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='角色和权限关联表';

-- =============================================
-- 6. 操作日志记录表（sys_oper_log）
-- =============================================
DROP TABLE IF EXISTS sys_oper_log;
CREATE TABLE sys_oper_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    title VARCHAR(50) DEFAULT '' COMMENT '模块标题',
    business_type INT DEFAULT 0 COMMENT '业务类型（0其它 1新增 2修改 3删除）',
    method VARCHAR(100) DEFAULT '' COMMENT '方法名称',
    request_method VARCHAR(10) DEFAULT '' COMMENT '请求方式',
    operator_type INT DEFAULT 0 COMMENT '操作类别（0其它 1后台用户 2手机端用户）',
    oper_name VARCHAR(50) DEFAULT '' COMMENT '操作人员',
    oper_url VARCHAR(255) DEFAULT '' COMMENT '请求URL',
    oper_ip VARCHAR(128) DEFAULT '' COMMENT '主机地址',
    oper_location VARCHAR(255) DEFAULT '' COMMENT '操作地点',
    oper_param TEXT DEFAULT NULL COMMENT '请求参数',
    json_result TEXT DEFAULT NULL COMMENT '返回参数',
    status INT DEFAULT 0 COMMENT '操作状态（0正常 1异常）',
    error_msg TEXT DEFAULT NULL COMMENT '错误消息',
    oper_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    cost_time BIGINT DEFAULT 0 COMMENT '消耗时间',
    PRIMARY KEY (id),
    KEY idx_sys_oper_log_bt (business_type),
    KEY idx_sys_oper_log_s (status),
    KEY idx_sys_oper_log_ot (oper_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='操作日志记录';

-- =============================================
-- 7. 商品分类表（category）
-- =============================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    sort INT DEFAULT 0 COMMENT '排序（数字越小越靠前）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_sort (sort)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品分类表';

-- =============================================
-- 7.1 品牌表（brand）
-- =============================================
DROP TABLE IF EXISTS brand;
CREATE TABLE brand (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(50) NOT NULL COMMENT '品牌名称',
    logo VARCHAR(255) DEFAULT NULL COMMENT '品牌Logo',
    description VARCHAR(200) DEFAULT NULL COMMENT '品牌描述',
    sort INT DEFAULT 0 COMMENT '排序（数字越小越靠前）',
    status TINYINT DEFAULT 1 COMMENT '状态（0-禁用，1-正常）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_sort (sort),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='品牌表';

-- 插入品牌数据
INSERT INTO brand (name, logo, description, sort, status) VALUES
('海尔', NULL, '海尔集团，全球大型家电品牌', 1, 1),
('格力', NULL, '格力电器，中国空调行业领导品牌', 2, 1),
('美的', NULL, '美的集团，中国领先的家电制造商', 3, 1),
('容声', NULL, '容声冰箱，专注冰箱制造 30 年', 4, 1),
('美菱', NULL, '美菱电器，中国重要的家电企业', 5, 1),
('西门子', NULL, '西门子家电，德国百年品牌', 6, 1),
('卡萨帝', NULL, '卡萨帝，海尔旗下高端家电品牌', 7, 1),
('小米', NULL, '小米科技，智能家电领先品牌', 8, 1),
('海信', NULL, '海信集团，中国著名家电品牌', 9, 1),
('三星', NULL, '三星电子，韩国跨国企业', 10, 1),
('松下', NULL, '松下电器，日本百年品牌', 11, 1);

-- =============================================
-- 8. 商品表（product）
-- =============================================
DROP TABLE IF EXISTS product;
CREATE TABLE product (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    name VARCHAR(200) NOT NULL COMMENT '商品名称',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    brand VARCHAR(50) DEFAULT NULL COMMENT '品牌',
    model VARCHAR(100) DEFAULT NULL COMMENT '型号',
    sku VARCHAR(100) DEFAULT NULL COMMENT 'SKU编码',
    price DECIMAL(10,2) NOT NULL COMMENT '价格',
    original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
    stock INT DEFAULT 0 COMMENT '库存',
    image VARCHAR(255) DEFAULT NULL COMMENT '主图URL',
    images TEXT DEFAULT NULL COMMENT '详情图片JSON数组',
    capacity INT DEFAULT NULL COMMENT '容量（升）',
    energy_level VARCHAR(10) DEFAULT NULL COMMENT '能效等级',
    color VARCHAR(50) DEFAULT NULL COMMENT '颜色',
    size VARCHAR(50) DEFAULT NULL COMMENT '尺寸（长x宽x高）',
    description TEXT DEFAULT NULL COMMENT '商品描述',
    sales INT DEFAULT 0 COMMENT '销量',
    rating INT DEFAULT 0 COMMENT '平均评分（1-5）',
    status TINYINT DEFAULT 1 COMMENT '状态（0-下架，1-上架）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_category_id (category_id),
    KEY idx_brand (brand),
    KEY idx_sku (sku),
    KEY idx_price (price),
    KEY idx_status (status),
    KEY idx_sales (sales),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='商品表';

-- =============================================
-- 9. 购物车表（cart）
-- =============================================
DROP TABLE IF EXISTS cart;
CREATE TABLE cart (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    quantity INT DEFAULT 1 COMMENT '数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_product_id (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='购物车表';

-- =============================================
-- 10. 订单表（orders）
-- =============================================
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_no VARCHAR(50) NOT NULL COMMENT '订单号',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
    pay_amount DECIMAL(10,2) DEFAULT NULL COMMENT '实付金额',
    payment_type TINYINT DEFAULT NULL COMMENT '支付方式（1-支付宝，2-微信）',
    status TINYINT DEFAULT 0 COMMENT '订单状态（0-待付款，1-待发货，2-待收货，3-已完成，4-已取消）',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    receiver_address VARCHAR(255) NOT NULL COMMENT '收货地址',
    express_company VARCHAR(100) DEFAULT NULL COMMENT '物流公司',
    express_no VARCHAR(100) DEFAULT NULL COMMENT '物流单号',
    pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
    deliver_time DATETIME DEFAULT NULL COMMENT '发货时间',
    receive_time DATETIME DEFAULT NULL COMMENT '收货时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '是否删除：0-未删除，1-已删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_user_id (user_id),
    KEY idx_status (status),
    KEY idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单表';

-- =============================================
-- 11. 订单详情表（order_item）
-- =============================================
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    order_id BIGINT NOT NULL COMMENT '订单ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    product_name VARCHAR(200) NOT NULL COMMENT '商品名称',
    product_image VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    sku VARCHAR(100) DEFAULT NULL COMMENT 'SKU编码',
    price DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    quantity INT NOT NULL COMMENT '数量',
    total_amount DECIMAL(10,2) NOT NULL COMMENT '小计金额',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id),
    KEY idx_product_id (product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='订单详情表';

-- =============================================
-- 12. 收藏表（favorite）
-- =============================================
DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_product_id (product_id),
    UNIQUE KEY uk_user_product (user_id, product_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='收藏表';

-- =============================================
-- 13. 地址表（address）
-- =============================================
DROP TABLE IF EXISTS address;
CREATE TABLE address (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键 ID',
    user_id BIGINT NOT NULL COMMENT '用户 ID',
    receiver_name VARCHAR(50) NOT NULL COMMENT '收货人姓名',
    receiver_phone VARCHAR(20) NOT NULL COMMENT '收货人电话',
    province VARCHAR(50) DEFAULT NULL COMMENT '省',
    city VARCHAR(50) DEFAULT NULL COMMENT '市',
    district VARCHAR(50) DEFAULT NULL COMMENT '区',
    detail_address VARCHAR(255) NOT NULL COMMENT '详细地址',
    is_default TINYINT DEFAULT 0 COMMENT '是否默认（0-否，1-是）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_is_default (is_default)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='地址表';

-- =============================================
-- 14. 评价表（review）
-- =============================================
DROP TABLE IF EXISTS review;
CREATE TABLE review (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    product_id BIGINT NOT NULL COMMENT '商品ID',
    order_id BIGINT DEFAULT NULL COMMENT '订单ID',
    rating TINYINT NOT NULL COMMENT '评分（1-5）',
    content TEXT NOT NULL COMMENT '评价内容',
    images VARCHAR(1000) DEFAULT NULL COMMENT '评价图片JSON',
    status TINYINT DEFAULT 1 COMMENT '状态（0-隐藏，1-显示）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_user_id (user_id),
    KEY idx_product_id (product_id),
    KEY idx_order_id (order_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='评价表';

-- =============================================
-- 插入测试数据
-- =============================================

-- 插入角色数据
INSERT INTO sys_role (role_name, role_key, role_sort, status, remark) VALUES
('管理员', 'admin', 1, 1, '管理员角色，拥有所有权限'),
('普通用户', 'user', 2, 1, '普通用户角色');

-- 插入权限/菜单数据
INSERT INTO sys_permission (permission_name, parent_id, order_num, path, component, menu_type, perms, icon, status) VALUES
('系统管理', 0, 1, '/system', NULL, 'M', NULL, 'setting', 0),
('用户管理', 1, 1, '/system/user', 'system/user/index', 'C', 'system:user:list', 'user', 0),
('角色管理', 1, 2, '/system/role', 'system/role/index', 'C', 'system:role:list', 'peoples', 0),
('菜单管理', 1, 3, '/system/menu', 'system/menu/index', 'C', 'system:menu:list', 'tree-table', 0),
('订单管理', 0, 2, '/order', NULL, 'M', NULL, 'order', 0),
('订单列表', 5, 1, '/order/list', 'order/list', 'C', 'order:list', 'order', 0),
('商品管理', 0, 3, '/product', NULL, 'M', NULL, 'product', 0),
('商品列表', 7, 1, '/product/list', 'product/list', 'C', 'product:list', 'product', 0),
('分类管理', 7, 2, '/product/category', 'product/category', 'C', 'product:category:list', 'category', 0),
('数据统计', 0, 4, '/statistics', NULL, 'M', NULL, 'statistics', 0),
('销售统计', 10, 1, '/statistics/sales', 'statistics/sales', 'C', 'statistics:sales', 'chart', 0);

-- 插入测试用户（使用 BCrypt 加密，部署前请修改默认密码）
INSERT INTO sys_user (username, password, phone, email, avatar, role, status) VALUES
('admin', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13800138000', 'admin@fridge.com', NULL, 'admin', 1),
('zhangsan', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13800138001', 'zhangsan@example.com', NULL, 'user', 1),
('lisi', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13800138002', 'lisi@example.com', NULL, 'user', 1),
('wangwu', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13900139001', 'wangwu@example.com', NULL, 'user', 1),
('zhaoliu', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13900139002', 'zhaoliu@example.com', NULL, 'user', 1),
('sunqi', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13900139003', 'sunqi@example.com', NULL, 'user', 1),
('zhouba', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13900139004', 'zhouba@example.com', NULL, 'user', 1),
('wujiu', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13900139005', 'wujiu@example.com', NULL, 'user', 0),
('zhengshi', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13900139006', 'zhengshi@example.com', NULL, 'user', 1),
('testuser', '$2a$10$YK38Iuz3ZR2hMONJB6W38eADav/gDSxfjph7e.lF/VuAPsv5cpsIC', '13900139007', 'testuser@example.com', NULL, 'user', 1);

-- 插入用户和角色关联（admin用户分配管理员角色）
INSERT INTO sys_user_role (user_id, role_id) VALUES
(1, 1),
(2, 2),
(3, 2),
(4, 2),
(5, 2),
(6, 2),
(7, 2),
(8, 2),
(9, 2),
(10, 2);

-- 补充角色权限关联（管理员拥有所有权限）
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 4),
(1, 5), (1, 6),
(1, 7), (1, 8), (1, 9),
(1, 10), (1, 11);

-- 普通用户只拥有查看权限
INSERT INTO sys_role_permission (role_id, permission_id) VALUES
(2, 5), (2, 6);

-- 插入商品分类
INSERT INTO category (name, sort) VALUES
('双门冰箱', 1),
('三门冰箱', 2),
('对开门冰箱', 3),
('多门冰箱', 4),
('嵌入式冰箱', 5);

-- 插入商品数据（双门冰箱 - category_id=1）
INSERT INTO product (name, category_id, brand, model, sku, price, original_price, stock, image, images, capacity, energy_level, color, size, description, sales, rating, status) VALUES
('海尔 双门冰箱 BCD-180TMPS', 1, '海尔', 'BCD-180TMPS', 'BCD-180TMPS', 1299.00, 1599.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', '[]', 180, '一级', '银色', '580x520x1410mm', '海尔双门冰箱，180L 大容量，一级能效，节能省电，静音设计，适合小家庭使用。', 1, 0, 1),
('格力 双门冰箱 BCD-155C', 1, '格力', 'BCD-155C', 'BCD-155C', 1099.00, 1399.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', '[]', 155, '二级', '银色', '520x475x1405mm', '格力双门冰箱，155L 容量，二级能效，机械控温，经济实惠，适合单身或情侣使用。', 0, 0, 1),
('美的 双门冰箱 BCD-160WM', 1, '美的', 'BCD-160WM', 'BCD-160WM', 1199.00, 1499.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', '[]', 160, '一级', '白色', '540x500x1400mm', '美的双门冰箱，160L 容量，风冷无霜，智能变频，节能静音。', 1, 0, 1),
('容声 双门冰箱 BCD-172D11D', 1, '容声', 'BCD-172D11D', 'BCD-172D11D', 999.00, 1299.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', '[]', 172, '二级', '银色', '560x490x1420mm', '容声双门冰箱，172L 容量，机械控温，经济实用，性价比之选。', 0, 0, 1);

-- 插入商品数据（三门冰箱 - category_id=2）
INSERT INTO product (name, category_id, brand, model, sku, price, original_price, stock, image, images, capacity, energy_level, color, size, description, sales, rating, status) VALUES
('美的 三门冰箱 BCD-230WTPZM', 2, '美的', 'BCD-230WTPZM', 'BCD-230WTPZM', 2199.00, 2699.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/180712/35/45998/67458/66c0b5a2Ff8e3c3e5/2e3e5a8e7e5e5e5e.jpg', '[]', 230, '一级', '白色', '580x610x1770mm', '美的三门冰箱，230L 容量，中门变温设计，智能变频，风冷无霜，保鲜更持久。', 0, 0, 1),
('美菱 三门冰箱 BCD-206L3CT', 2, '美菱', 'BCD-206L3CT', 'BCD-206L3CT', 1699.00, 1999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/180712/35/45998/67458/66c0b5a2Ff8e3c3e5/2e3e5a8e7e5e5e5e.jpg', '[]', 206, '一级', '香槟金', '560x580x1720mm', '美菱三门冰箱，206L 容量，三温区设计，电脑控温，节能省电，性价比之选。', 0, 0, 1),
('容声 三门冰箱 BCD-252WD11NPA', 2, '容声', 'BCD-252WD11NPA', 'BCD-252WD11NPA', 2899.00, 3399.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/180712/35/45998/67458/66c0b5a2Ff8e3c3e5/2e3e5a8e7e5e5e5e.jpg', '[]', 252, '一级', '银色', '580x610x1760mm', '容声三门冰箱，252L 容量，风冷无霜，电脑温控，节能静音，家用优选。', 0, 0, 1),
('海尔 三门冰箱 BCD-235WLHDP9W1U1', 2, '海尔', 'BCD-235WLHDP9W1U1', 'BCD-235WLHDP9W1U1', 3299.00, 3799.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/180712/35/45998/67458/66c0b5a2Ff8e3c3e5/2e3e5a8e7e5e5e5e.jpg', '[]', 235, '一级', '星辉银', '600x630x1770mm', '海尔三门冰箱，235L 容量，智能变频，干湿分储，T.ABT 杀菌。', 1, 0, 1);

-- 插入商品数据（对开门冰箱 - category_id=3）
INSERT INTO product (name, category_id, brand, model, sku, price, original_price, stock, image, images, capacity, energy_level, color, size, description, sales, rating, status) VALUES
('西门子 对开门冰箱 BCD-610W', 3, '西门子', 'BCD-610W', 'BCD-610W', 5999.00, 6999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', '[]', 610, '一级', '不锈钢', '912x714x1756mm', '西门子对开门冰箱，610L 超大容量，双循环制冷，独立控温，德国品质，高端大气。', 1, 0, 1),
('卡萨帝 对开门冰箱 BCD-628WICAU1', 3, '卡萨帝', 'BCD-628WICAU1', 'BCD-628WICAU1', 12999.00, 14999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', '[]', 628, '一级', '黑色', '905x738x1900mm', '卡萨帝对开门冰箱，628L 超大容量，细胞级养鲜，智慧物联，高端奢华之选。', 0, 0, 1),
('小米 对开门冰箱 BCD-540WMSA', 3, '小米', 'BCD-540WMSA', 'BCD-540WMSA', 3999.00, 4599.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', '[]', 540, '一级', '银灰色', '910x690x1800mm', '小米智能冰箱，540L 大容量，智能互联，语音控制，AI 保鲜技术，现代科技典范。', 0, 0, 1),
('海信 对开门冰箱 BCD-535WTVBP', 3, '海信', 'BCD-535WTVBP', 'BCD-535WTVBP', 4299.00, 4999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', '[]', 535, '一级', '不锈钢', '910x680x1780mm', '海信对开门冰箱，535L 容量，矢量变频，360 度环绕送风，保鲜更均匀。', 2, 0, 1),
('三星 对开门冰箱 RS62R5004M9', 3, '三星', 'RS62R5004M9', 'RS62R5004M9', 6999.00, 7999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', '[]', 625, '一级', '银色', '912x734x1780mm', '三星智能冰箱，625L 超大容量，智能触摸屏，内置摄像头，远程查看食材。', 0, 0, 1),
('美的 对开门冰箱 BCD-606WKPZM', 3, '美的', 'BCD-606WKPZM', 'BCD-606WKPZM', 4799.00, 5599.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', '[]', 606, '一级', '曜石棕', '910x690x1790mm', '美的对开门冰箱，606L 超大容量，智能变频，铂金净味，大空间更实用。', 1, 0, 1),
('美菱 对开门冰箱 BCD-568WEC', 3, '美菱', 'BCD-568WEC', 'BCD-568WEC', 3799.00, 4399.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', '[]', 568, '一级', '银色', '910x680x1790mm', '美菱对开门冰箱，568L 大容量，风冷无霜，智能变频，性价比之王。', 0, 0, 1);

-- 插入商品数据（多门冰箱 - category_id=4）
INSERT INTO product (name, category_id, brand, model, sku, price, original_price, stock, image, images, capacity, energy_level, color, size, description, sales, rating, status) VALUES
('容声 多门冰箱 BCD-452WD12FP', 4, '容声', 'BCD-452WD12FP', 'BCD-452WD12FP', 4599.00, 5299.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', '[]', 452, '一级', '香槟金', '794x694x1810mm', '容声多门冰箱，452L 容量，十字对开门设计，干湿分储，智能双变频，节能静音。', 2, 0, 1),
('海尔 多门冰箱 BCD-456WDG', 4, '海尔', 'BCD-456WDG', 'BCD-456WDG', 5299.00, 6299.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', '[]', 456, '一级', '香槟金', '790x685x1790mm', '海尔法式多门冰箱，456L 容量，法式设计，干湿分储，T-ABT 杀菌，健康保鲜。', 2, 0, 1),
('松下 多门冰箱 NR-JD40ATX-N', 4, '松下', 'NR-JD40ATX-N', 'NR-JD40ATX-N', 7599.00, 8599.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', '[]', 405, '一级', '香槟金', '748x699x1800mm', '松下多门冰箱，405L 容量，五门设计，独立制冰，-3 度微冻，新鲜随取。', 0, 0, 1),
('卡萨帝 多门冰箱 BCD-551WDCQU1', 4, '卡萨帝', 'BCD-551WDCQU1', 'BCD-551WDCQU1', 15999.00, 17999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', '[]', 551, '一级', '紫色', '830x700x1900mm', '卡萨帝法式冰箱，551L 容量，MSA 控氧保鲜，细胞级养鲜，奢华品质。', 0, 0, 1),
('格力 多门冰箱 BCD-405WIPZ', 4, '格力', 'BCD-405WIPZ', 'BCD-405WIPZ', 4199.00, 4899.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', '[]', 405, '一级', '玫瑰金', '754x654x1850mm', '格力多门冰箱，405L 容量，十字对开门，智能控湿，节能静音，品质生活。', 0, 0, 1),
('小米 多门冰箱 BCD-486WMSA', 4, '小米', 'BCD-486WMSA', 'BCD-486WMSA', 4599.00, 5299.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', '[]', 486, '一级', '银灰色', '830x690x1850mm', '小米法式冰箱，486L 容量，智能互联，干湿分储，变频节能，智能生活新选择。', 0, 0, 1);

-- 插入商品数据（嵌入式冰箱 - category_id=5）
INSERT INTO product (name, category_id, brand, model, sku, price, original_price, stock, image, images, capacity, energy_level, color, size, description, sales, rating, status) VALUES
('松下 嵌入式冰箱 NR-EW45TGA-W', 5, '松下', 'NR-EW45TGA-W', 'NR-EW45TGA-W', 8999.00, 9999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/120712/35/45998/67458/66c0b5a2Ff8e3c3e5/5e3e5a8e7e5e5e5e.jpg', '[]', 451, '一级', '白色', '748x748x1850mm', '松下嵌入式冰箱，451L 容量，全嵌入式设计，nanoeX 除菌技术，-3 度微冻保鲜。', 0, 0, 1),
('海尔 嵌入式冰箱 BCD-405WDPD', 5, '海尔', 'BCD-405WDPD', 'BCD-405WDPD', 7999.00, 8999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/120712/35/45998/67458/66c0b5a2Ff8e3c3e5/5e3e5a8e7e5e5e5e.jpg', '[]', 405, '一级', '白色', '740x740x1850mm', '海尔嵌入式冰箱，405L 容量，零嵌入设计，两侧散热，完美融入橱柜。', 0, 0, 1),
('卡萨帝 嵌入式冰箱 BCD-380WLCI', 5, '卡萨帝', 'BCD-380WLCI', 'BCD-380WLCI', 11999.00, 13999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/120712/35/45998/67458/66c0b5a2Ff8e3c3e5/5e3e5a8e7e5e5e5e.jpg', '[]', 380, '一级', '黑色', '720x720x1800mm', '卡萨帝嵌入式冰箱，380L 容量，自由嵌入，智慧恒温，高端定制之选。', 0, 0, 1),
('美的 嵌入式冰箱 BCD-318WTPZM', 5, '美的', 'BCD-318WTPZM', 'BCD-318WTPZM', 5999.00, 6999.00, 1000, 'https://img14.360buyimg.com/n1/jfs/t1/120712/35/45998/67458/66c0b5a2Ff8e3c3e5/5e3e5a8e7e5e5e5e.jpg', '[]', 318, '一级', '白色', '680x680x1750mm', '美的嵌入式冰箱，318L 容量，嵌入式设计，智能变频，节能静音。', 0, 0, 1);

-- 插入收货地址数据
INSERT INTO address (user_id, receiver_name, receiver_phone, province, city, district, detail_address, is_default) VALUES
(2, '张三', '13800138001', '北京市', '北京市', '朝阳区', '建国路88号SOHO现代城A座1001室', 1),
(2, '张三', '13800138001', '北京市', '北京市', '海淀区', '中关村大街1号海龙大厦B座2005室', 0),
(3, '李四', '13800138002', '上海市', '上海市', '浦东新区', '陆家嘴环路1000号恒生银行大厦15层', 1),
(4, '王五', '13900139001', '广东省', '广州市', '天河区', '天河路385号太古汇1座', 1),
(4, '王五', '13900139001', '广东省', '深圳市', '南山区', '科技园南区深南大道9966号', 0),
(5, '赵六', '13900139002', '浙江省', '杭州市', '西湖区', '文三路398号东信大厦', 1),
(6, '孙七', '13900139003', '江苏省', '南京市', '鼓楼区', '中山北路30号绿地中心', 1),
(7, '周八', '13900139004', '四川省', '成都市', '武侯区', '天府大道中段500号', 1),
(10, '测试用户', '13900139007', '北京市', '北京市', '西城区', '金融街19号富凯大厦', 1);

-- 插入收藏记录
INSERT INTO favorite (user_id, product_id, create_time) VALUES
(2, 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(2, 5, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 9, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 12, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 2, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(3, 4, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 7, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 10, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 15, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(4, 1, DATE_SUB(NOW(), INTERVAL 7 DAY)),
(4, 6, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 11, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 3, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 8, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 5, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(6, 9, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 2, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(7, 14, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(10, 5, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 9, DATE_SUB(NOW(), INTERVAL 3 DAY));

-- 插入购物车记录
INSERT INTO cart (user_id, product_id, quantity, create_time) VALUES
(2, 1, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(2, 3, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(2, 6, 2, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 2, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(3, 4, 1, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(3, 8, 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(4, 5, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(4, 10, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(5, 7, 1, DATE_SUB(NOW(), INTERVAL 1 DAY)),
(6, 11, 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(7, 15, 2, DATE_SUB(NOW(), INTERVAL 2 DAY)),
(10, 1, 1, DATE_SUB(NOW(), INTERVAL 1 DAY));

-- 插入订单数据（包含各种状态）
INSERT INTO orders (order_no, user_id, total_amount, pay_amount, payment_type, status, receiver_name, receiver_phone, receiver_address, pay_time, deliver_time, receive_time, create_time) VALUES
('ORD202603010001', 2, 5999.00, 5999.00, 1, 3, '张三', '13800138001', '北京市朝阳区建国路88号SOHO现代城A座1001室', DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY)),
('ORD202603010002', 2, 1299.00, 1299.00, 2, 3, '张三', '13800138001', '北京市朝阳区建国路88号SOHO现代城A座1001室', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
('ORD202603010003', 2, 4599.00, 4599.00, 1, 3, '张三', '13800138001', '北京市海淀区中关村大街1号海龙大厦B座2005室', DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY)),
('ORD202603010004', 3, 4299.00, 4299.00, 2, 3, '李四', '13800138002', '上海市浦东新区陆家嘴环路1000号恒生银行大厦15层', DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY)),
('ORD202603010005', 3, 2199.00, NULL, NULL, 0, '李四', '13800138002', '上海市浦东新区陆家嘴环路1000号恒生银行大厦15层', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD202603010006', 3, 5999.00, 5999.00, 1, 1, '李四', '13800138002', '上海市浦东新区陆家嘴环路1000号恒生银行大厦15层', DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ORD202603010007', 2, 1199.00, 1199.00, 2, 2, '张三', '13800138001', '北京市朝阳区建国路88号SOHO现代城A座1001室', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD202603010008', 3, 3799.00, 3799.00, 1, 3, '李四', '13800138002', '上海市浦东新区陆家嘴环路1000号恒生银行大厦15层', DATE_SUB(NOW(), INTERVAL 9 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 6 DAY), DATE_SUB(NOW(), INTERVAL 9 DAY)),
('ORD202603010009', 4, 3299.00, 3299.00, 2, 3, '王五', '13900139001', '广东省广州市天河区天河路385号太古汇1座', DATE_SUB(NOW(), INTERVAL 12 DAY), DATE_SUB(NOW(), INTERVAL 11 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 12 DAY)),
('ORD202603010010', 4, 4799.00, 4799.00, 1, 3, '王五', '13900139001', '广东省深圳市南山区科技园南区深南大道9966号', DATE_SUB(NOW(), INTERVAL 5 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 5 DAY)),
('ORD202603010011', 4, 1099.00, NULL, NULL, 0, '王五', '13900139001', '广东省广州市天河区天河路385号太古汇1座', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD202603010012', 5, 1699.00, 1699.00, 2, 1, '赵六', '13900139002', '浙江省杭州市西湖区文三路398号东信大厦', DATE_SUB(NOW(), INTERVAL 2 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD202603010013', 5, 2899.00, 2899.00, 1, 2, '赵六', '13900139002', '浙江省杭州市西湖区文三路398号东信大厦', DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 3 DAY), NULL, DATE_SUB(NOW(), INTERVAL 4 DAY)),
('ORD202603010014', 5, 5299.00, 5299.00, 2, 3, '赵六', '13900139002', '浙江省杭州市西湖区文三路398号东信大厦', DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 14 DAY), DATE_SUB(NOW(), INTERVAL 10 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY)),
('ORD202603010015', 6, 12999.00, 12999.00, 1, 3, '孙七', '13900139003', '江苏省南京市鼓楼区中山北路30号绿地中心', DATE_SUB(NOW(), INTERVAL 20 DAY), DATE_SUB(NOW(), INTERVAL 19 DAY), DATE_SUB(NOW(), INTERVAL 15 DAY), DATE_SUB(NOW(), INTERVAL 20 DAY)),
('ORD202603010016', 6, 2199.00, NULL, NULL, 4, '孙七', '13900139003', '江苏省南京市鼓楼区中山北路30号绿地中心', NULL, NULL, NULL, DATE_SUB(NOW(), INTERVAL 3 DAY)),
('ORD202603010017', 7, 3799.00, 3799.00, 2, 1, '周八', '13900139004', '四川省成都市武侯区天府大道中段500号', DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, NULL, DATE_SUB(NOW(), INTERVAL 1 DAY)),
('ORD202603010018', 7, 4599.00, 4599.00, 1, 3, '周八', '13900139004', '四川省成都市武侯区天府大道中段500号', DATE_SUB(NOW(), INTERVAL 8 DAY), DATE_SUB(NOW(), INTERVAL 7 DAY), DATE_SUB(NOW(), INTERVAL 4 DAY), DATE_SUB(NOW(), INTERVAL 8 DAY)),
('ORD202603010019', 10, 1199.00, 1199.00, 2, 2, '测试用户', '13900139007', '北京市西城区金融街19号富凯大厦', DATE_SUB(NOW(), INTERVAL 2 DAY), DATE_SUB(NOW(), INTERVAL 1 DAY), NULL, DATE_SUB(NOW(), INTERVAL 2 DAY)),
('ORD202603010020', 10, 3999.00, NULL, NULL, 0, '测试用户', '13900139007', '北京市西城区金融街19号富凯大厦', NULL, NULL, NULL, NOW());

-- 插入订单详情数据
INSERT INTO order_item (order_id, product_id, product_name, product_image, sku, price, quantity, total_amount) VALUES
(1, 9, '西门子 对开门冰箱 BCD-610W', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-610W', 5999.00, 1, 5999.00),
(2, 1, '海尔 双门冰箱 BCD-180TMPS', 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', 'BCD-180TMPS', 1299.00, 1, 1299.00),
(3, 16, '容声 多门冰箱 BCD-452WD12FP', 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', 'BCD-452WD12FP', 4599.00, 1, 4599.00),
(4, 10, '海信 对开门冰箱 BCD-535WTVBP', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-535WTVBP', 4299.00, 1, 4299.00),
(5, 5, '美的 三门冰箱 BCD-230WTPZM', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-230WTPZM', 2199.00, 1, 2199.00),
(6, 9, '西门子 对开门冰箱 BCD-610W', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-610W', 5999.00, 1, 5999.00),
(7, 3, '美的 双门冰箱 BCD-160WM', 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', 'BCD-160WM', 1199.00, 1, 1199.00),
(8, 17, '美菱 对开门冰箱 BCD-568WEC', 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', 'BCD-568WEC', 3799.00, 1, 3799.00),
(9, 8, '海尔 三门冰箱 BCD-235WLHDP9W1U1', 'https://img14.360buyimg.com/n1/jfs/t1/180712/35/45998/67458/66c0b5a2Ff8e3c3e5/2e3e5a8e7e5e5e5e.jpg', 'BCD-235WLHDP9W1U1', 3299.00, 1, 3299.00),
(10, 14, '美的 对开门冰箱 BCD-606WKPZM', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-606WKPZM', 4799.00, 1, 4799.00),
(11, 2, '格力 双门冰箱 BCD-155C', 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', 'BCD-155C', 1099.00, 1, 1099.00),
(12, 6, '美菱 三门冰箱 BCD-206L3CT', 'https://img14.360buyimg.com/n1/jfs/t1/180712/35/45998/67458/66c0b5a2Ff8e3c3e5/2e3e5a8e7e5e5e5e.jpg', 'BCD-206L3CT', 1699.00, 1, 1699.00),
(13, 7, '容声 三门冰箱 BCD-252WD11NPA', 'https://img14.360buyimg.com/n1/jfs/t1/180712/35/45998/67458/66c0b5a2Ff8e3c3e5/2e3e5a8e7e5e5e5e.jpg', 'BCD-252WD11NPA', 2899.00, 1, 2899.00),
(14, 17, '海尔 多门冰箱 BCD-456WDG', 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', 'BCD-456WDG', 5299.00, 1, 5299.00),
(15, 10, '卡萨帝 对开门冰箱 BCD-628WICAU1', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-628WICAU1', 12999.00, 1, 12999.00),
(16, 5, '美的 三门冰箱 BCD-230WTPZM', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-230WTPZM', 2199.00, 1, 2199.00),
(17, 17, '美菱 对开门冰箱 BCD-568WEC', 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', 'BCD-568WEC', 3799.00, 1, 3799.00),
(18, 16, '容声 多门冰箱 BCD-452WD12FP', 'https://img14.360buyimg.com/n1/jfs/t1/140712/35/45998/67458/66c0b5a2Ff8e3c3e5/4e3e5a8e7e5e5e5e.jpg', 'BCD-452WD12FP', 4599.00, 1, 4599.00),
(19, 3, '美的 双门冰箱 BCD-160WM', 'https://img14.360buyimg.com/n1/jfs/t1/200712/35/45998/67458/66c0b5a2Ff8e3c3e5/1e3e5a8e7e5e5e5e.jpg', 'BCD-160WM', 1199.00, 1, 1199.00),
(20, 12, '小米 对开门冰箱 BCD-540WMSA', 'https://img14.360buyimg.com/n1/jfs/t1/160712/35/45998/67458/66c0b5a2Ff8e3c3e5/3e3e5a8e7e5e5e5e.jpg', 'BCD-540WMSA', 3999.00, 1, 3999.00);

-- 插入评价数据
INSERT INTO review (user_id, product_id, order_id, rating, content, status, create_time) VALUES
(2, 9, 1, 5, '冰箱收到了，外观很漂亮，容量也很大，一家人用足够了。西门子大品牌值得信赖！', 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(2, 1, 2, 5, '海尔冰箱质量很好，制冷效果快，噪音很小，一级能效省电，非常满意！', 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(2, 16, 3, 4, '容声多门冰箱设计合理，干湿分储很实用，就是价格稍贵了点。', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(3, 10, 4, 5, '海信冰箱性价比很高，送货速度快，安装师傅很专业，推荐购买！', 1, DATE_SUB(NOW(), INTERVAL 4 DAY)),
(3, 17, 8, 5, '美菱冰箱物美价廉，容量大，制冷效果好，静音效果也不错。', 1, DATE_SUB(NOW(), INTERVAL 6 DAY)),
(4, 8, 9, 5, '海尔三门冰箱非常好用，智能变频省电，T.ABT杀菌功能很实用，全家都很满意！', 1, DATE_SUB(NOW(), INTERVAL 8 DAY)),
(4, 14, 10, 4, '美的对开门冰箱外观大气，容量够大，就是送货时间长了点。', 1, DATE_SUB(NOW(), INTERVAL 3 DAY)),
(5, 17, 14, 5, '海尔法式冰箱高端大气，干湿分储设计很人性化，T-ABT杀菌让食材更放心！', 1, DATE_SUB(NOW(), INTERVAL 10 DAY)),
(6, 10, 15, 5, '卡萨帝冰箱真的太棒了！超大容量，智慧物联功能很酷，高端品质值得这个价格！', 1, DATE_SUB(NOW(), INTERVAL 15 DAY)),
(7, 16, 18, 4, '容声多门冰箱性价比不错，十字对开门设计方便，干湿分储实用。', 1, DATE_SUB(NOW(), INTERVAL 5 DAY)),
(10, 3, 19, 5, '美的双门冰箱小巧实用，风冷无霜很方便，适合小户型家庭使用。', 1, DATE_SUB(NOW(), INTERVAL 2 DAY));

-- 更新商品评分（基于review表计算平均评分）
UPDATE product p
SET p.rating = COALESCE(
    (SELECT FLOOR(AVG(r.rating))
     FROM review r
     WHERE r.product_id = p.id AND r.status = 1),
    0
);

-- 更新商品销量（基于review表统计评论数量）
UPDATE product p
SET p.sales = COALESCE(
    (SELECT COUNT(r.id)
     FROM review r
     WHERE r.product_id = p.id AND r.status = 1),
    0
);

-- =============================================
-- 数据更新脚本
-- =============================================

-- 更新现有商品的 SKU 字段（将 model 字段的值复制到 sku）
UPDATE product SET sku = model WHERE sku IS NULL OR sku = '';

-- 更新订单项的商品图片（关联商品表获取图片）
UPDATE order_item oi 
JOIN product p ON oi.product_id = p.id 
SET oi.product_image = p.image 
WHERE oi.product_image IS NULL OR oi.product_image = '';

-- =============================================
-- 结束
-- =============================================
