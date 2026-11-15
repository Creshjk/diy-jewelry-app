USE diy_jewelry;

ALTER TABLE app_user
    MODIFY COLUMN avatar TEXT;

ALTER TABLE ai_design_record
    MODIFY COLUMN reference_image_url TEXT,
    MODIFY COLUMN result_image_url TEXT NOT NULL;

ALTER TABLE gallery_post
    MODIFY COLUMN cover_image_url TEXT NOT NULL;

ALTER TABLE tutorial
    MODIFY COLUMN cover_image_url TEXT,
    MODIFY COLUMN link_url TEXT;

ALTER TABLE material_recommendation
    MODIFY COLUMN cover_image_url TEXT,
    MODIFY COLUMN link_url TEXT;
