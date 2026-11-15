CREATE DATABASE IF NOT EXISTS diy_jewelry DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE diy_jewelry;

DROP TABLE IF EXISTS login_log;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS gallery_like;
DROP TABLE IF EXISTS gallery_post;
DROP TABLE IF EXISTS ai_design_record;
DROP TABLE IF EXISTS ai_prompt_recommendation;
DROP TABLE IF EXISTS material_recommendation;
DROP TABLE IF EXISTS tutorial;
DROP TABLE IF EXISTS admin_user;
DROP TABLE IF EXISTS app_user;

CREATE TABLE app_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    nickname VARCHAR(64) NOT NULL,
    avatar TEXT,
    gender VARCHAR(20),
    phone VARCHAR(32),
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE admin_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(64) NOT NULL,
    display_name VARCHAR(64) NOT NULL,
    role VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL
);

CREATE TABLE ai_prompt_recommendation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    prompt_text VARCHAR(255) NOT NULL,
    category VARCHAR(64) NOT NULL,
    sort_order INT NOT NULL DEFAULT 0,
    enabled TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL
);

CREATE TABLE ai_design_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    design_mode VARCHAR(32) NOT NULL,
    prompt VARCHAR(255) NOT NULL,
    style_tags VARCHAR(255),
    clarity_level VARCHAR(32),
    aspect_ratio VARCHAR(32),
    reference_image_url TEXT,
    result_image_url TEXT NOT NULL,
    ai_summary TEXT,
    published TINYINT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_design_user(user_id)
);

CREATE TABLE gallery_post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    design_record_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    cover_image_url TEXT NOT NULL,
    description VARCHAR(500),
    like_count INT NOT NULL DEFAULT 0,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    KEY idx_gallery_user(user_id)
);

CREATE TABLE gallery_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL,
    UNIQUE KEY uk_post_user(post_id, user_id)
);

CREATE TABLE tutorial (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(128) NOT NULL,
    cover_image_url TEXT,
    summary VARCHAR(500),
    link_url TEXT,
    content TEXT,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE material_recommendation (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    body_part VARCHAR(32) NOT NULL,
    material_name VARCHAR(64) NOT NULL,
    cover_image_url TEXT,
    description VARCHAR(500),
    link_url TEXT,
    status TINYINT NOT NULL DEFAULT 1,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

CREATE TABLE operation_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    operator_type VARCHAR(32) NOT NULL,
    operator_id BIGINT NOT NULL,
    action_type VARCHAR(64) NOT NULL,
    target_type VARCHAR(64) NOT NULL,
    target_id BIGINT,
    content VARCHAR(500),
    created_at DATETIME NOT NULL
);

CREATE TABLE login_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_type VARCHAR(32) NOT NULL,
    user_id BIGINT NOT NULL,
    username VARCHAR(64) NOT NULL,
    login_ip VARCHAR(64),
    created_at DATETIME NOT NULL
);

INSERT INTO admin_user(username, password, display_name, role, created_at) VALUES
('admin', 'admin123', '系统管理员', 'SUPER_ADMIN', NOW());

INSERT INTO app_user(username, password, nickname, avatar, gender, phone, status, created_at, updated_at) VALUES
('demo', '123456', '饰界体验官', 'https://api.dicebear.com/7.x/notionists/svg?seed=demo', '女', '13800000000', 1, NOW(), NOW()),
('xiaoyu', '123456', '小羽', 'https://api.dicebear.com/7.x/notionists/svg?seed=xiaoyu', '女', '13900000000', 1, NOW(), NOW());

INSERT INTO ai_prompt_recommendation(prompt_text, category, sort_order, enabled, created_at) VALUES
('轻奢珍珠项链，适合春季通勤', '通勤', 1, 1, NOW()),
('国风花丝耳坠，带一点鎏金感', '国风', 2, 1, NOW()),
('适合夏日海边穿搭的贝壳脚链', '海洋', 3, 1, NOW()),
('暗黑银饰戒指，利落高级', '酷感', 4, 1, NOW()),
('森系藤蔓头饰，适合拍照', '森系', 5, 1, NOW());

INSERT INTO tutorial(title, cover_image_url, summary, link_url, content, status, created_at, updated_at) VALUES
('珍珠耳饰基础制作', 'https://images.unsplash.com/photo-1617038260897-41a1f14a0b4a?auto=format&fit=crop&w=900&q=80', '从耳钩、开口圈到珍珠串接的完整入门流程。', 'https://www.bilibili.com', '准备耳钩、开口圈、珍珠、尖嘴钳。先固定主坠，再调整耳线方向，最后检查佩戴重心。', 1, NOW(), NOW()),
('丝线编织手链教程', 'https://images.unsplash.com/photo-1512436991641-6745cdb1723f?auto=format&fit=crop&w=900&q=80', '适合新手的三股编织和收尾技巧。', 'https://www.xiaohongshu.com', '选择不易起毛的蜡线，先完成固定扣，再按三股编法推进，收尾处加入金属吊坠。', 1, NOW(), NOW());

INSERT INTO material_recommendation(body_part, material_name, cover_image_url, description, link_url, status, created_at, updated_at) VALUES
('脖子', '淡水珍珠', 'https://images.unsplash.com/photo-1617038302214-1adff73df0bd?auto=format&fit=crop&w=900&q=80', '适合打造温柔、轻奢的项链作品，搭配银色配件更显干净。', 'https://www.taobao.com', 1, NOW(), NOW()),
('手腕', '蜡绳与金属珠', 'https://images.unsplash.com/photo-1588444650700-6f4f17b6e1f0?auto=format&fit=crop&w=900&q=80', '适合做可调节手链，制作难度低，颜色丰富。', 'https://www.jd.com', 1, NOW(), NOW()),
('头饰', '缎带与人造水晶', 'https://images.unsplash.com/photo-1521572267360-ee0c2909d518?auto=format&fit=crop&w=900&q=80', '适合婚礼和写真场景，注意固定结构强度。', 'https://www.taobao.com', 1, NOW(), NOW()),
('脚腕', '贝壳与不锈钢链', 'https://images.unsplash.com/photo-1506630448388-4e683c67ddb0?auto=format&fit=crop&w=900&q=80', '适合夏季轻量化脚链，注意防汗和防氧化。', 'https://www.jd.com', 1, NOW(), NOW());

INSERT INTO ai_design_record(user_id, design_mode, prompt, style_tags, clarity_level, aspect_ratio, reference_image_url, result_image_url, ai_summary, published, status, created_at, updated_at) VALUES
(1, 'TEXT_TO_IMAGE', '轻奢珍珠锁骨链', '珍珠,金属,清透', '高清', '1:1', '', 'https://picsum.photos/seed/demo-necklace/768/768', '以小颗珍珠和细链组合为主，适合通勤与约会双场景佩戴。', 1, 1, NOW(), NOW()),
(2, 'IMAGE_TO_IMAGE', '新中式耳坠改款', '新中式,花丝,红金', '超清', '3:4', 'https://images.unsplash.com/photo-1617038260897-41a1f14a0b4a?auto=format&fit=crop&w=900&q=80', 'https://picsum.photos/seed/demo-earring/768/768', '保留东方花纹层次，增加垂坠结构与红色点缀，让整体更适合节日搭配。', 0, 1, NOW(), NOW());

INSERT INTO gallery_post(user_id, design_record_id, title, cover_image_url, description, like_count, status, created_at, updated_at) VALUES
(1, 1, '春日轻奢珍珠项链', 'https://picsum.photos/seed/demo-necklace/768/768', '适合白衬衫和针织衫的简约搭配。', 1, 1, NOW(), NOW());

INSERT INTO gallery_like(post_id, user_id, created_at) VALUES
(1, 2, NOW());
