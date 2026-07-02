-- ============================================================
-- 购物商城系统 - 数据库初始化脚本
-- 成员4: 数据访问层负责
-- ============================================================

CREATE DATABASE IF NOT EXISTS shopping_mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE shopping_mall;

-- ============================================================
-- 用户表
-- ============================================================
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
  `password` VARCHAR(200) NOT NULL COMMENT '密码(MD5加密)',
  `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
  `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
  `role` VARCHAR(10) NOT NULL DEFAULT 'user' COMMENT '角色: user普通用户, admin管理员',
  `avatar` VARCHAR(500) DEFAULT NULL COMMENT '头像URL',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态: 1正常, 0禁用',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- ============================================================
-- 地址表
-- ============================================================
DROP TABLE IF EXISTS `address`;
CREATE TABLE `address` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '地址ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `province` VARCHAR(50) NOT NULL COMMENT '省份',
  `city` VARCHAR(50) NOT NULL COMMENT '城市',
  `district` VARCHAR(50) DEFAULT NULL COMMENT '区县',
  `detail` VARCHAR(200) NOT NULL COMMENT '详细地址',
  `is_default` TINYINT NOT NULL DEFAULT 0 COMMENT '是否默认地址: 1是, 0否',
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地址表';

-- ============================================================
-- 商品分类表
-- ============================================================
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `description` VARCHAR(200) DEFAULT NULL COMMENT '分类描述',
  `sort_order` INT NOT NULL DEFAULT 0 COMMENT '排序序号'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================================
-- 商品表
-- ============================================================
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '商品ID',
  `category_id` INT NOT NULL COMMENT '所属分类ID',
  `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
  `description` TEXT COMMENT '商品描述',
  `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
  `stock` INT NOT NULL DEFAULT 0 COMMENT '库存数量',
  `image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片URL',
  `status` INT NOT NULL DEFAULT 1 COMMENT '状态: 1上架, 0下架',
  `sales` INT NOT NULL DEFAULT 0 COMMENT '销量',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上架时间',
  FOREIGN KEY (`category_id`) REFERENCES `category`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================================
-- 购物车表
-- ============================================================
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `quantity` INT NOT NULL DEFAULT 1 COMMENT '购买数量',
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`) ON DELETE CASCADE,
  FOREIGN KEY (`product_id`) REFERENCES `product`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================================
-- 订单表
-- ============================================================
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(50) NOT NULL UNIQUE COMMENT '订单编号',
  `user_id` INT NOT NULL COMMENT '用户ID',
  `total_price` DECIMAL(10,2) NOT NULL COMMENT '订单总金额',
  `status` VARCHAR(20) NOT NULL DEFAULT 'pending' COMMENT '订单状态: pending待付款, shipped待发货, delivered已发货, completed已完成, cancelled已取消',
  `receiver_name` VARCHAR(50) NOT NULL COMMENT '收货人姓名',
  `receiver_phone` VARCHAR(20) NOT NULL COMMENT '收货人电话',
  `address_detail` VARCHAR(500) NOT NULL COMMENT '收货地址',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '订单备注',
  `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '下单时间',
  FOREIGN KEY (`user_id`) REFERENCES `user`(`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================================
-- 订单项表
-- ============================================================
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `id` INT PRIMARY KEY AUTO_INCREMENT COMMENT '订单项ID',
  `order_id` INT NOT NULL COMMENT '所属订单ID',
  `product_id` INT NOT NULL COMMENT '商品ID',
  `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称(快照)',
  `product_image` VARCHAR(500) DEFAULT NULL COMMENT '商品图片(快照)',
  `price` DECIMAL(10,2) NOT NULL COMMENT '购买时单价',
  `quantity` INT NOT NULL COMMENT '购买数量',
  `subtotal` DECIMAL(10,2) NOT NULL COMMENT '小计金额',
  FOREIGN KEY (`order_id`) REFERENCES `orders`(`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ============================================================
-- 预置数据
-- ============================================================

-- 管理员账号 (admin / 123456)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', 'admin', 1);

-- 演示用户 (test / 123456)
INSERT INTO `user` (`username`, `password`, `nickname`, `role`, `status`) VALUES
('test', 'e10adc3949ba59abbe56e057f20f883e', '测试用户', 'user', 1);

-- 示例演示地址
INSERT INTO `address` (`user_id`, `name`, `phone`, `province`, `city`, `district`, `detail`, `is_default`) VALUES
(2, '张三', '13800138000', '广东省', '深圳市', '南山区', '科技园南区A栋1001室', 1);

-- 商品分类
INSERT INTO `category` (`id`, `name`, `description`, `sort_order`) VALUES
(1, '电子产品', '手机、电脑、数码配件等', 1),
(2, '服装鞋帽', '男装、女装、鞋包配饰等', 2),
(3, '食品饮料', '零食、饮料、生鲜等', 3),
(4, '家居用品', '家具、日用、厨具等', 4);

-- 示例商品
INSERT INTO `product` (`category_id`, `name`, `description`, `price`, `stock`, `image`, `status`, `sales`) VALUES
(1, '智能手机 Pro Max', '旗舰级智能手机，高性能处理器，高清摄像头', 6999.00, 100, '/static/images/product-1.svg', 1, 256),
(1, '轻薄笔记本电脑', '14英寸高清屏，16GB内存，512GB固态硬盘', 4999.00, 50, '/static/images/product-2.svg', 1, 128),
(1, '无线蓝牙耳机', '主动降噪，超长续航，舒适佩戴', 699.00, 200, '/static/images/product-3.svg', 1, 512),
(2, '纯棉休闲T恤', '100%纯棉面料，舒适透气，简约百搭', 99.00, 300, '/static/images/product-4.svg', 1, 1024),
(2, '经典牛仔裤', '修身版型，弹力面料，经典百搭', 259.00, 150, '/static/images/product-5.svg', 1, 678),
(2, '轻奢风衣外套', '时尚立领设计，防风防水面料', 899.00, 80, '/static/images/product-6.svg', 1, 345),
(3, '进口坚果礼盒', '精选优质坚果，健康美味', 168.00, 500, '/static/images/product-7.svg', 1, 889),
(3, '有机绿茶礼盒', '高山有机茶叶，清香甘醇', 128.00, 400, '/static/images/product-8.svg', 1, 567),
(3, '手工巧克力礼盒', '进口可可豆制作，丝滑口感', 198.00, 300, '/static/images/product-9.svg', 1, 723),
(4, '简约台灯', 'LED护眼，三档调光，柔和光线', 89.00, 250, '/static/images/product-10.svg', 1, 456),
(4, '真空保温杯', '316不锈钢内胆，保温12小时', 69.00, 350, '/static/images/product-11.svg', 1, 1567),
(4, '记忆棉枕头', '慢回弹记忆棉，人体工学设计', 159.00, 180, '/static/images/product-12.svg', 1, 834);
